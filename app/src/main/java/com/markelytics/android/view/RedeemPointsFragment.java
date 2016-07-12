package com.markelytics.android.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.markelytics.android.R;
import com.markelytics.android.controller.YourSurveyAdapter;
import com.markelytics.android.model.SurveyDetail;
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

/**
 * Created by dvimay on 11/17/15.
 */
public class RedeemPointsFragment extends Fragment implements View.OnClickListener {

    String panel_id, panelist_id;
    TextView txt_pointsEarned, txt_pointsRedeemed, txt_pointsAvailable;
    Button btn_redeemRewards;
    FontChangeCrawler fontChanger;
    LinearLayout linear_pointsAvailable, linear_pointredeemed, linear_pointsearned;
    private String netstat = "";
    int tearned, tavailable, tredeemed;
    String total_available, total_earned;
    SharedPreferences sharedPreferences;
    Boolean isChanged = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.your_balance_new, container, false);

        Constants.EXIT = false;
        init(rootView);
        getPanelistId();

        sharedPreferences = getActivity().getSharedPreferences("points", Context.MODE_PRIVATE);
        String result = sharedPreferences.getString("txt_pointsEarned", "empty");
        if (!result.equalsIgnoreCase("empty")) {
            String result1 = sharedPreferences.getString("txt_pointsEarned", "empty");
            String result2 = sharedPreferences.getString("txt_pointsAvailable", "empty");
            String result3 = sharedPreferences.getString("txt_pointsRedeemed", "empty");

            if (!result1.equalsIgnoreCase("empty") && !result2.equalsIgnoreCase("empty") && !result3.equalsIgnoreCase("empty")) {
                txt_pointsEarned.setText(result1);
                txt_pointsAvailable.setText(result2);
                txt_pointsRedeemed.setText(result3);
            }
        }

        getData();

        return rootView;
    }

    private void getData() {


        netstat = NetworkCheck.getNetwork(getActivity());
        if (netstat.equals("false")) {
//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
//            alertDialog.setMessage("No network available. Please check the internet connection");
//            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//            alertDialog.show();
        } else {
            try {
                //				JSONObject json = userFunction.loginUser(userNameText, passwordText);
                WebService service = new WebService(callbackYourSurvey);

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("panelist_id", panelist_id));
                params.add(new BasicNameValuePair("panel_id", panel_id));

                String result = sharedPreferences.getString("txt_pointsEarned", "empty");
                if (result.equalsIgnoreCase("empty")) {
                    service.getService(getActivity(), Constants.yourSurvey, params);
                }else{
                    service.getServiceNoLoader(getActivity(), Constants.yourSurvey, params, false);
                }


            } catch (NullPointerException e) {
                Log.e("OnClickLogin ==", "\n" + e.getMessage());
            }
        }
    }


    WebService.Callback callbackYourSurvey = new WebService.Callback() {

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

    protected void parseResult(JSONObject json) {
        // TODO Auto-generated method stub
        JSONObject result = null;
        JSONArray survey;
        String error_code;

        try {
            result = json.getJSONObject("Result");
            survey = result.getJSONArray("survey");
            int length = survey.length();

            error_code = result.getString("ErrorCode");
            if (error_code.equals("0")) {
                //profile_percentage = result.getString("profile_complete_percentage");
                //Constants.PERCENTAGE = profile_percentage;

                total_available = result.getString("total_available");
                Constants.TOTAL_AVAILABLE_POINTS = total_available;
                total_earned = result.getString("total_earned");
                Constants.TOTAL_EARNED_POINTS = total_earned;

                tearned = Integer.parseInt(total_earned);
                tavailable = Integer.parseInt(total_available);
                tredeemed = tearned - tavailable;
                String total_redeemed = String.valueOf(tredeemed);
                Constants.TOTAL_REDMMED_POINTS = total_redeemed;

                SharedPreferences.Editor ed = sharedPreferences.edit();
                String result1 = sharedPreferences.getString("txt_pointsEarned", "empty");
                String result2 = sharedPreferences.getString("txt_pointsAvailable", "empty");
                String result3 = sharedPreferences.getString("txt_pointsRedeemed", "empty");

                if(result1.equalsIgnoreCase("empty") || !result1.equalsIgnoreCase(Constants.TOTAL_EARNED_POINTS))
                {
                    ed.putString("txt_pointsEarned", Constants.TOTAL_EARNED_POINTS);
                    isChanged = true;
                    txt_pointsEarned.setText(Constants.TOTAL_EARNED_POINTS);
                }
                if(result2.equalsIgnoreCase("empty") || !result2.equalsIgnoreCase(Constants.TOTAL_AVAILABLE_POINTS))
                {
                    ed.putString("txt_pointsAvailable", Constants.TOTAL_AVAILABLE_POINTS);
                    isChanged = true;
                    txt_pointsAvailable.setText(Constants.TOTAL_AVAILABLE_POINTS);
                }
                if(result3.equalsIgnoreCase("empty") || !result3.equalsIgnoreCase(Constants.TOTAL_REDMMED_POINTS))
                {
                    ed.putString("txt_pointsRedeemed", Constants.TOTAL_REDMMED_POINTS);
                    isChanged = true;
                    txt_pointsRedeemed.setText(Constants.TOTAL_REDMMED_POINTS);
                }

                if(isChanged){
                    ed.commit();
                }
                isChanged = false;
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void init(View rootView) {
        txt_pointsRedeemed = (TextView) rootView.findViewById(R.id.txt_pointsRedeemed);
        txt_pointsAvailable = (TextView) rootView.findViewById(R.id.txt_pointsAvailable);
        txt_pointsEarned = (TextView) rootView.findViewById(R.id.txt_pointsEarned);
        btn_redeemRewards = (Button) rootView.findViewById(R.id.btn_redeemRewards);
        linear_pointsearned = (LinearLayout) rootView.findViewById(R.id.linear_pointsearned);
        linear_pointsAvailable = (LinearLayout) rootView.findViewById(R.id.linear_pointsAvailable);
        linear_pointredeemed = (LinearLayout) rootView.findViewById(R.id.linear_pointredeemed);

        linear_pointredeemed.setOnClickListener(this);
        linear_pointsAvailable.setOnClickListener(this);
        linear_pointsearned.setOnClickListener(this);

        btn_redeemRewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("panel_id", panel_id);
                bundle.putString("panelist_id", panelist_id);

                Fragment Frag = new RedeemrewardsFragment();
                Frag.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, Frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        rootView.setOnTouchListener(new SwipeTouchListener(getActivity()) {

            public void onSwipeRight() {
                super.onSwipeRight();
                Bundle bundle = new Bundle();
                bundle.putString("panel_id", panel_id);
                bundle.putString("panelist_id", panelist_id);

                Fragment Frag = new ProfilingFragment();
                Frag.setArguments(bundle);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
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
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, Frag);
                ft.addToBackStack(null);
                ft.commit();

            }

        });

    }

    private void getPanelistId() {
        // TODO Auto-generated method stub
        panel_id = getArguments().getString("panel_id");
        panelist_id = getArguments().getString("panelist_id");
        // Toast.makeText(getActivity(), "panel_id" + panel_id + "++++++++" + "panelist_id" + panelist_id, Toast.LENGTH_SHORT).show();
    }

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

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        Fragment Frag;
        FragmentTransaction ft;

        switch (v.getId()) {
            case R.id.linear_pointredeemed:

                bundle.putString("panel_id", panel_id);
                bundle.putString("panelist_id", panelist_id);

                Frag = new RedemptionHistoryFragment();
                Frag.setArguments(bundle);
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, Frag);
                ft.addToBackStack(null);
                ft.commit();

                break;

            case R.id.linear_pointsAvailable:


                bundle.putString("panel_id", panel_id);
                bundle.putString("panelist_id", panelist_id);

                Frag = new RedeemrewardsFragment();
                Frag.setArguments(bundle);
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, Frag);
                ft.addToBackStack(null);
                ft.commit();

                break;

            case R.id.linear_pointsearned:

                bundle.putString("calledFrom", "Redeem");
                bundle.putString("panel_id", panel_id);
                bundle.putString("panelist_id", panelist_id);
                Constants.EXIT = true;

                Frag = new RecentSurveyFragment();
                Frag.setArguments(bundle);
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_container, Frag);
                ft.addToBackStack(null);
                ft.commit();

                break;
        }
    }
}
