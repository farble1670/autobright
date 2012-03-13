package org.jtb.autobright;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

public class WelcomeActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);        
        setContentView(R.layout.welcome);
        
        Intent receiverIntent = new Intent(this, EventReceiver.class);
        sendBroadcast(receiverIntent);
        
        WebView wv = (WebView) findViewById(R.id.web);
        wv.setBackgroundColor(Color.TRANSPARENT);
        wv.loadUrl("file:///android_asset/html/welcome.html");        
    }
}