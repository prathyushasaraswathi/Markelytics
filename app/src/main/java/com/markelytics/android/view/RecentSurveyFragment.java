package com.markelytics.android.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.markelytics.android.R;
import com.markelytics.android.controller.ProfilingAdapter;
import com.markelytics.android.controller.SurveyAdapter;
import com.markelytics.android.database.DatabaseHandler;
import com.markelytics.android.model.PartnerDetails;
import com.markelytics.android.model.SurveyDetail;
import com.markelytics.android.network.NetworkCheck;
import com.markelytics.android.utils.Constants;
import com.markelytics.android.webservice.WebService;
import com.markelytics.android.webservice.WebService.Callback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecentSurveyFragment extends Fragment {
    TextView txt_noSurvey;
    LinearLayout btn_yourBalance, linear2;
    ArrayList<SurveyDetail> list;
    String panel_id, panelist_id, total_points, total_available, total_earned, panel_logo;
    private String netstat = "";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    CardView card_view;
    Bitmap img;
    FontChangeCrawler fontChanger;
    DatabaseHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.recent_survey, container, false);

        init(rootView);
        getPanelistId();

        displayrecentSurvey();
        return rootView;
    }


    private void init(View rootView) {
        // TODO Auto-generated method stub

        db = new DatabaseHandler(getActivity());
        btn_yourBalance = (LinearLayout) rootView.findViewById(R.id.btn_yourBalance);
        txt_noSurvey = (TextView) rootView.findViewById(R.id.txt_noSurvey);
        card_view = (CardView) rootView.findViewById(R.id.card_view);
        linear2 = (LinearLayout) rootView.findViewById(R.id.linear2);
        list = new ArrayList<SurveyDetail>();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        rootView.setOnTouchListener(new SwipeTouchListener(getActivity()) {

            public void onSwipeRight() {
                super.onSwipeRight();

            }

            public void onSwipeLeft() {
                super.onSwipeLeft();
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

        });

        mRecyclerView.setOnTouchListener(new SwipeTouchListener(getActivity()) {

            public void onSwipeRight() {
                super.onSwipeRight();

            }

            public void onSwipeLeft() {
                super.onSwipeLeft();
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

        });

        btn_yourBalance.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Bundle bundle = new Bundle();
                bundle.putString("panel_id", panel_id);
                bundle.putString("panelist_id", panelist_id);
                bundle.putString("points", total_points);

                Fragment Frag = new YourSurveyFragment();
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
        //	Toast.makeText(getActivity(), "panel_id" + panel_id + "++++++++" + "panelist_id" + panelist_id, Toast.LENGTH_SHORT).show();

    }


    //Display survey data from server
    private void displayrecentSurvey() {
        // TODO Auto-generated method stub
        ArrayList<SurveyDetail> dblist = db.Get_All_Surveys(1);

        if (dblist.size() == 0) {

        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            card_view.setVisibility(View.GONE);
            txt_noSurvey.setVisibility(View.GONE);

            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
            adapter = new SurveyAdapter(this, dblist, font);
            mRecyclerView.setAdapter(adapter);
        }

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
                WebService service = new WebService(callbackRecentSurvey);

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("user_id", panelist_id));

                if(dblist.size() == 0){
                    service.getService(getActivity(), Constants.recentSurvey, params);
                }else{
                    service.getServiceNoLoader(getActivity(), Constants.recentSurvey, params, false);
                }


            } catch (NullPointerException e) {
                Log.e("OnClickLogin ==", "\n" + e.getMessage());
            }
        }

    }

    Callback callbackRecentSurvey = new Callback() {

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


    //method for parsing recent survey data
    protected void parseResult(JSONObject json) {
        // TODO Auto-generated method stub
        JSONObject result;
        String error_code;
        JSONObject resultset;
        JSONArray points_details;
        JSONObject balance;

        try {
            result = json.getJSONObject("Result");

            error_code = result.getString("ErrorCode");
            if (error_code.equals("0")) {
                resultset = result.getJSONObject("result");

                points_details = resultset.getJSONArray("points_details");
                int length = points_details.length();

                int myi = db.getSurveyCount(1);
                if (myi != length) {
                    balance = resultset.getJSONObject("balance");
                    for (int i = 0; i < length; i++) {
                        String date = points_details.getJSONObject(i).getString("date");
                        String description = points_details.getJSONObject(i).getString("reward_description");
                        String point = points_details.getJSONObject(i).getString("points");

                        SurveyDetail detail = new SurveyDetail();
                        detail.setDate(date);
                        detail.setDescrption(description);
                        int pt = Integer.parseInt(point);
                        detail.setPoints(pt);
                        //////
                        detail.setYrSrvyUrl("empty");
                        detail.setYrSrvyLoi("empty");
                        detail.setYrSurveyId("empty");
                        detail.setYrSrvyPts("empty");

                        list.add(detail);
                    }
                        db.deleteSurvey(1);
                        db.addSurvey(list, 1);

                    if (list.size() == 0) {
                        mRecyclerView.setVisibility(View.GONE);
                        card_view.setVisibility(View.VISIBLE);
                        txt_noSurvey.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerView.setVisibility(View.VISIBLE);
                        card_view.setVisibility(View.GONE);
                        txt_noSurvey.setVisibility(View.GONE);

                        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
                        adapter = new SurveyAdapter(this, getDataSet(), font);
                        mRecyclerView.setAdapter(adapter);
                    }

                    String min_redeemPts = balance.getString("minimum_redeem_points");

                    panel_logo = balance.getString("panel_logo");

                    Constants.BALANCE_POINTS = total_available;
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private ArrayList<SurveyDetail> getDataSet() {

        return list;
    }


    private class LoadImage extends AsyncTask<String, Void, Bitmap> {
        Bitmap bitmap;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override


        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null) {
                Log.v("Bitmap", "" + result);

            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments().containsKey("calledFrom")) {
            if (getArguments().getString("calledFrom").equalsIgnoreCase("redeem")) {
                linear2.setVisibility(View.GONE);
                TextView txt = (TextView) getActivity().findViewById(R.id.heading);
                txt.setText("Rewards");
                getActivity().findViewById(R.id.btnSurvey).setBackgroundDrawable(getResources().getDrawable(R.drawable.survey));
                getActivity().findViewById(R.id.btnRedeemRewards).setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards_selected));

                TextView txtRewards = (TextView) getActivity().findViewById(R.id.txtRewards);
                txtRewards.setTextColor(getResources().getColor(R.color.color_wexpand));

                RelativeLayout relativeRewards = (RelativeLayout) getActivity().findViewById(R.id.relativeRewards);
                relativeRewards.setBackgroundColor(getResources().getColor(R.color.white));

            }

            if (getArguments().getString("calledFrom").equalsIgnoreCase("yoursrvy")) {
                linear2.setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.btnSurvey).setBackgroundDrawable(getResources().getDrawable(R.drawable.survey_selected));
                getActivity().findViewById(R.id.btnRedeemRewards).setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards));
                TextView txt = (TextView) getActivity().findViewById(R.id.heading);
                txt.setText("Surveys");

                TextView txtSurvey = (TextView) getActivity().findViewById(R.id.txtSurvey);
                txtSurvey.setTextColor(getResources().getColor(R.color.color_wexpand));

                RelativeLayout relativeSurvey = (RelativeLayout) getActivity().findViewById(R.id.relativeSurvey);
                relativeSurvey.setBackgroundColor(getResources().getColor(R.color.white));

            }

            if (getArguments().getString("calledFrom").equalsIgnoreCase("my")) {
                linear2.setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.btnSurvey).setBackgroundDrawable(getResources().getDrawable(R.drawable.survey_selected));
                getActivity().findViewById(R.id.btnRedeemRewards).setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards));
                TextView txt = (TextView) getActivity().findViewById(R.id.heading);
                txt.setText("Surveys");

                TextView txtSurvey = (TextView) getActivity().findViewById(R.id.txtSurvey);
                txtSurvey.setTextColor(getResources().getColor(R.color.color_wexpand));

                RelativeLayout relativeSurvey = (RelativeLayout) getActivity().findViewById(R.id.relativeSurvey);
                relativeSurvey.setBackgroundColor(getResources().getColor(R.color.white));

            }
            if (getArguments().getString("calledFrom").equalsIgnoreCase("profile")) {
                linear2.setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.btnSurvey).setBackgroundDrawable(getResources().getDrawable(R.drawable.survey_selected));
                getActivity().findViewById(R.id.btnRedeemRewards).setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards));
                TextView txt = (TextView) getActivity().findViewById(R.id.heading);
                txt.setText("Surveys");

                TextView txtSurvey = (TextView) getActivity().findViewById(R.id.txtSurvey);
                txtSurvey.setTextColor(getResources().getColor(R.color.color_wexpand));

                RelativeLayout relativeSurvey = (RelativeLayout) getActivity().findViewById(R.id.relativeSurvey);
                relativeSurvey.setBackgroundColor(getResources().getColor(R.color.white));

            }

        } else {
            linear2.setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.btnMyAccount).setBackgroundDrawable(getResources().getDrawable(R.drawable.acct));
            getActivity().findViewById(R.id.btnSurvey).setBackgroundDrawable(getResources().getDrawable(R.drawable.survey_selected));
            getActivity().findViewById(R.id.btnInvite).setBackgroundDrawable(getResources().getDrawable(R.drawable.invite));
            getActivity().findViewById(R.id.btnRedeemRewards).setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards));
            getActivity().findViewById(R.id.btnProfile).setBackgroundDrawable(getResources().getDrawable(R.drawable.profile));

            TextView txtSurvey = (TextView) getActivity().findViewById(R.id.txtSurvey);
            txtSurvey.setTextColor(getResources().getColor(R.color.color_wexpand));

            RelativeLayout relativeSurvey = (RelativeLayout) getActivity().findViewById(R.id.relativeSurvey);
            relativeSurvey.setBackgroundColor(getResources().getColor(R.color.white));

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

            TextView txtInvite = (TextView) getActivity().findViewById(R.id.txtInvite);
            txtInvite.setTextColor(getResources().getColor(R.color.white));

            RelativeLayout relativeInvite = (RelativeLayout) getActivity().findViewById(R.id.relativeInvite);
            relativeInvite.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

        }

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
