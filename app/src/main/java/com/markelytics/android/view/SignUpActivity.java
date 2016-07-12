package com.markelytics.android.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.markelytics.android.R;
import com.markelytics.android.utils.Constants;
import com.markelytics.android.utils.SharedPref;
import com.markelytics.android.webservice.WebService;
import com.markelytics.android.webservice.WebService.Callback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends Activity implements OnClickListener 
{
	Button btnCancel, btnSignUp;
	TextView txtfields;
	EditText edtEmail, edtPassword, edtReenterPassword, edtPostalCode, edtYearBrth;
	String email, password, confirmPwd, gender, zipCode, yrOfBrth;
	private String netstat = "";
	Dialog activationDialog;
	TextView tv_alert1, tv_alert;
	String fId, fFirstName, fLastName, fEmail, fGender, fname;
	Button loginWithFacebook;
	public static CallbackManager callbackmanager;
	RadioGroup rg;
	RadioButton rd_male, rd_female;
	FontChangeCrawler fontChanger;
	SharedPreferences mPrefs;
	String PREFS = "MyPrefs";

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		FacebookSdk.sdkInitialize(getApplicationContext());

		setContentView(R.layout.sign_up);

		fontChanger = new FontChangeCrawler(getAssets());
		fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

		init();
	}

	private void init()
	{
		// TODO Auto-generated method stub
		btnCancel = (Button)findViewById(R.id.btnCancel);
		btnSignUp = (Button)findViewById(R.id.btnSignUp);
		txtfields = (TextView)findViewById(R.id.txtfields);
		edtEmail = (EditText)findViewById(R.id.edtEmail);

		edtPassword = (EditText)findViewById(R.id.edtPassword);
		edtPostalCode = (EditText)findViewById(R.id.edtPostalCode);
		edtReenterPassword = (EditText)findViewById(R.id.edtReenterPassword);
		edtYearBrth = (EditText)findViewById(R.id.edtYearBrth);
		loginWithFacebook = (Button) findViewById(R.id.loginWithFacebook);
		rg = (RadioGroup)findViewById(R.id.rdg_gender);
		rd_male = (RadioButton)findViewById(R.id.gender_male);
		rd_female = (RadioButton)findViewById(R.id.gender_female);

		loginWithFacebook.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnSignUp.setOnClickListener(this);

	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
			case R.id.btnCancel:

				finish();

				break;

			case R.id.btnSignUp:

				if(validation())
				{
					if(validationFields())
					{
						//Toast.makeText(this, "SIGN UP", Toast.LENGTH_SHORT).show();
						signUp();
					}
				}

				break;

			case R.id.loginWithFacebook:

				onFblogin();

				break;

			default:
				break;
		}
	}


	private void onFblogin()
	{
		callbackmanager = CallbackManager.Factory.create();

		// Set permissions
		LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile"));

		LoginManager.getInstance().registerCallback(callbackmanager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {

						GraphRequest request = GraphRequest.newMeRequest(
								loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
									@Override
									public void onCompleted(JSONObject json, GraphResponse response) {
										if (response.getError() != null) {
											// handle error
											System.out.println("ERROR");
										} else {

											try {

												String jsonresult = String.valueOf(json);

												fId = json.getString("id");
												fEmail = json.getString("email");
												fname = json.getString("name");

												String[] name = fname.split(" ");
												fFirstName = name[0];
												fLastName = name[1];
												fGender = json.getString("gender");
												saveFbLoginCredentials();
												//loginWithFacebook();

											} catch (JSONException e) {
												e.printStackTrace();
											}
										}
									}

								});

						Bundle parameters = new Bundle();
						parameters.putString("fields", "id,name,link,email,gender");

						request.setParameters(parameters);
						request.executeAsync();

					}

					@Override
					public void onCancel() {
						Log.d("TAG_CANCEL", "On cancel");
					}

					@Override
					public void onError(FacebookException error) {

						Log.d("TAG_ERROR", error.toString());
						if (error instanceof FacebookAuthorizationException) {
							if (AccessToken.getCurrentAccessToken() != null) {
								LoginManager.getInstance().logOut();
								onFblogin();
							}
						}
					}
				});
	}


	private void saveFbLoginCredentials() {
		mPrefs = getSharedPreferences(PREFS, MODE_PRIVATE);
		SharedPreferences.Editor e = mPrefs.edit();
		e.putString("fEmail", fEmail);
		e.putString("fGender", fGender);
		e.putString("fFirstName", fFirstName);
		e.putString("fLastName", fLastName);
		e.putString("fId", fId);
		e.commit();

		loginWithFacebook();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		callbackmanager.onActivityResult(requestCode, resultCode, data);
	}



	//method when user login with Facebook account
	private void loginWithFacebook()
	{
		// TODO Auto-generated method stub
		if (netstat.equals("false"))
		{
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					SignUpActivity.this);
			alertDialog
					.setMessage("No network available. Please check the internet connection");
			alertDialog.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.cancel();
						}
					});
			alertDialog.show();
		}
		else
		{

			String gender = "";

			try {

				if(fGender.equalsIgnoreCase("male"))
				{
					gender = "1";

				}
				else if(fGender.equalsIgnoreCase("female"))
				{
					gender = "2";

				}
				WebService service = new WebService(callbackLoginWithFB);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("email", fEmail));
				params.add(new BasicNameValuePair("gender",gender));
				params.add(new BasicNameValuePair("firstname",fFirstName));
				params.add(new BasicNameValuePair("lastname",fLastName));
				params.add(new BasicNameValuePair("panel_id","14"));
				params.add(new BasicNameValuePair("facebook_id",fId));
				params.add(new BasicNameValuePair("registration_id", SharedPref.getPreferences().getString(
						SharedPref.GCMREGISTRATIONID, "")));

				service.getService(SignUpActivity.this, Constants.loginWithFBUrl, params);

			}
			catch (NullPointerException e)
			{
				Log.e("OnClickLogin ex ==", "\n" + e.getMessage());
			}

		}

	}

	Callback callbackLoginWithFB = new Callback() {

		@Override
		public void onSuccess(int reqestcode, JSONObject rootjson)
		{
			// TODO Auto-generated method stub
			JSONObject result;
			String errorCode ;
			String errorMsg ;
			JSONObject resultSet;
			String panelistid;
			try
			{
				result = rootjson.getJSONObject("Result");
				errorCode = result.getString("ErrorCode");
				errorMsg = result.getString("ErrorMessage");

				if(errorCode.equals("0"))
				{
					//After successful login
					resultSet = result.getJSONObject("result");
					panelistid = resultSet.getString("panelist");

					Intent i = new Intent(SignUpActivity.this, MyActivity.class);
					i.putExtra("panelistId", panelistid);
					i.putExtra("panelId","1");
					startActivity(i);

				}
				else if(errorCode.equals("1"))
				{
					//For first time
					Intent i = new Intent(SignUpActivity.this, NewFBUser.class);
					startActivity(i);
					/*activationDialog = new Dialog(SignUpActivity.this);
					activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					activationDialog.setContentView(R.layout.alert);

					fontChanger = new FontChangeCrawler(getAssets());
					fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

					tv_alert = (TextView)activationDialog.findViewById(R.id.txt_alert);
					tv_alert.setText("Success");
					tv_alert1 = (TextView)activationDialog.findViewById(R.id.txt_alert1);
					tv_alert1.setText(errorMsg);

					Button btn_ok = (Button) activationDialog
							.findViewById(R.id.btn_ok);
					btn_ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							activationDialog.cancel();
						}
					});
					activationDialog.show();
					activationDialog.setCancelable(false);*/
				}
				else if(errorCode.equals("3"))
				{
					//please, activate account
					activationDialog = new Dialog(SignUpActivity.this);
					activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					activationDialog.setContentView(R.layout.alert);

					fontChanger = new FontChangeCrawler(getAssets());
					fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

					tv_alert = (TextView)activationDialog.findViewById(R.id.txt_alert);
					tv_alert.setText("Alert");
					tv_alert1 = (TextView)activationDialog.findViewById(R.id.txt_alert1);
					tv_alert1.setText(errorMsg);

					Button btn_ok = (Button) activationDialog
							.findViewById(R.id.btn_ok);
					btn_ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							activationDialog.cancel();
						}
					});
					activationDialog.show();
					activationDialog.setCancelable(false);
				}


			}
			catch (Exception e)
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


	private boolean validation()
	{
		// TODO Auto-generated method stub
		email = edtEmail.getText().toString();
		password = edtPassword.getText().toString();
		confirmPwd = edtReenterPassword.getText().toString();
		yrOfBrth = edtYearBrth.getText().toString();
		zipCode = edtPostalCode.getText().toString();

		if(rd_male.isChecked())
		{
			gender = "1";

		}
		else if(rd_female.isChecked())
		{
			gender = "2";

		}
		if(email.equalsIgnoreCase("") || password.equalsIgnoreCase("") || confirmPwd.equalsIgnoreCase("") ||
				yrOfBrth.equalsIgnoreCase("") || zipCode.equalsIgnoreCase("") || (!rd_female.isChecked() && !rd_male.isChecked()))
		{
			txtfields.setVisibility(View.VISIBLE);
			return false;
		}
		else
		{
			txtfields.setVisibility(View.GONE);
		}


		return true;
	}

	private boolean validationFields()
	{

		if(!isValidEmail(email) && !email.equalsIgnoreCase(""))
		{
			edtEmail.setError("Invalid email");
			return false;
		}
		else if (!isValidPassword(password) && !password.equalsIgnoreCase(""))
		{
			edtPassword.setError("Invalid password. " +
					"Password length should be minimum 6 characters");
			return false;
		}
		else if(!edtPassword.getText().toString().equals(edtReenterPassword.getText().toString())){
			edtReenterPassword.setError("Password not matched");
			return false;
		}
		else if(!yrOfBrth.isEmpty())
		{
			if(!isValidDate(yrOfBrth))
			{
				edtYearBrth.setError("Invalid date");
				return false;
			}

		}

		return true;

	}

	private boolean isValidPassword(String pwd)
	{
		// TODO Auto-generated method stub
		if (pwd != null && pwd.length() >= 6) //make pwd length min 6
		{
			return true;
		}
		return false;
	}

	private boolean isValidEmail(String email) {
		// TODO Auto-generated method stub
		String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();

	}

	private boolean isValidDate(String yrOfBirth2)
	{
		// TODO Auto-generated method stub

		final Calendar c = Calendar.getInstance();
		final int currentYear = c.get(Calendar.YEAR);
		final int currentMonth = c.get(Calendar.MONTH)+1;
		final int currentDay = c.get(Calendar.DAY_OF_MONTH);
		int yr = Integer.parseInt(yrOfBirth2);

		if(yr > (currentYear-100) && yr < (currentYear-14))
		{
			return true;
		}
		return false;
	}

	private void signUp()
	{
		// TODO Auto-generated method stub
		if (netstat.equals("false"))
		{
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
					SignUpActivity.this);
			alertDialog
					.setMessage("No network available. Please check the internet connection");
			alertDialog.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.cancel();
						}
					});
			alertDialog.show();
		}
		else
		{
			try {

					WebService service = new WebService(callback);

					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("email", email));
					params.add(new BasicNameValuePair("password", password));
					params.add(new BasicNameValuePair("year_of_birth", yrOfBrth));
					params.add(new BasicNameValuePair("gender",  gender));
					params.add(new BasicNameValuePair("postalcode", zipCode));
					params.add(new BasicNameValuePair("country_id", "42"));
					params.add(new BasicNameValuePair("panel_id", "14"));

				System.out.println("************SIGN UP*************"+email+"\n"+password+"\n"+yrOfBrth
				+"\n"+gender+"\n"+zipCode);
					service.getService(SignUpActivity.this, Constants.signUpUrl, params);

				}
				catch (NullPointerException e)
				{
					Log.e("OnClickLogin ex ==", "\n" + e.getMessage());
				}

			}

		}


	Callback callback = new Callback() {

		@Override
		public void onSuccess(int reqestcode, JSONObject rootjson)
		{
			// TODO Auto-generated method stub
			JSONObject result;
			String errorCode;
			String errorMsg;

			try
			{
				result = rootjson.getJSONObject("Result");
				errorCode = result.getString("ErrorCode");
				errorMsg = result.getString("ErrorMessage");

				if(errorCode.equals("0"))
				{
					activationDialog = new Dialog(SignUpActivity.this);
					activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					activationDialog.setContentView(R.layout.alert);

					fontChanger = new FontChangeCrawler(getAssets());
					fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

					tv_alert = (TextView)activationDialog.findViewById(R.id.txt_alert);
					tv_alert.setText("Success");
					tv_alert1 = (TextView)activationDialog.findViewById(R.id.txt_alert1);
					tv_alert1.setText(errorMsg);

					Button btn_ok = (Button) activationDialog
							.findViewById(R.id.btn_ok);
					btn_ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							activationDialog.cancel();
							finish();
						}
					});
					activationDialog.show();
					activationDialog.setCancelable(false);
				}
				else if(errorCode.equals("-2"))
				{
					activationDialog = new Dialog(SignUpActivity.this);
					activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					activationDialog.setContentView(R.layout.alert);

					fontChanger = new FontChangeCrawler(getAssets());
					fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

					tv_alert = (TextView)activationDialog.findViewById(R.id.txt_alert);
					tv_alert.setText("Alert");
					tv_alert1 = (TextView)activationDialog.findViewById(R.id.txt_alert1);
					tv_alert1.setText(errorMsg);

					Button btn_ok = (Button) activationDialog
							.findViewById(R.id.btn_ok);
					btn_ok.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							activationDialog.cancel();
						}
					});
					activationDialog.show();
					activationDialog.setCancelable(false);
				}


			}
			catch (Exception e) 
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

	
}
