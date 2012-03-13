package org.jtb.autobright;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class BrightnessActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int brightness = getIntent().getIntExtra("BRIGHTNESS", -1);
		if (brightness != -1) {
			setBrightness(brightness);
		}
		finish();
	}

	private void setBrightness(int b) {
		setBrightness(b / 255.0f);
	}
	
	private void setBrightness(final float b) {
		WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
		layoutParams.screenBrightness = b;
		getWindow().setAttributes(layoutParams);
	}
}