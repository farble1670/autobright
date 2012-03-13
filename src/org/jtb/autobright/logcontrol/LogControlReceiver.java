package org.jtb.autobright.logcontrol;

import org.jtb.autobright.ALog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LogControlReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		ALog.d("intent: %s", intent);
		
		if (intent.hasExtra(LogControlIntent.EXTRA_LOG_LEVEL)) {
			String l = intent.getStringExtra(LogControlIntent.EXTRA_LOG_LEVEL);
			if (l != null) {
				ALog.Level level = ALog.Level.valueOf(l);
				ALog.setLevel(level);
				ALog.d("set log level: %s", level);
			}
		}
		if (intent.hasExtra(LogControlIntent.EXTRA_LOG_FILE)) {
			boolean lf = intent.getBooleanExtra(
					LogControlIntent.EXTRA_LOG_FILE, false);
			ALog.setFileLogging(lf);
			ALog.d("set file logging: %s", lf);
		}
	}

}
