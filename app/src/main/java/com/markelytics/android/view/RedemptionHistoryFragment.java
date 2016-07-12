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
import com.markelytics.android.controller.RedeemHstryAdapter;
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

public class RedemptionHistoryFragment extends Fragment
{


    ArrayList<SurveyDetail> list;
    ArrayList<SurveyDetail> redeemList;
    String panel_id, panelist_id ;
    TextView txtNohstry;
    private String netstat="";
    private RecyclerView mRecyclerViewRedeem;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    CardView card_view;
    DatabaseHandler db;

    FontChangeCrawler fontChanger;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View rootView = inflater.inflate(R.layout.fragment_redemption_history, container, false);

        init(rootView);
        getPanelistId();
        //dummy();
        displayrecentSurvey();
        return rootView;
    }

private void dummy() {
    Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
    for (int i = 0; i < 5; i++) {

        SurveyDetail detail = new SurveyDetail();
        detail.setDate("02/02/16");
        detail.setDescrption("survey reward");
        int pt = Integer.parseInt("100");
        detail.setPoints(pt);

        redeemList.add(detail);
    }
    adapter = new RedeemHstryAdapter(this, redeemList,font);
    mRecyclerViewRedeem.setAdapter(adapter);
}
    private void init(View rootView)
    {
        // TODO Auto-generated method stub

        db = new DatabaseHandler(getActivity());

        card_view = (CardView) rootView.findViewById(R.id.card_view);
        list = new ArrayList<SurveyDetail>();
        redeemList = new ArrayList<SurveyDetail>();
        txtNohstry = (TextView) rootView.findViewById(R.id.txtNohstry);

        mRecyclerViewRedeem = (RecyclerView) rootView.findViewById(R.id.recyclerviewRedeem);
        mRecyclerViewRedeem.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewRedeem.setLayoutManager(mLayoutManager);

        rootView.setOnTouchListener(new SwipeTouchListener(getActivity()) {

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

        mRecyclerViewRedeem.setOnTouchListener(new SwipeTouchListener(getActivity()) {

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




    }

    private void getPanelistId()
    {
        // TODO Auto-generated method stub
        panel_id = getArguments().getString("panel_id");
        panelist_id = getArguments().getString("panelist_id");
        //Toast.makeText(getActivity(), "Activity to RecentSurveyFragment" + "Panel id" + panel_id + "panelist id" + panelist_id, Toast.LENGTH_SHORT).show();
    }


    //Display survey data from server
    private void displayrecentSurvey()
    {

        // TODO Auto-generated method stub

        ArrayList<SurveyDetail> dblist = db.getAllHistory();
        if(dblist.size() != 0){
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
            adapter = new RedeemHstryAdapter(this, dblist,font);
            mRecyclerViewRedeem.setAdapter(adapter);
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
//            });alertDialog.show();
        }
        else
        {
            try {
                //				JSONObject json = userFunction.loginUser(userNameText, passwordText);
                WebService service = new WebService(callbackRecentSurvey);

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("panelist_id", panelist_id));
                params.add(new BasicNameValuePair("panel_id", panel_id));
                if(dblist.size() == 0){
                    service.getService(getActivity(), Constants.redeem_history, params);
                }else{
                    service.getServiceNoLoader(getActivity(), Constants.redeem_history, params, false);
                }
            }
            catch (NullPointerException e)
            {
                Log.e("OnClickLogin ==", "\n" + e.getMessage());
            }
        }

    }

    Callback callbackRecentSurvey = new Callback() {

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


    //method for parsing recent survey data
    protected void parseResult(JSONObject rootjson)
    {
        // TODO Auto-generated method stub

        JSONObject result;
        String error_code;
        JSONArray redeem_points;


        try
        {
            result = rootjson.getJSONObject("Result");

            error_code = result.getString("ErrorCode");
            if (error_code.equals("0"))
            {
                redeem_points = result.getJSONArray("redeem_points");


                for(int i=0; i<redeem_points.length(); i++)
                {
                    String date = redeem_points.getJSONObject(i).getString("addtime");
                    String description = redeem_points.getJSONObject(i).getString("reason_to_add");
                    String point = redeem_points.getJSONObject(i).getString("redeem_points");

                    SurveyDetail detail = new SurveyDetail();
                    detail.setDate(date);
                    detail.setDescrption(description);
                    int pt = Integer.parseInt(point);
                    detail.setPoints(pt);
                    //for database
                    redeemList.add(detail);
                }
            }

            if(db.getHistoryCount() != redeemList.size()){
                db.Delete_History();
                db.addHistory(redeemList);
                if(redeemList.size() > 0)
                {
                    Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
                    adapter = new RedeemHstryAdapter(this, redeemList,font);
                    mRecyclerViewRedeem.setAdapter(adapter);
                }
                else
                {
                    txtNohstry.setVisibility(View.VISIBLE);
                    mRecyclerViewRedeem.setVisibility(View.GONE);
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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



