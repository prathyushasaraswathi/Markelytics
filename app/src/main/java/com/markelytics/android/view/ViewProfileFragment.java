package com.markelytics.android.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.markelytics.android.R;
import com.markelytics.android.controller.ProfilingAdapter;
import com.markelytics.android.database.DatabaseHandler;
import com.markelytics.android.model.PartnerDetails;
import com.markelytics.android.network.NetworkCheck;
import com.markelytics.android.utils.Constants;
import com.markelytics.android.webservice.WebService;
import com.markelytics.android.webservice.WebService.Callback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewProfileFragment extends Fragment implements OnClickListener {
    EditText edt_email, edt_firstName, edt_lastName, edt_gender, edt_yrOfBirth,
            edt_streetAddrs, edt_zipCode, edt_phone, edt_idealSurvey;

    Spinner spn_surveyFreq;
    RadioGroup rg;
    RadioButton rd_male, rd_female;
    Button btn_edit, btn_save;
    String id, email, firstName, lastName, gender, yrOfBirth, streetAddrs, zipCode,
            phone, surveyFreq;
    String PREFS = "MyPrefs";
    SharedPreferences mPrefs;
    private String netstat = "";
    String panel_id, panelist_id;

    List<String> survyFreqList;
    ScrollView scrollview;
    Dialog activationDialog;
    TextView tv_alert1, tv_alert2, tv_alert;
    String errorMsg;
    RelativeLayout relativeLogout;
    Context context;
    FontChangeCrawler fontChanger;
    DatabaseHandler db;
    Boolean isDatainDB = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        View view = inflater.inflate(R.layout.view_profile, container, false);

        init(view);

        return view;

    }

    private void init(View view) {
        // TODO Auto-generated method stub

        db = new DatabaseHandler(getActivity());
        edt_email = (EditText) view.findViewById(R.id.edt_email);
        edt_firstName = (EditText) view.findViewById(R.id.edt_firstName);
        edt_lastName = (EditText) view.findViewById(R.id.edt_lastName);
        edt_gender = (EditText) view.findViewById(R.id.edt_gender);
        rg = (RadioGroup) view.findViewById(R.id.rdg_gender);
        rd_male = (RadioButton) view.findViewById(R.id.gender_male);
        rd_female = (RadioButton) view.findViewById(R.id.gender_female);

        scrollview = (ScrollView) view.findViewById(R.id.scrollview);
        spn_surveyFreq = (Spinner) view.findViewById(R.id.spn_surveyFreq);
        edt_yrOfBirth = (EditText) view.findViewById(R.id.edt_birthYear);
        edt_streetAddrs = (EditText) view.findViewById(R.id.edt_streetAddress);
        edt_zipCode = (EditText) view.findViewById(R.id.edt_zipCode);
        edt_phone = (EditText) view.findViewById(R.id.edt_phone);

        edt_idealSurvey = (EditText) view.findViewById(R.id.edt_surveyFreq);

        relativeLogout = (RelativeLayout) view.findViewById(R.id.relativeLogout);

        edt_email.setInputType(524288);
        edt_firstName.setInputType(524288);
        edt_lastName.setInputType(524288);
        edt_gender.setInputType(524288);
        edt_yrOfBirth.setInputType(524288);

        edt_zipCode.setInputType(524288);
        edt_phone.setInputType(524288);

        edt_idealSurvey.setInputType(524288);

        btn_edit = (Button) view.findViewById(R.id.btn_edit);
        btn_save = (Button) view.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(this);
        btn_edit.setOnClickListener(this);
        relativeLogout.setOnClickListener(this);

        edt_yrOfBirth.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_zipCode.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_phone.setInputType(InputType.TYPE_CLASS_PHONE);

        getPanelistId();
        displayDetails();

        view.setOnTouchListener(new SwipeTouchListener(getActivity()) {

            public void onSwipeRight() {
                super.onSwipeRight();
                Bundle bundle = new Bundle();
                bundle.putString("panel_id", panel_id);
                bundle.putString("panelist_id", panelist_id);

                Fragment Frag = new InviteFriendFragment();
                Frag.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, Frag);
                ft.addToBackStack(null);
                ft.commit();
            }

            public void onSwipeLeft() {

            }

        });

        scrollview.setOnTouchListener(new SwipeTouchListener(getActivity()) {

            public void onSwipeRight() {
                super.onSwipeRight();
                Bundle bundle = new Bundle();
                bundle.putString("panel_id", panel_id);
                bundle.putString("panelist_id", panelist_id);

                Fragment Frag = new InviteFriendFragment();
                Frag.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, Frag);
                ft.addToBackStack(null);
                ft.commit();
            }

            public void onSwipeLeft() {

            }

        });

    }

    private void getPanelistId() {
        // TODO Auto-generated method stub

        panel_id = getArguments().getString("panel_id");
        panelist_id = getArguments().getString("panelist_id");
        //Toast.makeText(getActivity(), "panel_id" + panel_id + "++++++++" + "panelist_id" + panelist_id, Toast.LENGTH_SHORT).show();
    }

    //Display partner profile details from server
    private void displayDetails() {
        int count = db.isProfileAvailable();
        if (count != 0) {
            isDatainDB = true;
            DisplayProfileFromDB();
        }else{
            isDatainDB = false;
        }

//		// TODO Auto-generated method stub
        netstat = NetworkCheck.getNetwork(getActivity());
        if (netstat.equals("true")) {
            try {
                //				JSONObject json = userFunction.loginUser(userNameText, passwordText);
                WebService service = new WebService(callbackViewDetails);

                List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(new BasicNameValuePair("panelist_id", panelist_id));
                params.add(new BasicNameValuePair("panel_id", panel_id));

                if(!isDatainDB){
                    service.getService(getActivity(), Constants.viewDetails, params);
                }else {
                    service.getServiceNoLoader(getActivity(), Constants.viewDetails, params, false);
                }

            } catch (NullPointerException e) {
                Log.e("OnClickLogin ==", "\n" + e.getMessage());
            }
        }


    }

    ////////////////////////////////////////////////////
    private void DisplayProfileFromDB() {
        PartnerDetails detail = db.Get_Profile(1);
        if (!detail.getEmail().equalsIgnoreCase("null")) {
            edt_email.setText(detail.getEmail());
        } else {
            edt_email.setText("");
        }

        if (!detail.getFirstName().equalsIgnoreCase("null")) {
            edt_firstName.setText(detail.getFirstName());
        } else {
            edt_firstName.setText("");
        }

        if (!detail.getLastName().equalsIgnoreCase("null")) {
            edt_lastName.setText(detail.getLastName());
        } else {
            edt_lastName.setText("");
        }

        if (!detail.getGender().equalsIgnoreCase("null")) {
            edt_gender.setText(detail.getGender());
        } else {
            edt_gender.setText("");
        }

        if (!detail.getDob().equalsIgnoreCase("null")) {
            edt_yrOfBirth.setText(detail.getDob());
        } else {
            edt_yrOfBirth.setText("");
        }

        if (!detail.getAddress().equalsIgnoreCase("null")) {
            edt_streetAddrs.setText(detail.getAddress());
        } else {
            edt_streetAddrs.setText("");
        }

        if (!detail.getZipCode().equalsIgnoreCase("null")) {
            edt_zipCode.setText(detail.getZipCode());
        } else {
            edt_zipCode.setText("");
        }

        if (!detail.getPhone().equalsIgnoreCase("null")) {
            edt_phone.setText(detail.getPhone());
        } else {
            edt_phone.setText("");
        }

        if (!detail.getSurveyFreq().equalsIgnoreCase("null")) {
            edt_idealSurvey.setText(detail.getSurveyFreq());
        } else {
            edt_idealSurvey.setText("");
        }

        edt_email.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_firstName.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_lastName.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_gender.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_yrOfBirth.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_streetAddrs.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_zipCode.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_phone.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_idealSurvey.setBackgroundColor(getActivity().getResources().getColor(R.color.white));

    }

    Callback callbackViewDetails = new Callback() {

        @Override
        public void onSuccess(int reqestcode, JSONObject rootjson) {
            // TODO Auto-generated method stub
            parseResult(rootjson);

        }

        @Override
        public void onError(int reqestcode, String error) {
            // TODO Auto-generated method stub

        }
    };


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_edit:

                editDetails();

                break;

            case R.id.btn_save:
                if (validation()) {
                    savedetails();
                }

                break;

            case R.id.relativeLogout:

                mPrefs = this.getActivity().getSharedPreferences(PREFS, context.MODE_PRIVATE);

                activationDialog = new Dialog(getActivity());
                activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                //for transparent dialog parent layout
                activationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
                activationDialog.setContentView(R.layout.popup_logout);

                fontChanger = new FontChangeCrawler(getActivity().getAssets());
                fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

                Button btn_ok = (Button) activationDialog.findViewById(R.id.btnYes);
                Button btn_cancel = (Button) activationDialog.findViewById(R.id.btnCancel);

                btn_cancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        activationDialog.cancel();
                    }
                });

                btn_ok.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        SharedPreferences.Editor e = mPrefs.edit();
                        e.remove("rememberMe");
                        e.remove("username");
                        e.remove("password");
                        e.commit();

                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("LOGOUT", true);
                        startActivity(intent);

                    }
                });

                activationDialog.show();
                activationDialog.setCancelable(false);

                break;

            default:
                break;
        }
    }

    private void redeemPoints() {
        // TODO Auto-generated method stub
        netstat = NetworkCheck.getNetwork(getActivity());
        if (netstat.equals("false")) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setMessage("No network available. Please check the internet connection");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        } else {
            try {
                //				JSONObject json = userFunction.loginUser(userNameText, passwordText);
                WebService service = new WebService(callbackRedeemPoints);

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                System.out.println("panelId" + panel_id + "panelist_id" + panelist_id);
                params.add(new BasicNameValuePair("panelist_id", panelist_id));

                service.getService(getActivity(), Constants.redeemPoints, params);

            } catch (NullPointerException e) {
                Log.e("OnClickLogin ==", "\n" + e.getMessage());
            }
        }

    }

    Callback callbackRedeemPoints = new Callback() {

        @Override
        public void onSuccess(int reqestcode, JSONObject rootjson) {
            // TODO Auto-generated method stub
            JSONObject result;
            String errorCode;
            try {
                result = rootjson.getJSONObject("Result");
                errorCode = result.getString("ErrorCode");

                if (errorCode.equals("3")) {
                    errorMsg = result.getString("ErrorMessage");


                } else if (errorCode.equals("4")) {
                    errorMsg = result.getString("ErrorMessage");

                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            activationDialog = new Dialog(getActivity());
            activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            activationDialog.setContentView(R.layout.message);

            fontChanger = new FontChangeCrawler(getActivity().getAssets());
            fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

            tv_alert2 = (TextView) activationDialog.findViewById(R.id.txt_alert2);
            tv_alert2.setText(errorMsg);


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

        @Override
        public void onError(int reqestcode, String error) {
            // TODO Auto-generated method stub

        }
    };

    //Method for parsing partner details for server
    protected void parseResult(JSONObject json) {
        // TODO Auto-generated method stub
        System.out.println("+++++Profile details+++++" + json);
        JSONObject results = null;
        JSONObject resultset = null;

        JSONArray paymntMthd = null;
        String details = null;

        try {
            results = json.getJSONObject("Result");

            String success = results.getString("ErrorCode");
            if (success.equals("0")) {
                resultset = results.getJSONObject("result");
                int length = resultset.length();

                paymntMthd = resultset.getJSONArray("payment_method");
                int paymntLength = paymntMthd.length();

                Log.v("FOR PARTNER DETAILS", "parseResult");
                email = resultset.getString("email");
                firstName = resultset.getString("first_name");
                lastName = resultset.getString("last_name");
                gender = resultset.getString("gender");
                yrOfBirth = resultset.getString("year_of_birth");
                streetAddrs = resultset.getString("street_address");
                zipCode = resultset.getString("zip_code");
                phone = resultset.getString("phone");

                surveyFreq = resultset.getString("ideal_survey");

                if (gender.equalsIgnoreCase("1")) {
                    gender = "Male";
                } else if (gender.equalsIgnoreCase("2")) {
                    gender = "Female";
                }

                if (surveyFreq.equalsIgnoreCase("0")) {
                    surveyFreq = "Recommended default";
                } else if (surveyFreq.equalsIgnoreCase("1")) {
                    surveyFreq = "6 per month";
                } else if (surveyFreq.equalsIgnoreCase("2")) {
                    surveyFreq = "3 per month";
                } else if (surveyFreq.equalsIgnoreCase("3")) {
                    surveyFreq = "1 per month";
                }

                PartnerDetails detail = new PartnerDetails();
                detail.setEmail(email);
                detail.setFirstName(firstName);
                detail.setLastName(lastName);
                detail.setGender(gender);
                detail.setDob(yrOfBirth);
                detail.setAddress(streetAddrs);
                detail.setZipCode(zipCode);
                detail.setPhone(phone);
                detail.setSurveyFreq(surveyFreq);

                if (db.isDataChanged(detail)) {
                    db.Delete_Profile(1);
                    db.Add_Profile(detail);


                    if (!detail.getEmail().equalsIgnoreCase("null")) {
                        edt_email.setText(detail.getEmail());
                    } else {
                        edt_email.setText("");
                    }

                    if (!detail.getFirstName().equalsIgnoreCase("null")) {
                        edt_firstName.setText(detail.getFirstName());
                    } else {
                        edt_firstName.setText("");
                    }

                    if (!detail.getLastName().equalsIgnoreCase("null")) {
                        edt_lastName.setText(detail.getLastName());
                    } else {
                        edt_lastName.setText("");
                    }

                    if (!detail.getGender().equalsIgnoreCase("null")) {
                        edt_gender.setText(detail.getGender());
                    } else {
                        edt_gender.setText("");
                    }

                    if (!detail.getDob().equalsIgnoreCase("null")) {
                        edt_yrOfBirth.setText(detail.getDob());
                    } else {
                        edt_yrOfBirth.setText("");
                    }

                    if (!detail.getAddress().equalsIgnoreCase("null")) {
                        edt_streetAddrs.setText(detail.getAddress());
                    } else {
                        edt_streetAddrs.setText("");
                    }

                    if (!detail.getZipCode().equalsIgnoreCase("null")) {
                        edt_zipCode.setText(detail.getZipCode());
                    } else {
                        edt_zipCode.setText("");
                    }

                    if (!detail.getPhone().equalsIgnoreCase("null")) {
                        edt_phone.setText(detail.getPhone());
                    } else {
                        edt_phone.setText("");
                    }

                    if (!detail.getSurveyFreq().equalsIgnoreCase("null")) {
                        edt_idealSurvey.setText(detail.getSurveyFreq());
                    } else {
                        edt_idealSurvey.setText("");
                    }


                    edt_email.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                    edt_firstName.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                    edt_lastName.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                    edt_gender.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                    edt_yrOfBirth.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                    edt_streetAddrs.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                    edt_zipCode.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                    edt_phone.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
                    edt_idealSurvey.setBackgroundColor(getActivity().getResources().getColor(R.color.white));

                }

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }


    }


    private void editDetails() {
        // TODO Auto-generated method stub
        edt_firstName.setEnabled(true);
        edt_lastName.setEnabled(true);
        rg.setVisibility(View.VISIBLE);
        edt_gender.setVisibility(View.GONE);
        spn_surveyFreq.setVisibility(View.VISIBLE);
        edt_idealSurvey.setVisibility(View.GONE);
        edt_yrOfBirth.setEnabled(true);
        edt_streetAddrs.setEnabled(true);
        edt_zipCode.setEnabled(true);
        edt_phone.setEnabled(true);

        relativeLogout.setVisibility(View.INVISIBLE);
        btn_save.setVisibility(View.VISIBLE);
        btn_edit.setEnabled(false);

        btn_edit.setVisibility(View.GONE);

        edt_firstName.setBackgroundResource(R.drawable.login_back);
        edt_lastName.setBackgroundResource(R.drawable.login_back);
        edt_gender.setBackgroundResource(R.drawable.login_back);
        edt_yrOfBirth.setBackgroundResource(R.drawable.login_back);
        edt_streetAddrs.setBackgroundResource(R.drawable.login_back);
        edt_zipCode.setBackgroundResource(R.drawable.login_back);
        edt_phone.setBackgroundResource(R.drawable.login_back);

        edt_idealSurvey.setBackgroundResource(R.drawable.login_back);

        //Pop-up Survey Frequency spinner items
        survyFreqList = new ArrayList<String>();

        survyFreqList.add("Recommended default");
        survyFreqList.add("6 per month");
        survyFreqList.add("3 per month");
        survyFreqList.add("1 per month");

        ArrayAdapter<String> survyFreqAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, survyFreqList);
        survyFreqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_surveyFreq.setAdapter(survyFreqAdapter);

        for (int i = 0; i < survyFreqList.size(); i++) {
            if (survyFreqList.get(i).equalsIgnoreCase(edt_idealSurvey.getText().toString())) {

                spn_surveyFreq.setSelection(i);
                break;
            }
        }

        //set gender
        gender = edt_gender.getText().toString();

        if (gender.equals("male") || gender.equals("Male")) {
            rd_male.setChecked(true);

        } else if (gender.equals("female") || gender.equals("Female")) {
            rd_female.setChecked(true);

        }
    }


    //Save edited details of partner on server side
    private void savedetails() {
        // TODO Auto-generated method stub
        //Toast.makeText(getActivity(), "Details updated successfully", Toast.LENGTH_SHORT).show();
        edt_firstName.setEnabled(false);
        edt_lastName.setEnabled(false);
        rg.setVisibility(View.GONE);

        spn_surveyFreq.setVisibility(View.GONE);
        edt_gender.setEnabled(false);
        edt_gender.setVisibility(View.VISIBLE);
        edt_yrOfBirth.setEnabled(false);
        edt_streetAddrs.setEnabled(false);
        edt_zipCode.setEnabled(false);
        edt_phone.setEnabled(false);

        edt_idealSurvey.setEnabled(false);
        edt_idealSurvey.setVisibility(View.VISIBLE);

        btn_edit.setEnabled(true);

        btn_edit.setVisibility(View.VISIBLE);

        btn_save.setVisibility(View.INVISIBLE);
        relativeLogout.setVisibility(View.VISIBLE);

        edt_email.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_firstName.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_lastName.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_gender.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_yrOfBirth.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_streetAddrs.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_zipCode.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_phone.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        edt_idealSurvey.setBackgroundColor(getActivity().getResources().getColor(R.color.white));

        PartnerDetails detail = new PartnerDetails();

        surveyFreq = spn_surveyFreq.getSelectedItem().toString();
        //Toast.makeText(getActivity(),"PAYMENT METHOD:"+paymentMethod+"SURVEY FREQUENCY:"+surveyFreq , Toast.LENGTH_SHORT).show();

        if (rd_male.isChecked()) {
            gender = "1";
            edt_gender.setText("Male");
        } else if (rd_female.isChecked()) {
            gender = "2";
            edt_gender.setText("Female");
        }

        if (surveyFreq.equalsIgnoreCase("Recommended default")) {
            surveyFreq = "0";
            edt_idealSurvey.setText("Recommended default");
        } else if (surveyFreq.equalsIgnoreCase("6 per month")) {
            surveyFreq = "1";
            edt_idealSurvey.setText("6 per month");
        } else if (surveyFreq.equalsIgnoreCase("3 per month")) {
            surveyFreq = "2";
            edt_idealSurvey.setText("3 per month");
        } else if (surveyFreq.equalsIgnoreCase("1 per month")) {
            surveyFreq = "3";
            edt_idealSurvey.setText("1 per month");
        }


        detail.setFirstName(firstName);
        detail.setLastName(lastName);
        detail.setGender(gender);
        detail.setDob(yrOfBirth);
        detail.setAddress(streetAddrs);
        detail.setZipCode(zipCode);
        detail.setPhone(phone);
        detail.setSurveyFreq(surveyFreq);

        try {
            WebService service = new WebService(callbackEditDetails);

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            String fName = detail.getFirstName();
            String lName = detail.getLastName();
            String gender = detail.getGender();
            String yrOfBirth = detail.getDob();
            String streetAddress = detail.getAddress();
            String zipCode = detail.getZipCode();
            String phone = detail.getPhone();

            String idealSrvy = detail.getSurveyFreq();

            params.add(new BasicNameValuePair("panel_id", panel_id));
            params.add(new BasicNameValuePair("panelist_id", panelist_id));
            params.add(new BasicNameValuePair("first_name", fName));
            params.add(new BasicNameValuePair("last_name", lName));
            params.add(new BasicNameValuePair("gender", gender));
            params.add(new BasicNameValuePair("year_of_birth", yrOfBirth));
            params.add(new BasicNameValuePair("street_address", streetAddress));
            params.add(new BasicNameValuePair("zip_code", zipCode));
            params.add(new BasicNameValuePair("phone", phone));
            params.add(new BasicNameValuePair("ideal_survey", idealSrvy));

            service.getService(getActivity(), Constants.editUrl, params);

        } catch (NullPointerException e) {
            Log.e("OnClickLogin ==", "\n" + e.getMessage());
        }

    }

    //Validation for profile fields
    private boolean validation() {
        // TODO Auto-generated method stub
        firstName = edt_firstName.getText().toString();
        lastName = edt_lastName.getText().toString();
        yrOfBirth = edt_yrOfBirth.getText().toString();
        streetAddrs = edt_streetAddrs.getText().toString();
        zipCode = edt_zipCode.getText().toString();
        phone = edt_phone.getText().toString();

        if (firstName == null || firstName.equals("")) {
            edt_firstName.setError("Please, Enter first name.");
            //Toast.makeText(getActivity(), "Enter first name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (lastName == null || lastName.equals("")) {
            edt_lastName.setError("Please, Enter last name.");
            //Toast.makeText(getActivity(), "Enter last name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (zipCode == null || zipCode.equals("")) {
            edt_zipCode.setError("Please, Enter zip code.");
            //Toast.makeText(getActivity(), "Enter Zip code", Toast.LENGTH_SHORT).show();
            return false;
        } else if (streetAddrs == null || streetAddrs.equals("")) {
            edt_streetAddrs.setError("Please, Enter street address.");
            //Toast.makeText(getActivity(), "Enter Street address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!isValidName(firstName)) {
            edt_firstName.setError("Name should be in characters");
            return false;
        } else if (!isValidName(lastName)) {
            edt_lastName.setError("Name should be in characters");
            return false;
        } else if (!yrOfBirth.isEmpty()) {
            if (!isValidDate(yrOfBirth)) {
                edt_yrOfBirth.setError("Invalid date");
                return false;
            }

        }

        return true;
    }

    //For cheking validation of date field
    private boolean isValidDate(String yrOfBirth2) {
        // TODO Auto-generated method stub
        final Calendar c = Calendar.getInstance();
        final int currentYear = c.get(Calendar.YEAR);
        final int currentMonth = c.get(Calendar.MONTH) + 1;
        final int currentDay = c.get(Calendar.DAY_OF_MONTH);
        int yr = Integer.parseInt(yrOfBirth2);
        //Log.v("year:"+currentYear+"month:"+currentMonth+"day:"+currentDay, "---msg---");
        if (yr > (currentYear - 100) && yr < (currentYear - 14)) {
            return true;
        }
        return false;
    }

    private boolean isValidName(String firstname) {
        // TODO Auto-generated method stub
        String EMAIL_PATTERN = ("^[a-zA-Z ]+$");

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(firstname);
        return matcher.matches();

    }

    Callback callbackEditDetails = new Callback() {

        @Override
        public void onSuccess(int reqestcode, JSONObject rootjson) {
            // TODO Auto-generated method stub
            //Toast.makeText(getActivity(),
            //"Successfully Updated", Toast.LENGTH_LONG).show();
            activationDialog = new Dialog(getActivity());
            activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            activationDialog.setContentView(R.layout.alert);

            fontChanger = new FontChangeCrawler(getActivity().getAssets());
            fontChanger.replaceFonts((ViewGroup) activationDialog.findViewById(android.R.id.content));

            tv_alert = (TextView) activationDialog.findViewById(R.id.txt_alert);
            tv_alert.setText("Success");

            tv_alert1 = (TextView) activationDialog.findViewById(R.id.txt_alert1);
            tv_alert1.setText("Profile details are successfully updated. ");

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

        @Override
        public void onError(int reqestcode, String error) {
            // TODO Auto-generated method stub
            try {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setMessage(error);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                });
                alert.create();
                alert.show();
            } catch (Exception e) {
            }
        }

    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        fontChanger = new FontChangeCrawler(getActivity().getAssets());
        fontChanger.replaceFonts((ViewGroup) getActivity().findViewById(android.R.id.content));

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.btnMyAccount).setBackgroundDrawable(getResources().getDrawable(R.drawable.acct_selected));
        getActivity().findViewById(R.id.btnSurvey).setBackgroundDrawable(getResources().getDrawable(R.drawable.survey));
        getActivity().findViewById(R.id.btnInvite).setBackgroundDrawable(getResources().getDrawable(R.drawable.invite));
        getActivity().findViewById(R.id.btnRedeemRewards).setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards));
        getActivity().findViewById(R.id.btnProfile).setBackgroundDrawable(getResources().getDrawable(R.drawable.profile));

        TextView txt = (TextView) getActivity().findViewById(R.id.heading);
        txt.setText("MyAccount");

        TextView txtAccount = (TextView) getActivity().findViewById(R.id.txtAccount);
        txtAccount.setTextColor(getResources().getColor(R.color.color_wexpand));

        RelativeLayout relativeAccount = (RelativeLayout) getActivity().findViewById(R.id.relativeAccount);
        relativeAccount.setBackgroundColor(getResources().getColor(R.color.white));

        TextView txtSurvey = (TextView) getActivity().findViewById(R.id.txtSurvey);
        txtSurvey.setTextColor(getResources().getColor(R.color.white));

        RelativeLayout relativeSurvey = (RelativeLayout) getActivity().findViewById(R.id.relativeSurvey);
        relativeSurvey.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

        TextView txtRewards = (TextView) getActivity().findViewById(R.id.txtRewards);
        txtRewards.setTextColor(getResources().getColor(R.color.white));

        RelativeLayout relativeRewards = (RelativeLayout) getActivity().findViewById(R.id.relativeRewards);
        relativeRewards.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

        TextView txtProfile = (TextView) getActivity().findViewById(R.id.txtProfile);
        txtProfile.setTextColor(getResources().getColor(R.color.white));

        RelativeLayout relativeProfile = (RelativeLayout) getActivity().findViewById(R.id.relativeProfile);
        relativeProfile.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

        TextView txtInvite = (TextView) getActivity().findViewById(R.id.txtInvite);
        txtInvite.setTextColor(getResources().getColor(R.color.white));

        RelativeLayout relativeInvite = (RelativeLayout) getActivity().findViewById(R.id.relativeInvite);
        relativeInvite.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

    }


}
