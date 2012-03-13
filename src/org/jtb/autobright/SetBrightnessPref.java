package org.jtb.autobright;

import android.content.Context;

public class SetBrightnessPref extends BrightnessPref {

	public SetBrightnessPref(Context context) {
		super(context, "setBrightness");
	}

	@Override
	public String toString() {
		return Integer.toString(getInt());
	}
	
	@Override
	public int getDefault() {
		return (int)(255.0f * .2f);
	}	
}
