package org.jtb.autobright;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "dGZnU0IxY3NPWUs3UEkxYnp0NHg5aUE6MQ") 
public class AutobrightApplication extends Application {
	static {
		ALog.setTag("autobright");
		ALog.setLevel(ALog.Level.W);
		ALog.setFileLogging(false);		
	}
	
	@Override
	public void onCreate() {
		// The following line triggers the initialization of ACRA
		ACRA.init(this);
		
	}
}
