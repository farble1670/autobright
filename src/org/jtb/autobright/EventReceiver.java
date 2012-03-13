package org.jtb.autobright;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class EventReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		PartialLock.acquire(context);
		Intent serviceIntent = new Intent(context, EventService.class);
		serviceIntent.putExtra("ALARM",
				intent.getBooleanExtra("ALARM", false));
		context.startService(serviceIntent);
	}
}
