package org.jtb.autobright;

import android.content.Context;
import android.provider.Settings;

public class LocationSettings {
	private Context mContext;

	public LocationSettings(Context context) {
		mContext = context;
	}

	public boolean isEnabled(String provider) throws SecurityException {
		boolean on = Settings.Secure.isLocationProviderEnabled(
				mContext.getContentResolver(), provider);
		return on;
	}
}
