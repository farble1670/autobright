package org.jtb.autobright;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class BrightnessPref {
	protected Context mContext;

	protected SharedPreferences mPrefs;

	protected String mKey;

	public BrightnessPref(Context context, String key) {
		mContext = context;
		mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		this.mKey = key;
	}

	@Override
	public abstract String toString();

	public abstract int getDefault();
	
	public int getInt() {
		int def = getDefault();
		return mPrefs.getInt(mKey, def);
	}

	public void setInt(int val) {
		mPrefs.edit().putInt(mKey, val).commit();
	}

	protected static boolean equals(Object A, Object B) {
		return (A == null) ? (B == null) : A.equals(B);
	}
}
