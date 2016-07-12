package com.markelytics.android.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.markelytics.android.R;
import com.markelytics.android.utils.Constants;
import com.markelytics.android.utils.SharedPref;
import com.markelytics.android.webservice.WebService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewFBUser extends AppCompatActivity  {

    String email, password, confirmPassword, yrOfBirth, postalCode;
    EditText ed_password, ed_confirmPassword, ed_postalCode, ed_yrofBirth, ed_email;
    Button btnJoin;
    TextView txtfields;
    SharedPreferences mPrefs;
    private String netstat = "";
    String PREFS = "MyPrefs";
    Dialog activationDialog;
    FontChangeCrawler fontChanger;
    TextView tv_alert1, tv_alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_fbuser);

        init();

    }

    private void init() {
        ed_email = (EditText) findViewById(R.id.ed_email);
        ed_password = (EditText) findViewById(R.id.ed_password);
        ed_confirmPassword = (EditText) findViewById(R.id.ed_confirmPassword);
        ed_postalCode = (EditText) findViewById(R.id.ed_postalCode);
        ed_yrofBirth = (EditText) findViewById(R.id.ed_yrofBirth);
        txtfields = (TextView) findViewById(R.id.txtfields);

        mPrefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if(mPrefs.getString("fEmail", null).equalsIgnoreCase("null") || mPrefs.getString("fEmail", null).equalsIgnoreCase("")) {
            ed_email.setVisibility(View.VISIBLE);
        }

        btnJoin = (Button)findViewById(R.id.btnJoin);
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    if (validationFields()) {
                        join();
                    }
                }
            }
        });


    }

    private void join() {
        if (netstat.equals("false"))
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    NewFBUser.this);
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

                mPrefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                String gender ="";
                if(mPrefs.getString("fGender", null).equalsIgnoreCase("male"))
                {
                    gender = "1";

                }
                else if(mPrefs.getString("fGender", null).equalsIgnoreCase("female"))
                {
                    gender = "2";

                }

                WebService service = new WebService(callbackFirstLoginWithFB);

                List<NameValuePair> params = new ArrayList<NameValuePair>();

                if(ed_email.getVisibility() == 0)
                {
                    email = ed_email.getText().toString();
                }
                else
                {
                    email = mPrefs.getString("fEmail", null);
                }

                params.add(new BasicNameValuePair("email",email ));
                params.add(new BasicNameValuePair("gender",gender));
                params.add(new BasicNameValuePair("firstname",mPrefs.getString("fFirstName", null)));
                params.add(new BasicNameValuePair("lastname",mPrefs.getString("fLastName", null)));
                params.add(new BasicNameValuePair("panel_id", "14"));
                params.add(new BasicNameValuePair("facebook_id", mPrefs.getString("fId", null)));
                params.add(new BasicNameValuePair("year_of_birth",ed_yrofBirth.getText().toString()));
                params.add(new BasicNameValuePair("zip_code", ed_postalCode.getText().toString()));
                params.add(new BasicNameValuePair("password",ed_password.getText().toString()));
                params.add(new BasicNameValuePair("registration_id", SharedPref.getPreferences().getString(
                        SharedPref.GCMREGISTRATIONID, "")));

                System.out.println("*****SIGN UP NEW*****" + email + "\n" + gender + "\n" + mPrefs.getString("fFirstName", null) + "\n" +
                        mPrefs.getString("fLastName", null) + "\n" + mPrefs.getString("fId", null) + "\n" + ed_yrofBirth.getText().toString() + "\n" +
                        ed_postalCode.getText().toString() + "\n" + ed_password.getText().toString());

                service.getService(NewFBUser.this, Constants.facebookRegister, params);

            }
            catch (NullPointerException e)
            {
                Log.e("OnClickLogin ex ==", "\n" + e.getMessage());
            }

        }


    }


    WebService.Callback callbackFirstLoginWithFB = new WebService.Callback() {

        @Override
        public void onSuccess(int reqestcode, JSONObject rootjson)
        {
            // TODO Auto-generated method stub
            System.out.println("*****FirstLoginWithFB JSON*****"+rootjson);
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


                }
                else if(errorCode.equals("1"))
                {
                    //For first time

					activationDialog = new Dialog(NewFBUser
                            .this);
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
					btn_ok.setOnClickListener(new View.OnClickListener() {

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
                else if(errorCode.equals("3")) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_fbuser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Validation for username & password
    private boolean validation()
    {
        // TODO Auto-generated method stub
       // username = et_username.getText().toString();
        email = ed_email.getText().toString();
        password = ed_password.getText().toString();
        confirmPassword = ed_confirmPassword.getText().toString();
        yrOfBirth = ed_yrofBirth.getText().toString();
        postalCode = ed_postalCode.getText().toString();

        if(ed_email.getVisibility() == 0)
        {
            if(email.equalsIgnoreCase(""))
            {
                txtfields.setVisibility(View.VISIBLE);
                return false;
            }
        }
         if(password.equalsIgnoreCase("") || confirmPassword.equalsIgnoreCase("") || yrOfBirth.equalsIgnoreCase("") || postalCode.equalsIgnoreCase("") )
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


    private boolean validationFields() {
        if (!isValidPassword(password))
        {
            ed_password.setError("Invalid password. " +
                    "Password length should be minimum 6 characters");
            return false;
        }
        else if(!password.equals(confirmPassword))
        {
            System.out.println("++++++++"+password+"----"+confirmPassword);
            ed_confirmPassword.setError("Password mismatched");
            return false;

        }
        else if(!ed_yrofBirth.getText().toString().isEmpty())
        {
            if(!isValidDate(ed_yrofBirth.getText().toString()))
            {
                System.out.println("************");
                ed_yrofBirth.setError("Invalid date");
                return false;
            }

        }

        if(ed_email.getVisibility() == 0)
        {
            if(!isValidEmail(email)) {
                ed_email.setError("Invalid email");
                return false;
            }

        }
        return true;
    }

    private boolean isValidEmail(String email) {
        // TODO Auto-generated method stub
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }


    //For cheking validation of date field
    private boolean isValidDate(String yrOfBirth2)
    {
        // TODO Auto-generated method stub
        final Calendar c = Calendar.getInstance();
        final int currentYear = c.get(Calendar.YEAR);
        final int currentMonth = c.get(Calendar.MONTH)+1;
        final int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int yr = Integer.parseInt(yrOfBirth2);
        //Log.v("year:"+currentYear+"month:"+currentMonth+"day:"+currentDay, "---msg---");
        if(yr > (currentYear-100) && yr < (currentYear-14))
        {
            return true;
        }
        return false;
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



}
