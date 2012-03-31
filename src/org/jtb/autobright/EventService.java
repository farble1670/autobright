package org.jtb.autobright;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import net.sourceforge.zmanim.AstronomicalCalendar;
import net.sourceforge.zmanim.util.GeoLocation;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings.SettingNotFoundException;

public class EventService extends Service {
	private LocationManager mLocationManager;
	private LocationListener mLocationListener = new LocationListener() {
		@Override
		public synchronized void onLocationChanged(Location l) {
			mLocation = l;
			stopSelf();
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private LocationSettings mLocationSettings;
	private AstronomicalCalendar mCal = null;
	private boolean mAlarm = false;
	private Location mLocation = null;
	private Timer mStopTimer = null;

	@Override
	public void onCreate() {
		super.onCreate();
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationSettings = new LocationSettings(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mStopTimer != null) {
			mStopTimer.cancel();
		}
		mLocationManager.removeUpdates(mLocationListener);
		if (mLocation != null) {
			setCalendar(mLocation);
			setBrightness();
		}
		schedule();
		PartialLock.release();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getBooleanExtra("ALARM", false)) {
			mAlarm = true;
		}

		if (!mLocationSettings.isEnabled(LocationManager.NETWORK_PROVIDER)) {
			ALog.w("network provider not enabled, will try to use last known location");
			Location l = getLastKnownLocation();
			if (l != null) {
				mLocation = l;
			} else {
				ALog.w("unable to obtain last known location");
			}
			stopSelf();
		} else {
			mLocationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

			mStopTimer = new Timer();
			mStopTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					// stop after 1 minute, regardless of
					// whether we successfully got the location
					// or not
					stopSelf();
					mStopTimer = null;
				}
			}, 1000 * 60);
		}

		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void setCalendar(Location l) {
		GeoLocation gl = new GeoLocation(null, l.getLatitude(),
				l.getLongitude(), l.getAltitude(), TimeZone.getDefault());
		mCal = new AstronomicalCalendar(gl);
	}

	private void schedule() {
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		if (mCal != null) {
			long triggerAtTime;

			long rise = mCal.getSunrise().getTime();
			long set = mCal.getSunset().getTime();
			long now = System.currentTimeMillis();
			if (now > rise && now < set) {
				triggerAtTime = set;
			} else {
				AstronomicalCalendar ac = new AstronomicalCalendar(
						mCal.getGeoLocation());
				Calendar c = Calendar.getInstance();
				c.add(Calendar.DAY_OF_YEAR, 1);
				ac.setCalendar(c);
				triggerAtTime = ac.getSunrise().getTime();
			}

			// trigger a little after event to avoid
			// race condition
			triggerAtTime += 1000 * 60;

			Intent i = new Intent(this, EventReceiver.class);
			i.putExtra("ALARM", true);
			PendingIntent pi = PendingIntent.getBroadcast(this, 0, i,
					PendingIntent.FLAG_CANCEL_CURRENT);
			am.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
			ALog.d("schedule alarm for: %s",
					DateFormat.getDateTimeInstance(DateFormat.LONG,
							DateFormat.LONG).format(triggerAtTime));
		} else {
			long triggerAtTime = System.currentTimeMillis() + 1000 * 60 * 60; // one
																				// hour

			Intent i = new Intent(this, EventReceiver.class);
			PendingIntent pi = PendingIntent.getBroadcast(this, 0, i,
					PendingIntent.FLAG_CANCEL_CURRENT);
			am.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);
			ALog.d("schedule retry for: %s",
					DateFormat.getDateTimeInstance(DateFormat.LONG,
							DateFormat.LONG).format(triggerAtTime));
		}
	}

	private void setBrightness() {
		long rise = mCal.getSunrise().getTime();
		long set = mCal.getSunset().getTime();
		long now = System.currentTimeMillis();

		int currentBrightness = getBrightness();
		ALog.d("current brightness: %s", currentBrightness);

		if (now > rise && now < set) {
			if (mAlarm) {
				new SetBrightnessPref(this).setInt(currentBrightness);
			} else {
				new RiseBrightnessPref(this).setInt(currentBrightness);
			}

			int riseBrightness = new RiseBrightnessPref(this).getInt();
			setBrightness(riseBrightness);
			ALog.d("set 'rise' brightness: %s", riseBrightness);

		} else {
			if (mAlarm) {
				new RiseBrightnessPref(this).setInt(currentBrightness);
			} else {
				new SetBrightnessPref(this).setInt(currentBrightness);
			}

			int setBrightness = new SetBrightnessPref(this).getInt();
			setBrightness(setBrightness);
			ALog.d("set 'set' brightness: %s", setBrightness);
		}
	}

	private int getBrightness() {
		try {
			int b = android.provider.Settings.System.getInt(
					getContentResolver(),
					android.provider.Settings.System.SCREEN_BRIGHTNESS);
			return b;
		} catch (SettingNotFoundException e) {
			throw new AssertionError(e);
		}
	}

	private void setBrightness(final int val) {
		android.provider.Settings.System.putInt(getContentResolver(),
				android.provider.Settings.System.SCREEN_BRIGHTNESS, val);
	}

	private Location getLastKnownLocation() {
		List<String> providers = mLocationManager.getProviders(true);
		Location bestLocation = null;
		for (String provider : providers) {
			Location l = mLocationManager.getLastKnownLocation(provider);
			ALog.d("last known location, provider: %s, location: %s", provider,
					l);

			if (l == null) {
				continue;
			}
			if (bestLocation == null
					|| l.getAccuracy() < bestLocation.getAccuracy()) {
				ALog.d("found best last known location: %s", l);
				bestLocation = l;
			}
		}
		if (bestLocation == null) {
			return null;
		}
		return bestLocation;
	}
}
