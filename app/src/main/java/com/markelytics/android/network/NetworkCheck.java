package com.markelytics.android.network;import android.content.Context;import android.net.ConnectivityManager;import android.net.NetworkInfo;import android.net.NetworkInfo.State;import java.util.concurrent.Executors;import java.util.concurrent.ScheduledExecutorService;import java.util.concurrent.TimeUnit;/** * @author Sudhakar Pachava * */public class NetworkCheck {	public static NetworkInfo nwinfo;	static ConnectivityManager connectivity;	static State mobile;	static State wifi;	int count = 0;	static Runnable periodicTask;	static ScheduledExecutorService executor;	static String network = "";	/**	 * This function used to check network availability.	 * 	 * @param context Activity context.	 * @return	 */	public static String getNetwork(Context context) {		connectivity = (ConnectivityManager) context				.getSystemService(Context.CONNECTIVITY_SERVICE);		mobile = connectivity.getNetworkInfo(0).getState();		wifi = connectivity.getNetworkInfo(1).getState();		executor = Executors.newSingleThreadScheduledExecutor();		periodicTask = new Runnable() {			public void run() {				// Invoke method(s) to do the work				nwinfo = connectivity.getActiveNetworkInfo();				if (nwinfo == null)					network = "false";				else if (mobile == NetworkInfo.State.CONNECTED						|| mobile == NetworkInfo.State.CONNECTING)					network = "true";				else if (wifi == NetworkInfo.State.CONNECTED						|| wifi == NetworkInfo.State.CONNECTING)					network = "true";			}		};		executor.scheduleAtFixedRate(periodicTask, 0, 60, TimeUnit.SECONDS);		return network;	}}