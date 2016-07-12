package com.markelytics.android.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.Toast;

import com.markelytics.android.utils.Constants;
import com.markelytics.android.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

public class ShareIntentsApps 
{
public void shareApps(Activity activity){
		
		ShareIntentMethods objShareIntentMethods = new ShareIntentMethods();

		List<Intent> targetedShareIntents = new ArrayList<Intent>();

		Intent facebookIntent = objShareIntentMethods.getShareIntent(activity, "facebook.katana", Constants.shareSubject, SharedPref.getPreferences().getString(SharedPref.LINK, ""));
		if(facebookIntent != null)
			targetedShareIntents.add(facebookIntent);
		
		
		Intent linkedInIntent = objShareIntentMethods.getShareIntent(activity, "com.linkedin.android", Constants.shareSubject, SharedPref.getPreferences().getString(SharedPref.LINK, ""));
		if(linkedInIntent != null)
			targetedShareIntents.add(linkedInIntent);
		
		
		Intent googlePlusIntent = objShareIntentMethods.getShareIntent(activity, "com.google.android.apps.plus", Constants.shareSubject, SharedPref.getPreferences().getString(SharedPref.LINK, ""));
		if(googlePlusIntent != null)
			targetedShareIntents.add(googlePlusIntent);

		/*Intent twitterIntent = objShareIntentMethods.getShareIntent(activity, "twitter", Constant.shareSubject, Constant.shareTwitterMessage);
		if(twitterIntent != null)
			targetedShareIntents.add(twitterIntent);

		Intent gmailIntent = objShareIntentMethods.getShareIntent(activity, "gmail", Constant.shareSubject, Constant.shareBody);
		if(gmailIntent != null)
			targetedShareIntents.add(gmailIntent);
		
		Intent hangOutIntent = objShareIntentMethods.getShareIntent(activity, "talk", Constant.shareSubject, Constant.shareMessageBody);
		if(hangOutIntent != null)
			targetedShareIntents.add(hangOutIntent);
		
		Intent messageIntent = objShareIntentMethods.getShareIntent(activity, "mms", Constant.shareSubject, Constant.shareMessageBody);
		if(messageIntent != null)
			targetedShareIntents.add(messageIntent);
		
		Intent messageSonyIntent = objShareIntentMethods.getShareIntent(activity, "conversations", Constant.shareSubject, Constant.shareMessageBody);
		if(messageSonyIntent != null)
			targetedShareIntents.add(messageSonyIntent);

		Intent emailIntent = objShareIntentMethods.getShareIntent(activity, "email", Constant.shareSubject, Constant.shareBody);
		if(emailIntent != null)
			targetedShareIntents.add(emailIntent);*/
		
		Intent chooser = Intent.createChooser(targetedShareIntents.remove(0), "Share via");

		chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));

		activity.startActivity(chooser);
	}

public void shareWithFacebook(Activity activity, String link){
	ShareIntentMethods objShareIntentMethods = new ShareIntentMethods();
	List<Intent> targetedShareIntents = new ArrayList<Intent>();
	Intent facebookIntent = objShareIntentMethods.getShareIntent(activity, "facebook.katana", Constants.shareSubject, link);

	if(facebookIntent != null){
	
		targetedShareIntents.add(facebookIntent);
		
		Intent chooser = Intent.createChooser(targetedShareIntents.remove(0), "Share via");
		chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
		activity.startActivity(chooser);
		
	}
	else 
		Toast.makeText(activity, "Facebook App is not Installed on your device", Toast.LENGTH_LONG).show();


}

public void shareWithGoogle(Activity activity, String link){
	ShareIntentMethods objShareIntentMethods = new ShareIntentMethods();
	List<Intent> targetedShareIntents = new ArrayList<Intent>();
	
	Intent googlePlusIntent = objShareIntentMethods.getShareIntent(activity, "com.google.android.apps.plus", Constants.shareSubject, link);
	if(googlePlusIntent != null){
	
		targetedShareIntents.add(googlePlusIntent);
		
		Intent chooser = Intent.createChooser(targetedShareIntents.remove(0), "Share via");
		chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
		activity.startActivity(chooser);
		
	}
	else 
		Toast.makeText(activity, "Google+ App is not Installed on your device", Toast.LENGTH_LONG).show();


}

public void shareWithLinkedin(Activity activity, String link){
	ShareIntentMethods objShareIntentMethods = new ShareIntentMethods();
	List<Intent> targetedShareIntents = new ArrayList<Intent>();
	
	
	Intent linkedInIntent = objShareIntentMethods.getShareIntent(activity, "com.linkedin.android", Constants.shareSubject, link);
	if(linkedInIntent != null){
	
		targetedShareIntents.add(linkedInIntent);
		
		Intent chooser = Intent.createChooser(targetedShareIntents.remove(0), "Share via");
		chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
		activity.startActivity(chooser);
		
	}
	else 
		Toast.makeText(activity, "Linkedin App is not Installed on your device", Toast.LENGTH_LONG).show();


}

}
