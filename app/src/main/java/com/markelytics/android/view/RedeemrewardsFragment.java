package com.markelytics.android.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.markelytics.android.R;
import com.markelytics.android.model.PanelDetails;
import com.markelytics.android.model.RedeemRewards;
import com.markelytics.android.network.NetworkCheck;
import com.markelytics.android.utils.Constants;
import com.markelytics.android.webservice.WebService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RedeemrewardsFragment extends Fragment{
    String panel_id, panelist_id;
    Spinner spn_paymentMethod, sp_points;
    private String netstat="";
    String methodName, methodId, minThreshold, maxThreshold, unitCost, titleText, belowAmount, overAmount, confirmText;
    ArrayList<String> redeemList;
    ArrayList<String> pointList;
    String[][] redeemArray;
    String [] paymentMthdArray;
    String [] pointsArray;
    Dialog activationDialog;
    int length;
    int balance_pts;
    String paymentMthd;
    double unit_cost;
    String points;
    int point;
    TextView tv_alert1, tv_alert, text1, text2;
    Button btnRedeem;
    FontChangeCrawler fontChanger;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.redeem_rewrds, container, false);

        init(view);
        getPanelistId();
        displayPaymentMethod();
        return view;
    }

    private void init(View rootView)
    {
        sp_points = (Spinner) rootView.findViewById(R.id.sp_points);
        spn_paymentMethod = (Spinner) rootView.findViewById(R.id.spn_paymentMethod);
        btnRedeem = (Button) rootView.findViewById(R.id.btnRedeem);

        text1 = (TextView) rootView.findViewById(R.id.text1);
        text2 = (TextView) rootView.findViewById(R.id.text2);

        rootView.setOnTouchListener(new SwipeTouchListener(getActivity()) {

            public void onSwipeRight() {
                super.onSwipeRight();
                Bundle bundle = new Bundle();
                bundle.putString("panel_id", panel_id);
                bundle.putString("panelist_id", panelist_id);

                Fragment Frag = new ProfilingFragment();
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

                Fragment Frag = new InviteFriendFragment();
                Frag.setArguments(bundle);
                FragmentTransaction ft  = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, Frag);
                ft.addToBackStack(null);
                ft.commit();

            }

        });

        btnRedeem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                if(spn_paymentMethod.getSelectedItem().toString().equalsIgnoreCase("select payment method"))
                {
                    Toast.makeText(getActivity(), "Please select payment method", Toast.LENGTH_SHORT).show();
                }
                else if(sp_points.getSelectedItem().toString().equalsIgnoreCase("select points"))
                {
                    Toast.makeText(getActivity(), "Please select points", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    savePoints();
                }

            }
        });
        redeemList = new ArrayList<String>();
        pointList = new ArrayList<String>();
    }

    private void getPanelistId()
    {
        // TODO Auto-generated method stub
        panel_id = getArguments().getString("panel_id");
        panelist_id = getArguments().getString("panelist_id");
       // Toast.makeText(getActivity(), "panel_id"+panel_id+"++++++++"+"panelist_id"+panelist_id, Toast.LENGTH_SHORT).show();

    }

    private void displayPaymentMethod()
    {
        // TODO Auto-generated method stub
        netstat = NetworkCheck.getNetwork(getActivity());
        if (netstat.equals("false")) {

            sp_points.setVisibility(View.GONE);
            spn_paymentMethod.setVisibility(View.GONE);
            btnRedeem.setVisibility(View.GONE);

            text1.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setMessage("No network available. Please check the internet connection");
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });alertDialog.show();


        }
        else
        {
            try {

                WebService service = new WebService(callbackPaymentMthd);

                List<NameValuePair> params = new ArrayList<NameValuePair>();

                // params.add(new BasicNameValuePair("panelist_id", panelist_id));
                params.add(new BasicNameValuePair("panel_id", panel_id));

                service.getService(getActivity(), Constants.payment_method, params);

            }
            catch (NullPointerException e)
            {
                Log.e("OnClickLogin ex ==", "\n" + e.getMessage());
            }
        }

    }

    WebService.Callback callbackPaymentMthd = new WebService.Callback() {

        @Override
        public void onSuccess(int reqestcode, JSONObject rootjson)
        {
            // TODO Auto-generated method stub
            parseResult(rootjson);

        }

        @Override
        public void onError(int reqestcode, String error)
        {
            // TODO Auto-generated method stub

        }
    };

    private void parseResult(JSONObject rootjson) {

        JSONObject result;
        String errorCode;
        JSONArray resultset;

        try
        {
            result = rootjson.getJSONObject("Result");
            errorCode = result.getString("ErrorCode");

            if(errorCode.equals("0"))
            {
                resultset = result.getJSONArray("result");
                length = resultset.length();
                redeemArray = new String[length][9];
                for(int i=0; i<length; i++)
                {
                    methodName = resultset.getJSONObject(i).getString("method_name");
                    methodId = resultset.getJSONObject(i).getString("method_id");
                    minThreshold = resultset.getJSONObject(i).getString("min_threshold");
                    maxThreshold = resultset.getJSONObject(i).getString("max_threshold");
                    unitCost = resultset.getJSONObject(i).getString("unit_cost");
                    titleText = resultset.getJSONObject(i).getString("title_text");
                    belowAmount = resultset.getJSONObject(i).getString("below_amount");
                    overAmount = resultset.getJSONObject(i).getString("over_amount");
                    confirmText = resultset.getJSONObject(i).getString("confirm_text");

                    RedeemRewards details = new RedeemRewards();
                    details.setMethod_id(methodId);
                    details.setMethod_name(methodName);
                    details.setMin_threshold(minThreshold);
                    details.setMax_threshold(maxThreshold);
                    details.setUnit_cost(unitCost);
                    details.setTitle_text(titleText);
                    details.setBelow_amount(belowAmount);
                    details.setOver_amount(overAmount);
                    details.setConfirm_text(confirmText);

                    redeemArray[i][0] = methodName;
                    redeemArray[i][1] = methodId;
                    redeemArray[i][2] = minThreshold;
                    redeemArray[i][3] = unitCost;
                    redeemArray[i][4] = titleText;
                    redeemArray[i][5] = belowAmount;
                    redeemArray[i][6] = overAmount;
                    redeemArray[i][7] = confirmText;
                    redeemArray[i][8] = maxThreshold;


                }

                paymentMthdArray = new String[length+1];
                paymentMthdArray[0] = "Select Payment Method";

                for(int j=1; j<length+1; j++)
                {
                    paymentMthdArray[j] = redeemArray[j-1][0];
//                    System.out.print(" \nans_id: \n" +paymentMthdArray[j]);
                }

                for(int k=0; k<paymentMthdArray.length; k++)
                {
                    redeemList.add(paymentMthdArray[k]);
                }

            }

            //Pop-up Payment Method spinner items
            ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, redeemList);
            paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spn_paymentMethod.setAdapter(paymentAdapter);

            paymentMthd = spn_paymentMethod.getSelectedItem().toString();

            for (int i = 0; i <redeemList.size(); i++)
            {
                if (redeemList.get(i).equalsIgnoreCase(paymentMthd))
                {
                    spn_paymentMethod.setSelection(i);
                    break;

                }

            }
            pointList.add("Select Points");

            ArrayAdapter<String> pointsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, pointList);
            pointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_points.setAdapter(pointsAdapter);



            spn_paymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position!=0) {
                        for (int i = 0; i < length; i++) {

                            if (spn_paymentMethod.getSelectedItem().toString().equalsIgnoreCase(redeemArray[i][0])) {

//                                System.out.println("+++++++Method+++++++"+spn_paymentMethod.getSelectedItem().toString());
                                displayPoints();
                                break;
                            } else {
                                pointList.clear();
                                pointList.add("Select Points");
                                ArrayAdapter<String> pointsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, pointList);
                                pointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp_points.setAdapter(pointsAdapter);

                            }
                        }
                    }
                    else
                    {
                        pointList.clear();
                        pointList.add("Select Points");
                        ArrayAdapter<String> pointsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, pointList);
                        pointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_points.setAdapter(pointsAdapter);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void displayPoints()
    {
        int counter=1;
        pointList.clear();
        paymentMthd = spn_paymentMethod.getSelectedItem().toString();

        for(int i=0; i<length; i++)
        {
            if(paymentMthd.equalsIgnoreCase(redeemArray[i][0]))
            {

                balance_pts = Integer.parseInt(Constants.TOTAL_AVAILABLE_POINTS);

                methodName = redeemArray[i][0];
                methodId = redeemArray[i][1];
                minThreshold = redeemArray[i][2];
                unitCost = redeemArray[i][3];
                titleText = redeemArray[i][4];
                belowAmount = redeemArray[i][5];
                overAmount = redeemArray[i][6];
                confirmText = redeemArray[i][7];
                maxThreshold = redeemArray[i][8];

                int minthreshold = Integer.parseInt(minThreshold);
                int maxthreshold = Integer.parseInt(maxThreshold);

                unit_cost = Double.parseDouble(unitCost);
//Available points (787)>= 250
                if(balance_pts >= minthreshold) //(1300 >= 1000)  250
                {
//                    System.out.println("++++++IN IF1+++"+balance_pts+"====="+unit_cost);

//                    double division = threshold/unit_cost;
//                    Log.e("gdivision", ""+division);
//                    System.out.println("++++++IN IF2+++" + threshold + "=====" + division);
                    point = (int) unit_cost;

                    int selectpoints = minthreshold;
                    int counterselectpoints = minthreshold;

                    int remaining = balance_pts - minthreshold;
                    int division = remaining/point;
                    division++;
                    for(int j=1; j<=division; j++)
                    {
                        int k = j-1;
                        counterselectpoints = (point * k) +counterselectpoints;
                        if(counterselectpoints <= Integer.parseInt(Constants.TOTAL_AVAILABLE_POINTS))
                        {
                            points = String.valueOf(counterselectpoints);
                            counter++;
                        }
                    }
                    pointsArray = new String[(int) (counter)];
                    Log.e("gArraySize", ""+pointsArray.length);
                    pointsArray[0] = "Select Points";

                    for(int j=1; j<=division; j++)
                    {
                        int k = j-1;
                        selectpoints = (point * k) +selectpoints;
                        Log.e("gselectpoints", ""+selectpoints);


                        if(selectpoints <= Integer.parseInt(Constants.TOTAL_AVAILABLE_POINTS))
                        {
                            points = String.valueOf(selectpoints);
                            Log.e("gpoints", ""+points);
                            pointsArray[j] = points;
                        }


                    }

                    for(int k=0; k<pointsArray.length; k++)
                    {
                        System.out.println("++++++IN IF FOR+++"+pointsArray[k]);
                        pointList.add(pointsArray[k]);
                    }

                    for(int k=0; k<pointList.size(); k++)
                    {
                        System.out.println("++++++IN IF FOR pointList+++" + pointList.get(k).toString());
                       // pointList.add(pointsArray[k]);
                    }

                    /*ArrayAdapter<String> pointsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, pointList);
                    pointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sp_points.setAdapter(pointsAdapter);*/
                }
                else
                {
                    activationDialog = new Dialog(getActivity());
                    activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    activationDialog.setContentView(R.layout.alert);

                    tv_alert = (TextView)activationDialog.findViewById(R.id.txt_alert);
                    tv_alert.setText("Alert");

                    tv_alert1 = (TextView)activationDialog.findViewById(R.id.txt_alert1);
                    tv_alert1.setText(belowAmount);
                    Button btn_ok = (Button) activationDialog
                            .findViewById(R.id.btn_ok);
                    btn_ok.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            pointList.clear();
                            pointList.add("Select Points");
                            ArrayAdapter<String> pointsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, pointList);
                            pointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            sp_points.setAdapter(pointsAdapter);
                            activationDialog.cancel();

                        }
                    });
                    activationDialog.show();
                    activationDialog.setCancelable(false);

                }

            }

        }
        System.out.println("++++pointList+++"+pointList.size());

        ArrayAdapter<String> pointsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, pointList);
        pointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_points.setAdapter(pointsAdapter);


    }

    private void savePoints()
    {

        activationDialog = new Dialog(getActivity());
        activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //for transparent dialog parent layout
        activationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
        activationDialog.setContentView(R.layout.confirm);

        TextView txtMesssage = (TextView)activationDialog.findViewById(R.id.txtMesssage);

        txtMesssage.setText(confirmText);

        Button btn_ok = (Button) activationDialog.findViewById(R.id.btnYes);




        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                activationDialog.cancel();

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
                        WebService service = new WebService(callbackPoints);

                        List<NameValuePair> params = new ArrayList<NameValuePair>();

                        params.add(new BasicNameValuePair("panelist_id", panelist_id));
                        params.add(new BasicNameValuePair("panel_id", panel_id));
                        params.add(new BasicNameValuePair("method_id", methodId));
                        params.add(new BasicNameValuePair("points", sp_points.getSelectedItem().toString()));

                        service.getService(getActivity(), Constants.redeem_points, params);

                    } catch (NullPointerException e) {
                        Log.e("OnClickLogin ex ==", "\n" + e.getMessage());
                    }
                }

            }
        });

        activationDialog.show();

    }

    WebService.Callback callbackPoints = new WebService.Callback() {

        @Override
        public void onSuccess(int reqestcode, JSONObject rootjson)
        {
            // TODO Auto-generated method stub
            JSONObject result;
            String errorCode;

            try {
                result = rootjson.getJSONObject("Result");
                errorCode = result.getString("ErrorCode");

                if(errorCode.equalsIgnoreCase("0"))
                {
                    activationDialog = new Dialog(getActivity());
                    activationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    activationDialog.setContentView(R.layout.alert);
                    tv_alert = (TextView)activationDialog.findViewById(R.id.txt_alert);
                    tv_alert.setText("Success");
                    tv_alert1 = (TextView)activationDialog.findViewById(R.id.txt_alert1);
                    tv_alert1.setText("Points redeemed successfully.");

                    Button btn_ok = (Button) activationDialog
                            .findViewById(R.id.btn_ok);
                    btn_ok.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            activationDialog.cancel();

                            Bundle bundle = new Bundle();
                            bundle.putString("panel_id", panel_id);
                            bundle.putString("panelist_id", panelist_id);

                            Fragment Frag = new RedemptionHistoryFragment();
                            Frag.setArguments(bundle);
                            FragmentTransaction ft  = getFragmentManager().beginTransaction();
                            ft.replace(R.id.frame_container, Frag);
                            ft.addToBackStack(null);
                            ft.commit();

                        }
                    });
                    activationDialog.show();
                    activationDialog.setCancelable(false);
                }

            } catch (JSONException e) {
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
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.btnMyAccount).setBackgroundDrawable(getResources().getDrawable(R.drawable.acct));
        getActivity().findViewById(R.id.btnSurvey).setBackgroundDrawable(getResources().getDrawable(R.drawable.survey));
        getActivity().findViewById(R.id.btnInvite).setBackgroundDrawable(getResources().getDrawable(R.drawable.invite));
        getActivity().findViewById(R.id.btnRedeemRewards).setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards_selected));
        getActivity().findViewById(R.id.btnProfile).setBackgroundDrawable(getResources().getDrawable(R.drawable.profile));

        TextView txt = (TextView) getActivity().findViewById(R.id.heading);
        txt.setText("Rewards");

        TextView txtRewards = (TextView) getActivity().findViewById(R.id.txtRewards);
        txtRewards.setTextColor(getResources().getColor(R.color.color_wexpand));

        RelativeLayout relativeRewards = (RelativeLayout) getActivity().findViewById(R.id.relativeRewards);
        relativeRewards.setBackgroundColor(getResources().getColor(R.color.white));

        TextView txtSurvey = (TextView) getActivity().findViewById(R.id.txtSurvey);
        txtSurvey.setTextColor(getResources().getColor(R.color.white));

        RelativeLayout relativeSurvey = (RelativeLayout) getActivity().findViewById(R.id.relativeSurvey);
        relativeSurvey.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

        TextView txtAccount = (TextView) getActivity().findViewById(R.id.txtAccount);
        txtAccount.setTextColor(getResources().getColor(R.color.white));

        RelativeLayout relativeAccount = (RelativeLayout) getActivity().findViewById(R.id.relativeAccount);
        relativeAccount.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

        TextView txtProfile = (TextView) getActivity().findViewById(R.id.txtProfile);
        txtProfile.setTextColor(getResources().getColor(R.color.white));

        RelativeLayout relativeProfile = (RelativeLayout) getActivity().findViewById(R.id.relativeProfile);
        relativeProfile.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

        TextView txtInvite = (TextView) getActivity().findViewById(R.id.txtInvite);
        txtInvite.setTextColor(getResources().getColor(R.color.white));

        RelativeLayout relativeInvite = (RelativeLayout) getActivity().findViewById(R.id.relativeInvite);
        relativeInvite.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

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
