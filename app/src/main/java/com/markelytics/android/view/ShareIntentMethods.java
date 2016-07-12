package com.markelytics.android.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import java.util.List;

public class ShareIntentMethods 
{
	public Intent getShareIntent( Activity activity,String type, String subject, String text) 
	{
	    boolean found = false;
	    Intent share = new Intent(Intent.ACTION_SEND);
	    share.setType("text/plain");

	    // gets the list of intents that can be loaded.
	    List<ResolveInfo> resInfo = activity.getPackageManager().queryIntentActivities(share, 0);
	    System.out.println("resinfo: " + resInfo);
	    if (!resInfo.isEmpty()){
	        for (ResolveInfo info : resInfo) {
	            if (info.activityInfo.packageName.toLowerCase().contains(type) || 
	                    info.activityInfo.name.toLowerCase().contains(type) ) {
	                share.putExtra(Intent.EXTRA_SUBJECT,  subject);
	                share.putExtra(Intent.EXTRA_TEXT,     text);
	                share.setPackage(info.activityInfo.packageName);
	                found = true;
	                break;
	            }
	        }
	        if (!found)
	            return null;

	        return share;
	    }
	    return null;
	}
}
