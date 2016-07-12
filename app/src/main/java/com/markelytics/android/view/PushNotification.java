package com.markelytics.android.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.google.android.gms.gcm.GcmListenerService;
import com.markelytics.android.R;
import com.markelytics.android.utils.SharedPref;


/**
 * Created by SYSTEM on 12/5/2015.
 */
public class PushNotification extends GcmListenerService {

    Context context;

   @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");

        System.out.println("Notification Mesaage======="+message);

        createNotification(message);
    }




    private static void createNotification(String message) {
        SharedPreferences mPrefs;
        String PREFS = "MyPrefs";

            Context context = MyApplication.getContext();
            int icon = R.mipmap.logo_zippy;
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification(icon, message, when);
            String title = context.getString(R.string.app_name);

            //SharedPref sharedPref = new SharedPref(context);
            //SharedPreferences preferences = SharedPref.getPreferences();

            SharedPref pref = new SharedPref(context);
            mPrefs = pref.getPreferences();

            boolean rememberMe = mPrefs.getBoolean("rememberMe", false);

            Intent notificationIntent = null;

            if(rememberMe)
            {
                notificationIntent = new Intent(context, MyActivity.class);
            }
            else
            {
                notificationIntent = new Intent(context, LoginActivity.class);
            }

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.mipmap.logo_zippy)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(intent);

            notification = builder.getNotification();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notificationManager.notify(R.mipmap.logo_zippy, notification);
        }








}
