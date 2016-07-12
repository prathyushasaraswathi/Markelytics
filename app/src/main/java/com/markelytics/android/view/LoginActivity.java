package com.markelytics.android.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;


import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.markelytics.android.R;
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

import com.markelytics.android.controller.PanellistAdapter;
import com.markelytics.android.model.PanelDetails;
import com.markelytics.android.network.NetworkCheck;
import com.markelytics.android.utils.Constants;
import com.markelytics.android.utils.SharedPref;
import com.markelytics.android.webservice.WebService;
import com.markelytics.android.webservice.WebService.Callback;
import com.markelytics.android.webservice.WebserviceWithoutProgressBar;
import com.markelytics.android.webservice.WebserviceWithoutProgressBar.CallbackWithoutProgressBar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends Activity implements OnClickListener, OnItemClickListener {
    EditText et_username, et_password, et_email;
    TextView tv_forgotPwd, txt_signUp, tv_alert1, tv_alert;
    Button btn_login;
    Button loginWithFacebook;
    Dialog activationDialog;
    String username, password, email;
    String PREFS = "MyPrefs";
    ListView panelListview;
    ListView frgtPanelListView;
    ArrayList<PanelDetails> list;
    PanellistAdapter adapter;
    CheckBox checkBoxPwd;
    SharedPreferences mPrefs;
    private String netstat = "";
    ArrayList<PanelDetails> panelList;
    ArrayList<PanelDetails> frgtPanelList;
    String panelName, panelId, panelistId;
    String frgtPanelId, frgtPanelName;
    String fId, fFirstName, fLastName, fEmail, fGender, fname;
    FontChangeCrawler fontChanger;
    Context context;

    public static CallbackManager callbackmanager;

    String PROJECT_NUMBER = "627105019996";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize SDK before setContentView(Layout ID)
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.login_screen);

        fontChanger = new FontChangeCrawler(getAssets());
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));

        init();

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            System.exit(0);
        }

        if (getIntent().getBooleanExtra("LOGOUT", false)) {
            finish();
            System.exit(0);
        }


        //Get saved login credentials from sharedpreference file if any
        mPrefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        boolean rememberMe = mPrefs.getBoolean("rememberMe", false);
        if (rememberMe) {
            SharedPreferences myPrefs = getSharedPreferences("onetime", MODE_PRIVATE);
            String username = myPrefs.getString("FIRSTTIME", null);
            if (username == null) {
                SharedPreferences.Editor e =mPrefs.edit();
                e.remove("rememberMe");
                e.remove("username");
                e.remove("password");
                e.commit();
            }
            else{
                panelistId = mPrefs.getString("panelist_id", null);
                panelId = mPrefs.getString("panel_id", null);

                Intent i = new Intent(this, MyActivity.class);
                i.putExtra("panelist_id", panelistId);
                i.putExtra("panel_id", panelId);
                startActivity(i);
            }
        }

    }

    private void init() {
        // TODO Auto-generated method stub
        tv_forgotPwd = (TextView) findViewById(R.id.txt_forgotPwd);
        txt_signUp = (TextView) findViewById(R.id.txt_signUp);
        et_username = (EditText) findViewById(R.id.ed_username);
        et_password = (EditText) findViewById(R.id.ed_password);
        loginWithFacebook = (Button) findViewById(R.id.loginWithFacebook);

        checkBoxPwd = (CheckBox) findViewById(R.id.checkBoxPwd);
        btn_login = (Button) findViewById(R.id.btn_login);

        loginWithFacebook.setOnClickListener(this);
        tv_forgotPwd.setOnClickListener(this);
        txt_signUp.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        list = new ArrayList<PanelDetails>();
        netstat = NetworkCheck.getNetwork(getApplicationContext());

        panelList = new ArrayList<PanelDetails>();
        frgtPanelList = new ArrayList<PanelDetails>();

        SharedPref shared_pref = new SharedPref(LoginActivity.this);   /// test comment

        if (SharedPref.getPreferences().getString(
                SharedPref.GCMREGISTRATIONID, "").equalsIgnoreCase("")) {

            GCMClientManager pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
            pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
                @Override
                public void onSuccess(String registrationId, boolean isNewRegistration) {


                    SharedPref.writeString(SharedPref.GCMREGISTRATIONID, registrationId);

                    System.out.println("We Written Registration id========" + registrationId);
                    //send this registrationId to your server
                }

                @Override
                public void onFailure(String ex) {
                    super.onFailure(ex);
                }
            });

        } else {
            System.out.println("Already Registered========" + SharedPref.getPreferences().getString(SharedPref.GCMREGISTRATIONID, ""));

        }


        checkBoxPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_login:

                loginCredentials();

                break;

            case R.id.txt_forgotPwd:

                forgotPassword();

                break;

            case R.id.txt_signUp:

                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);

                break;

            case R.id.loginWithFacebook:

                onFblogin();

                break;

            default:
                break;
        }

    }


    private void onFblogin() {
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
                                                System.out.println("JSON Result" + jsonresult);

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

    //Method for forgot password after selecting panel
    private void forgotPassword() {
        // TODO Auto-generated method stub
        activationDialog = new Dialog(LoginActivity.this);
        activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //for making transparent dialog background
        activationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        activationDialog.setContentView(R.layout.popup_forgetpassword);

        fontChanger = new FontChangeCrawler(getAssets());
        fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

        Button btn_submit = (Button) activationDialog
                .findViewById(R.id.btnSubmit);

        et_email = (EditText) activationDialog
                .findViewById(R.id.ed_femail);


        btn_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (netstat.equals("false")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            LoginActivity.this);
                    alertDialog
                            .setMessage("No network available. Please, check the internet connection");
                    alertDialog.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                } else {
                    if (validationFrgtPwd()) {
                        try {
                            WebserviceWithoutProgressBar service = new WebserviceWithoutProgressBar(
                                    callbackForgotPassword);

                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair("email", email));
                            params.add(new BasicNameValuePair("panel_id", "14"));

                            service.getService(LoginActivity.this,
                                    Constants.forgotPasswordUrl, params);

                        } catch (NullPointerException e) {
                            Log.e("OnClickLogin ex ==",
                                    "\n" + e.getMessage());
                        }


                        activationDialog.cancel();

                    }
                }
            }

        });
        activationDialog.show();
    }

    //Validation of email field in forgot password
    protected boolean validationFrgtPwd() {
        // TODO Auto-generated method stub
        email = et_email.getText().toString();

        if (email.equals("") || email == null) {
            return false;
        } else if (!isValidEmail(email)) {
            et_email.setError("Invalid email");
            return false;
        }
        return true;

    }


    CallbackWithoutProgressBar callbackForgotPassword = new CallbackWithoutProgressBar() {

        @Override
        public void onSuccess(int reqestcode, JSONObject rootjson) {
            // TODO Auto-generated method stub
            JSONObject result;
            String errorCode;

            try {
                result = rootjson.getJSONObject("Result");
                errorCode = result.getString("ErrorCode");

                if (errorCode.equals("0")) {
                    activationDialog = new Dialog(LoginActivity.this);
                    activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    activationDialog.setContentView(R.layout.alert);

                    fontChanger = new FontChangeCrawler(getAssets());
                    fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

                    tv_alert = (TextView) activationDialog.findViewById(R.id.txt_alert);
                    tv_alert.setText("Success");
                    tv_alert1 = (TextView) activationDialog.findViewById(R.id.txt_alert1);
                    tv_alert1.setText("The forgot password link has been sent to your email successfully.");

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
                } else if (errorCode.equals("-1")) {
                    activationDialog = new Dialog(LoginActivity.this);
                    activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    activationDialog.setContentView(R.layout.alert);

                    fontChanger = new FontChangeCrawler(getAssets());
                    fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

                    tv_alert = (TextView) activationDialog.findViewById(R.id.txt_alert);
                    tv_alert.setText("Alert");
                    tv_alert1 = (TextView) activationDialog.findViewById(R.id.txt_alert1);
                    tv_alert1.setText("Email you entered is incorrect");

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
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        @Override
        public void onError(int reqestcode, String error) {
            // TODO Auto-generated method stub

        }
    };


    private void loginCredentials() {
        // TODO Auto-generated method stub
        panelList.clear();
        if (netstat.equals("false")) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    LoginActivity.this);
            alertDialog
                    .setMessage("No network available. Please check the internet connection");
            alertDialog.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
        } else {
            if (validation()) {

                if ((!(et_username.getText().toString().equals("")) && !(et_password
                        .getText().toString().equals("")))) {

                    saveLoginCredentials();
                } else {
                    removeLoginCredentials();
                }

                try {

                    mPrefs = getSharedPreferences(PREFS, MODE_PRIVATE);

                    WebService service = new WebService(callback);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("email", username));
                    params.add(new BasicNameValuePair("password", password));
                    params.add(new BasicNameValuePair("registration_id", SharedPref.getPreferences().getString(
                            SharedPref.GCMREGISTRATIONID, "")));
                    params.add(new BasicNameValuePair("panel_id", "14"));

                    service.getService(LoginActivity.this, Constants.loginUrl,
                            params);

                } catch (NullPointerException e) {
                    Log.e("OnClickLogin ex ==", "\n" + e.getMessage());
                }

            }
        }

    }

    //Select panel for forgot password
    protected void selectPanelForFrgtPwd() {
        // TODO Auto-generated method stub
        if (frgtPanelList.size() == 1) {
            if (netstat.equals("false")) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        LoginActivity.this);
                alertDialog
                        .setMessage("No network available. Please, check the internet connection");
                alertDialog.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            } else {

                try {
                    WebserviceWithoutProgressBar service = new WebserviceWithoutProgressBar(
                            callbackForgotPassword);

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("email", email));
                    params.add(new BasicNameValuePair("panel_id", "14"));

                    service.getService(LoginActivity.this,
                            Constants.forgotPasswordUrl, params);

                } catch (NullPointerException e) {
                    Log.e("OnClickLogin ex ==",
                            "\n" + e.getMessage());
                }

            }
        } else {

            activationDialog = new Dialog(LoginActivity.this);
            activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //for transparent dialog parent layout
            activationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
            activationDialog.setContentView(R.layout.popup_panelselection);

            fontChanger = new FontChangeCrawler(getAssets());
            fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

            frgtPanelListView = (ListView) activationDialog.findViewById(R.id.listview_panellist);

            adapter = new PanellistAdapter(LoginActivity.this, R.layout.content_panellist_listview, frgtPanelList);
            frgtPanelListView.setAdapter(adapter);
            activationDialog.show();

            frgtPanelListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    TextView panelName = (TextView) v.findViewById(R.id.listview_panelname);
                    TextView panelId = (TextView) v.findViewById(R.id.listview_panelid);

                    frgtPanelName = panelName.getText().toString();
                    frgtPanelId = panelId.getText().toString();

                    try {
                        WebserviceWithoutProgressBar service = new WebserviceWithoutProgressBar(
                                callbackForgotPassword);

                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("email", email));
                        params.add(new BasicNameValuePair("panel_id", "14"));

                        service.getService(LoginActivity.this,
                                Constants.forgotPasswordUrl, params);

                    } catch (NullPointerException e) {
                        Log.e("OnClickLogin ex ==",
                                "\n" + e.getMessage());
                    }
                    activationDialog.cancel();
                }
            });

        }
    }

    //Validation for username & password
    private boolean validation() {
        // TODO Auto-generated method stub
        username = et_username.getText().toString();
        password = et_password.getText().toString();

        if (username.equals("") || username == null) {
            // et_username.setError("Please, Enter username");
            Toast.makeText(this, "Please, Enter username",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.equals("") || password == null) {

            Toast.makeText(this, "Please, Enter password",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidPassword(password)) {
            et_password.setError("Invalid password. " +
                    "Password length should be minimum 6 characters");
            return false;
        } else if (!isValidEmail(username)) {
            et_username.setError("Invalid email");
            return false;
        }
        return true;
    }

    //callback for login
    Callback callback = new Callback() {

        @Override
        public void onSuccess(int reqestcode, JSONObject rootjson) {
            // TODO Auto-generated method stub
            JSONObject result;
            JSONArray resulset;
            String errorCode;
            String errorMsg;
            try {
                result = rootjson.getJSONObject("Result");
                errorCode = result.getString("ErrorCode");
                errorMsg = result.getString("ErrorMessage");

                if (errorCode.equals("0")) {
                    resulset = result.getJSONArray("result");
                    int lenght = resulset.length();

                    for (int i = 0; i < lenght; i++) {
                        panelistId = resulset.getJSONObject(i).getString("panelist_id");
                        panelId = resulset.getJSONObject(i).getString("panel_id");
                        panelName = resulset.getJSONObject(i).getString("panel_name");
                        String details = " " + panelistId + " " + panelId + " " + panelName;


                        mPrefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor e = mPrefs.edit();
                        e.putString("id", panelistId);
                        e.putString("panel_id", panelId);
                        e.putString("panel_name", panelName);
                        e.commit();

                        PanelDetails panelDetail = new PanelDetails();
                        panelDetail.setPanelId(panelId);
                        panelDetail.setPanelistId(panelistId);
                        panelDetail.setPanelName(panelName);

                        panelList.add(panelDetail);


                    }

                    selectPanel();

                } else if (errorCode.equals("2")) {
                    Toast.makeText(LoginActivity.this, "Record not found", Toast.LENGTH_SHORT).show();
                } else if (errorCode.equals("3")) {
                    activationDialog = new Dialog(LoginActivity.this);
                    activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    activationDialog.setContentView(R.layout.alert);

                    fontChanger = new FontChangeCrawler(getAssets());
                    fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

                    tv_alert = (TextView) activationDialog.findViewById(R.id.txt_alert);
                    tv_alert.setText("Alert");

                    tv_alert1 = (TextView) activationDialog.findViewById(R.id.txt_alert1);
                    tv_alert1.setText(errorMsg);

                    Button btn_ok = (Button) activationDialog
                            .findViewById(R.id.btn_ok);
                    btn_ok.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            activationDialog.cancel();
                            et_username.setText("");
                            et_password.setText("");
                        }
                    });
                    activationDialog.show();
                    activationDialog.setCancelable(false);
                } else if (errorCode.equals("-5")) {
                    activationDialog = new Dialog(LoginActivity.this);
                    activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    activationDialog.setContentView(R.layout.alert);

                    fontChanger = new FontChangeCrawler(getAssets());
                    fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

                    tv_alert = (TextView) activationDialog.findViewById(R.id.txt_alert);
                    tv_alert.setText("Alert");

                    tv_alert1 = (TextView) activationDialog.findViewById(R.id.txt_alert1);
                    tv_alert1.setText("The email or password you have entered is incorrect.");

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

                } else if (errorCode.equals("-2")) {
                    activationDialog = new Dialog(LoginActivity.this);
                    activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    activationDialog.setContentView(R.layout.alert);

                    fontChanger = new FontChangeCrawler(getAssets());
                    fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

                    tv_alert = (TextView) activationDialog.findViewById(R.id.txt_alert);
                    tv_alert.setText("Alert");

                    tv_alert1 = (TextView) activationDialog.findViewById(R.id.txt_alert1);
                    tv_alert1.setText("The email or password you have entered is incorrect. ");

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
            } catch (JSONException e) {
                // TODO Auto-generated catch bloc k
                e.printStackTrace();
            }

        }

        @Override
        public void onError(int reqestcode, String error) {
            // TODO Auto-generated method stub
            try {
                AlertDialog.Builder alert = new AlertDialog.Builder(
                        LoginActivity.this);
                alert.setMessage(error);
                alert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                            }
                        });
                alert.create();
                alert.show();
            } catch (Exception e) {
            }
        }

    };

    //Remove login credentials when checkbox is unchecked
    private void removeLoginCredentials() {
        // TODO Auto-generated method stub
        SharedPreferences.Editor e = mPrefs.edit();
        e.remove("rememberMe");
        e.remove("username");
        e.remove("password");
        e.commit();
    }

    //select panel name from list of panels while login
    protected void selectPanel() {
        // TODO Auto-generated method stub
        if (panelList.size() == 1) {
            mPrefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            SharedPreferences.Editor e = mPrefs.edit();
            e.putString("panelist_id", panelistId);
            e.putString("panel_id", "14");
            e.commit();

            Intent i = new Intent(this, MyActivity.class);
            i.putExtra("panelist_id", panelistId);
            i.putExtra("panel_id", "14");

            SharedPreferences myPrefss = this.getSharedPreferences("onetime", MODE_PRIVATE);
            SharedPreferences.Editor myeditor = myPrefss.edit();
            myeditor.putString("FIRSTTIME", "NO");
            myeditor.commit();

            startActivity(i);
        } else {
            activationDialog = new Dialog(LoginActivity.this);
            activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            //for transparent dialog parent layout
            activationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
            activationDialog.setContentView(R.layout.popup_panelselection);

            fontChanger = new FontChangeCrawler(getAssets());
            fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

            panelListview = (ListView) activationDialog.findViewById(R.id.listview_panellist);
            panelListview.setOnItemClickListener(this);

            adapter = new PanellistAdapter(LoginActivity.this, R.layout.content_panellist_listview, panelList);
            panelListview.setAdapter(adapter);
            activationDialog.show();

        }


    }


    //Save login credentials when checkbox is checked
    private void saveLoginCredentials() {
        // TODO Auto-generated method stub
        mPrefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        SharedPreferences.Editor e = mPrefs.edit();
        e.putBoolean("rememberMe", true);
        e.putString("username", username);
        e.putString("password", password);
        e.commit();

    }

    private boolean isValidPassword(String pwd) {
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

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        mPrefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        String login = mPrefs.getString("username", null);
        String upass = mPrefs.getString("password", null);
        boolean re = mPrefs.getBoolean("rememberMe", false);
        Log.v("login" + login, "upass" + upass);

        et_username.setText(login);
        et_password.setText(upass);

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //on listview item click
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        // TODO Auto-generated method stub
        //write webservice code for opening details of particular panel
        TextView panel_name = (TextView) v.findViewById(R.id.listview_panelname);
        TextView panel_id = (TextView) v.findViewById(R.id.listview_panelid);
        TextView panelist_id = (TextView) v.findViewById(R.id.listview_panelistid);

        panelName = panel_name.getText().toString();
        panelId = panel_id.getText().toString();
        panelistId = panelist_id.getText().toString();

        //Toast.makeText(LoginActivity.this,"Panel Name: "+panelName+"Panel Id:"+panelId+"Panelist Id:"+panelistId, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(LoginActivity.this, MyActivity.class);
        i.putExtra("panelist_id", panelistId);
        i.putExtra("panel_id", panelId);

        SharedPreferences myPrefss = getSharedPreferences("onetime", MODE_PRIVATE);
        SharedPreferences.Editor myeditor = myPrefss.edit();
        myeditor.putString("FIRSTTIME", "NO");
        myeditor.commit();

        startActivity(i);
        activationDialog.cancel();

    }


    //method when user login with Facebook account
    private void loginWithFacebook() {
        // TODO Auto-generated method stub
        if (netstat.equals("false")) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    LoginActivity.this);
            alertDialog
                    .setMessage("No network available. Please check the internet connection");
            alertDialog.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
        } else {
            String gender = "";

            try {

                if (fGender.equalsIgnoreCase("male")) {
                    gender = "1";

                } else if (fGender.equalsIgnoreCase("female")) {
                    gender = "2";

                }

                WebService service = new WebService(callbackLoginWithFB);

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("email", fEmail));
                params.add(new BasicNameValuePair("gender", gender));
                params.add(new BasicNameValuePair("firstname", fFirstName));
                params.add(new BasicNameValuePair("lastname", fLastName));
                params.add(new BasicNameValuePair("panel_id", "14"));
                params.add(new BasicNameValuePair("facebook_id", fId));
                params.add(new BasicNameValuePair("registration_id", SharedPref.getPreferences().getString(
                        SharedPref.GCMREGISTRATIONID, "")));

                service.getService(LoginActivity.this, Constants.loginWithFBUrl, params);

            } catch (NullPointerException e) {
                Log.e("OnClickLogin ex ==", "\n" + e.getMessage());
            }

        }

    }

    Callback callbackLoginWithFB = new Callback() {

        @Override
        public void onSuccess(int reqestcode, JSONObject rootjson) {
            // TODO Auto-generated method stub
            System.out.println("*********callbackLoginWithFB********" + rootjson);
            JSONObject result;
            String errorCode;
            String errorMsg;
            JSONObject resultSet;
            String panelistid;
            try {
                result = rootjson.getJSONObject("Result");
                errorCode = result.getString("ErrorCode");
                errorMsg = result.getString("ErrorMessage");

                if (errorCode.equals("0")) {
                    //After successful login
                    resultSet = result.getJSONObject("result");
                    panelistid = resultSet.getString("panelist");

                    Intent i = new Intent(LoginActivity.this, MyActivity.class);
                    i.putExtra("panelist_id", panelistid);
                    i.putExtra("panel_id", "14");
                    SharedPreferences myPrefss = getSharedPreferences("onetime", MODE_PRIVATE);
                    SharedPreferences.Editor myeditor = myPrefss.edit();
                    myeditor.putString("FIRSTTIME", "NO");
                    myeditor.commit();
                    startActivity(i);

                } else if (errorCode.equals("1")) {
                    //For first time
                    Intent i = new Intent(LoginActivity.this, NewFBUser.class);
                    startActivity(i);
                    /*activationDialog = new Dialog(LoginActivity.this);
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
                } else if (errorCode.equals("3")) {

                    //please, activate account
                    activationDialog = new Dialog(LoginActivity.this);
                    activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    activationDialog.setContentView(R.layout.alert);

                    fontChanger = new FontChangeCrawler(getAssets());
                    fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

                    tv_alert = (TextView) activationDialog.findViewById(R.id.txt_alert);
                    tv_alert.setText("Alert");
                    tv_alert1 = (TextView) activationDialog.findViewById(R.id.txt_alert1);
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


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        @Override
        public void onError(int reqestcode, String error) {
            // TODO Auto-generated method stub

        }
    };

}
