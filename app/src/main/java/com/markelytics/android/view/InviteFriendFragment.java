package com.markelytics.android.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.markelytics.android.R;
import com.markelytics.android.network.NetworkCheck;
import com.markelytics.android.utils.Constants;
import com.markelytics.android.utils.SharedPref;
import com.markelytics.android.webservice.WebserviceWithoutProgressBar;
import com.markelytics.android.webservice.WebserviceWithoutProgressBar.CallbackWithoutProgressBar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InviteFriendFragment extends Fragment implements OnClickListener
{
	EditText edt_temail;
	TextView tv_sendInvitation;
	Button btn_invite;
	String email;
	private String netstat = "";
	String panel_id, panelist_id;
	String PREFS = "MyPrefs";
	SharedPreferences mPrefs;
	String signUpLink;
	ImageView img_facebook, img_google, img_linkedin;
	String link;
	Dialog activationDialog;
	boolean Facebook, Linkedin, Google;
	TextView tv_alert1,tv_alert;
	FontChangeCrawler fontChanger;
	Handler progressBarHandler;
	int progressStatus;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 View rootView = inflater.inflate(R.layout.invite_friends, container, false);
         
		 init(rootView);
		 getPanelistId();
	        return rootView;
	}

	private void getPanelistId() 
	{
		// TODO Auto-generated method stub
		panel_id = getArguments().getString("panel_id");   
		panelist_id = getArguments().getString("panelist_id");
		//Toast.makeText(getActivity(), "panel_id"+panel_id+"++++++++"+"panelist_id"+panelist_id, Toast.LENGTH_SHORT).show();

	}

	private void init(View rootView) 
	{
		// TODO Auto-generated method stub
		edt_temail = (EditText)rootView.findViewById(R.id.ed_temail);
		btn_invite = (Button)rootView.findViewById(R.id.btn_invite);
		
		img_facebook = (ImageView) rootView.findViewById(R.id.img_facebook);
		img_linkedin = (ImageView) rootView.findViewById(R.id.img_linkedin);
		img_google = (ImageView) rootView.findViewById(R.id.img_google);

		tv_sendInvitation = (TextView)rootView.findViewById(R.id.txt_sendInvitation);
		edt_temail.setInputType(524288);
		
		btn_invite.setOnClickListener(this);
		edt_temail.setOnClickListener(this);
		img_facebook.setOnClickListener(this);
		img_linkedin.setOnClickListener(this);
		img_google.setOnClickListener(this);

		netstat = NetworkCheck.getNetwork(getActivity());

		rootView.setOnTouchListener(new SwipeTouchListener(getActivity()) {

			public void onSwipeRight() {
				super.onSwipeRight();
				Bundle bundle = new Bundle();
				bundle.putString("panel_id", panel_id);
				bundle.putString("panelist_id", panelist_id);

				Fragment Frag = new RedeemPointsFragment();
				Frag.setArguments(bundle);
				FragmentTransaction ft  = getFragmentManager().beginTransaction();
				ft.replace(R.id.frame_container, Frag);
				ft.addToBackStack(null);
				ft.commit();
			}
			public void onSwipeLeft() {
				super.onSwipeLeft();
				Bundle bundle = new Bundle();
				bundle.putString("panel_id", panel_id);
				bundle.putString("panelist_id", panelist_id);

				Fragment Frag = new ViewProfileFragment();
				Frag.setArguments(bundle);
				FragmentTransaction ft  = getFragmentManager().beginTransaction();
				ft.replace(R.id.frame_container, Frag);
				ft.addToBackStack(null);
				ft.commit();

			}

		});
		
	}

	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{

			case R.id.btn_invite:
				
				sendInvitation();
				
				break;
				
			case R.id.img_facebook:
				
				Facebook = true;
				Google = false;
				Linkedin = false;
				//shareAppLink();
				inviteFriends();
				
				break;
				
			case R.id.img_linkedin:
				Facebook = false;
				Google = false;
				Linkedin = true;
				shareAppLink();
				
				break;
				
			case R.id.img_google:
				Facebook = false;
				Linkedin = false;
				Google = true;
				shareAppLink();
	
				break;
				
			case R.id.txt_sendInvitation:
				
				shareAppLink();
				break;
				
			default:
				break;
		}
	}


	//sendInvitation through android
	private void inviteFriends()
	{
		String appLinkUrl, previewImageUrl;

		appLinkUrl = "https://fb.me/1670609059821753";
		previewImageUrl = "https://www.mypanelhub.com/panel_logo/14/panellogo_14.png";

		if (AppInviteDialog.canShow()) {
			AppInviteContent content = new AppInviteContent.Builder()
					.setApplinkUrl(appLinkUrl)
					.setPreviewImageUrl(previewImageUrl)
					.build();
			AppInviteDialog.show(this, content);
		}
	}

	private void shareAppLink() 
	{
		// TODO Auto-generated method stub
		
		if (netstat.equals("false")) 
		{
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					getActivity());
			alertDialog
					.setMessage("No network available. Please check the internet connection");
			alertDialog.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.cancel();
						}
					});           
			alertDialog.show();
		}
		else 
		{
			try 
				{
					WebserviceWithoutProgressBar service = new WebserviceWithoutProgressBar(
							callbackSignup);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("panel_id",panel_id));
					params.add(new BasicNameValuePair("panelist_id",panelist_id));
					
					service.getService(getActivity(), Constants.signupLink, params);

				} 
				catch (NullPointerException e) 
				{
					Log.e("OnClickLogin =","\n" + e.getMessage());
				}
	
		}
		
	}
	

	CallbackWithoutProgressBar callbackSignup = new CallbackWithoutProgressBar() {

		@Override
		public void onSuccess(int reqestcode, JSONObject rootjson) 
		{
			// TODO Auto-generated method stub
			JSONObject result;
			String errorCode;
			try 
			{
				result = rootjson.getJSONObject("Result");
				errorCode = result.getString("ErrorCode");
				
				if(errorCode.equals("0"))
				{
					signUpLink = result.getString("signuplink");
					SharedPref shared_pref = new SharedPref(getActivity());
					SharedPref.writeString(SharedPref.LINK, signUpLink);
					
				}
				else if(errorCode.equals("2")) 
				{
					
					
				}
			}
			catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			link = SharedPref.getPreferences().getString(
					SharedPref.LINK, "");
			
			
			if(Facebook)
			{
				Facebook = false;
				ShareIntentsApps objShareIntentApps = new ShareIntentsApps();
				objShareIntentApps.shareWithFacebook(getActivity(), link);
			}
			else if(Linkedin)
			{
				Linkedin = false;
				ShareIntentsApps objShareIntentApps = new ShareIntentsApps();
				objShareIntentApps.shareWithLinkedin(getActivity(), link);
			}
			else if(Google)
			{
				Google = false;
				ShareIntentsApps objShareIntentApps = new ShareIntentsApps();
				objShareIntentApps.shareWithGoogle(getActivity(), link);
			}
			
			
		}

		@Override
		public void onError(int reqestcode, String error) 
		{
			// TODO Auto-generated method stub

		}
	};

	
	//To send invitation through email
	private void sendInvitation() 
	{
		// TODO Auto-generated method stub
		mPrefs = getActivity().getSharedPreferences(PREFS, 0);
		panel_id = mPrefs.getString("id", null);

		email = edt_temail.getText().toString();                              
				
		if (netstat.equals("false")) 
		{
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					getActivity());
			alertDialog
					.setMessage("No network available. Please check the internet connection");
			alertDialog.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							dialog.cancel();
						}
					});           
			alertDialog.show();
		}
		else 
		{
			if(validation())
			{
				try 
				{
					WebserviceWithoutProgressBar service = new WebserviceWithoutProgressBar(
							callbackInvite);

					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("email",email));
					params.add(new BasicNameValuePair("panelist_id",panelist_id));
					
					service.getService(getActivity(), Constants.inviteFriends, params);

				} 
				catch (NullPointerException e) 
				{
					Log.e("OnClickLogin  ==",
							"\n" + e.getMessage());
				}
			}
		}
				
	}
	
	CallbackWithoutProgressBar callbackInvite = new CallbackWithoutProgressBar() {

		@Override
		public void onSuccess(int reqestcode, JSONObject rootjson) 
		{
			// TODO Auto-generated method stub
			JSONObject result;
			String errorCode;
			try 
			{
				result = rootjson.getJSONObject("Result");
				errorCode = result.getString("ErrorCode");
				
				activationDialog = new Dialog(getActivity());
				activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				activationDialog.setContentView(R.layout.alert);

				fontChanger = new FontChangeCrawler(getActivity().getAssets());
				fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));
				//int i=0;
				if(errorCode.equals("0"))
				{
					openthread();

				}
				else if(errorCode.equals("1"))
				{
					tv_alert = (TextView)activationDialog.findViewById(R.id.txt_alert);
					tv_alert.setText("Alert");
					tv_alert1 = (TextView)activationDialog.findViewById(R.id.txt_alert1);
					tv_alert1.setText("You have already invited "+edt_temail.getText().toString());

					Button btn_ok = (Button) activationDialog
							.findViewById(R.id.btn_ok);
					btn_ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							activationDialog.cancel();
							edt_temail.setText("");
						}
					});
					activationDialog.show();
					activationDialog.setCancelable(false);
				}
				else if(errorCode.equals("2")) 
				{
					tv_alert = (TextView)activationDialog.findViewById(R.id.txt_alert);
					tv_alert.setText("Alert");
					tv_alert1 = (TextView)activationDialog.findViewById(R.id.txt_alert1);
					tv_alert1.setText("No record found for your search.");
					
					Button btn_ok = (Button) activationDialog
							.findViewById(R.id.btn_ok);
					btn_ok.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							activationDialog.cancel();
							edt_temail.setText("");
						}
					});
					activationDialog.show();
					activationDialog.setCancelable(false);
				}

			}
			catch (JSONException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		@Override
		public void onError(int reqestcode, String error) 
		{
			// TODO Auto-generated method stub

		}
	};


	private void openthread() {

		// TODO Auto-generated method stub
		final ProgressDialog pd= new ProgressDialog(getActivity());

		pd.setProgress(0);
		pd.setCancelable(true);
		pd.setMax(100);
		pd.show();
		progressBarHandler = new Handler();
		progressStatus=10;

		pd.setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				tv_alert = (TextView)activationDialog.findViewById(R.id.txt_alert);
				tv_alert.setText("Success");
				tv_alert1 = (TextView)activationDialog.findViewById(R.id.txt_alert1);
				tv_alert1.setText("Invitation sent successfully.");

				Button btn_ok = (Button) activationDialog
						.findViewById(R.id.btn_ok);
				btn_ok.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						activationDialog.cancel();
						edt_temail.setText("");
					}
				});
				activationDialog.show();
				activationDialog.setCancelable(false);
			}
		});

		new Thread(new Runnable() {

			public void run() {

				while (progressStatus < 100) {

					// do file downnloading task and update progress status
					progressStatus +=10;

					try {
						// pause to see progress
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// Use Handler to update progress in progress dialog
					progressBarHandler.post(new Runnable() {

						public void run() {
							pd.setProgress(progressStatus);
							pd.setTitle("Sending...");
						}
					});


					// Dismiss Progress is progress is complete
					if (progressStatus >= 100) {
						pd.dismiss();

					}
				}

			}

		}).start();
	}



	//validation method for email field
	private boolean validation() 
	{
		// TODO Auto-generated method stub
		
		if(email.equals("") || email == null) 
		{

			Toast.makeText(getActivity(), "Please, Enter email",
					Toast.LENGTH_SHORT).show();
			 return false;
		}
		else if(!isValidEmail(email))
		{
			edt_temail.setError("Invalid email");
			return false;
		}
		return true;
	}
	
	private boolean isValidEmail(String email) 
	{
		// TODO Auto-generated method stub
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
		
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().findViewById(R.id.btnMyAccount).setBackgroundDrawable(getResources().getDrawable(R.drawable.acct));
		getActivity().findViewById(R.id.btnSurvey).setBackgroundDrawable(getResources().getDrawable(R.drawable.survey));
		getActivity().findViewById(R.id.btnInvite).setBackgroundDrawable(getResources().getDrawable(R.drawable.invite_selected));
		getActivity().findViewById(R.id.btnRedeemRewards).setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards));
		getActivity().findViewById(R.id.btnProfile).setBackgroundDrawable(getResources().getDrawable(R.drawable.profile));

		TextView txt = (TextView) getActivity().findViewById(R.id.heading);
		txt.setText("Invite Friends");

		TextView txtInvite = (TextView) getActivity().findViewById(R.id.txtInvite);
		txtInvite.setTextColor(getResources().getColor(R.color.color_wexpand));

		RelativeLayout relativeInvite = (RelativeLayout) getActivity().findViewById(R.id.relativeInvite);
		relativeInvite.setBackgroundColor(getResources().getColor(R.color.white));

		TextView txtSurvey = (TextView) getActivity().findViewById(R.id.txtSurvey);
		txtSurvey.setTextColor(getResources().getColor(R.color.white));

		RelativeLayout relativeSurvey = (RelativeLayout) getActivity().findViewById(R.id.relativeSurvey);
		relativeSurvey.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

		TextView txtRewards = (TextView) getActivity().findViewById(R.id.txtRewards);
		txtRewards.setTextColor(getResources().getColor(R.color.white));

		RelativeLayout relativeRewards = (RelativeLayout) getActivity().findViewById(R.id.relativeRewards);
		relativeRewards.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

		TextView txtAccount = (TextView) getActivity().findViewById(R.id.txtAccount);
		txtAccount.setTextColor(getResources().getColor(R.color.white));

		RelativeLayout relativeAccount = (RelativeLayout) getActivity().findViewById(R.id.relativeAccount);
		relativeAccount.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

		TextView txtProfile = (TextView) getActivity().findViewById(R.id.txtProfile);
		txtProfile.setTextColor(getResources().getColor(R.color.white));

		RelativeLayout relativeProfile = (RelativeLayout) getActivity().findViewById(R.id.relativeProfile);
		relativeProfile.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

		fontChanger = new FontChangeCrawler(getActivity().getAssets());
		fontChanger.replaceFonts((ViewGroup) getActivity().findViewById(android.R.id.content));

	}
}
