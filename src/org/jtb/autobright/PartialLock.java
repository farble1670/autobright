package org.jtb.autobright;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class PartialLock {
	private static PowerManager.WakeLock lock;

	private static PowerManager.WakeLock getLock(Context context) {
		if (lock == null) {
			PowerManager mgr = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);

			lock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
					"org.jtb.autobright.lock");
		}
		return lock;
	}

	public static synchronized void acquire(Context context) {
		WakeLock wakeLock = getLock(context);
		wakeLock.acquire();
		ALog.d("wake lock acquired");
	}

	public static synchronized void release() {
		if (lock == null) {
			ALog.w("release attempted, but wake lock was null");
		} else {
			if (lock.isHeld()) {
				lock.release();
				ALog.d("wake lock released");
			} else {
				ALog.w("release attempted, but wake lock was not held");
			}
		}
	}
}
