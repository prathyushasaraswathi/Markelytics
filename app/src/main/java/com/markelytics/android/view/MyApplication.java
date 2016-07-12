package com.markelytics.android.view;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
	
	private static boolean activityVisible = false;
	private static Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
	}

	public static Context getContext() {
		return mContext;
	}

	public static boolean isActivityVisible() {
	    return activityVisible;
	  }  

	  public static void activityResumed() {
	    activityVisible = true;
	  }

	  public static void activityPaused() {
	    activityVisible = true;
	  }

	  public static void activityStop() {
		    activityVisible = false;
		  }
	  
	  public static void activityFinish() {
		    activityVisible = false;
		  }
	}
