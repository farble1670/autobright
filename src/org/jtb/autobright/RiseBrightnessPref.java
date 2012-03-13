package org.jtb.autobright;

import android.content.Context;

public class RiseBrightnessPref extends BrightnessPref {

	public RiseBrightnessPref(Context context) {
		super(context, "riseBrightness");
	}

	@Override
	public String toString() {
		return Integer.toString(getInt());
	}

	@Override
	public int getDefault() {
		return (int)(255.0f * .8f);
	}
}
