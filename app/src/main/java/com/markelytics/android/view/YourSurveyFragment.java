package com.markelytics.android.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.markelytics.android.R;
import com.markelytics.android.controller.YourSurveyAdapter;
import com.markelytics.android.database.DatabaseHandler;
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

import java.util.ArrayList;
import java.util.List;


public class YourSurveyFragment extends Fragment// implements  OnItemClickListener
{
	TextView tv_noSrvy;
	String profile_percentage;
	String panel_id, panelist_id, total_available, total_earned;
	String loi, yrSrvyPts, surveyURL, surveyId;
	ArrayList<SurveyDetail> list;

	private String netstat="";
	String PREFS = "MyPrefs";
	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager mLayoutManager;
	CardView card_view;
	FontChangeCrawler fontChanger;
	Button  btn_yourBalance;
	LinearLayout btn_recentSurvey;
	int tearned, tavailable, tredeemed;
	DatabaseHandler db;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View rootView = inflater.inflate(R.layout.your_survey, container, false);
			
		init(rootView);
		getPanelistId();

		displayYourSurvey();
	    return rootView;
	}
	
	private void init(View rootView) 
	{
		// TODO Auto-generated method stub
		db = new DatabaseHandler(getActivity());
		list = new ArrayList<SurveyDetail>();
		tv_noSrvy = (TextView)rootView.findViewById(R.id.txt_noSrvy);
		card_view = (CardView) rootView.findViewById(R.id.card_view);
		btn_recentSurvey = (LinearLayout) rootView.findViewById(R.id.btn_recentSurvey);
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(mLayoutManager);
		btn_yourBalance = (Button) rootView.findViewById(R.id.btn_yourBalance);

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


		btn_recentSurvey.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("calledFrom", "yoursrvy");
				bundle.putString("panel_id", panel_id);
				bundle.putString("panelist_id", panelist_id);
				//bundle.putString("points", total_points);

				Fragment Frag = new RecentSurveyFragment();
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
		//Toast.makeText(getActivity(), "Activity to YourSurveyFragment " + "Panel id" + panel_id + "panelist id" + panelist_id, Toast.LENGTH_SHORT).show();
	}

	private void displayYourSurvey() 
	{
		// TODO Auto-generated method stub

		ArrayList<SurveyDetail> dblist = db.Get_All_Surveys(2);

		if(dblist.size()>0){
			Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
			adapter = new YourSurveyAdapter(this, dblist,font);
			mRecyclerView.setAdapter(adapter);
		}

		netstat = NetworkCheck.getNetwork(getActivity());
		if (netstat.equals("false")) {
//			AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
//			alertDialog.setMessage("No network available. Please check the internet connection");
//			alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					dialog.cancel();
//				}
//			});alertDialog.show();
		}
		else
		{
			try {
				//				JSONObject json = userFunction.loginUser(userNameText, passwordText);
				WebService service = new WebService(callbackYourSurvey);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("panelist_id", panelist_id));
				params.add(new BasicNameValuePair("panel_id", panel_id));

				if(dblist.size() == 0){
					service.getService(getActivity(), Constants.yourSurvey, params);
				}else{
					service.getServiceNoLoader(getActivity(), Constants.yourSurvey, params, false);
				}
			} 
			catch (NullPointerException e) 
			{
				Log.e("OnClickLogin ==", "\n" + e.getMessage());
			}
		}
	
	}
		
	Callback callbackYourSurvey = new Callback() {

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

	protected void parseResult(JSONObject json)
	{
		// TODO Auto-generated method stub
		JSONObject result = null;
		JSONArray survey;
		String error_code;
		list.clear();
		try {
			result = json.getJSONObject("Result");
			survey = result.getJSONArray("survey");
			int length = survey.length();
			
			error_code = result.getString("ErrorCode");
			if (error_code.equals("0"))
			{
				profile_percentage = result.getString("profile_complete_percentage");
				Constants.PERCENTAGE = profile_percentage;

				for (int i = 0; i < length; i++) {
					loi = survey.getJSONObject(i).getString("loi");
					surveyURL = survey.getJSONObject(i).getString("survey_URL");
					yrSrvyPts = survey.getJSONObject(i).getString("points");
					surveyId = survey.getJSONObject(i).getString("project_id");

					SurveyDetail detail = new SurveyDetail();
					detail.setYrSrvyLoi(loi);
					detail.setYrSrvyUrl(surveyURL);
					detail.setYrSrvyPts(yrSrvyPts);
					detail.setYrSurveyId(surveyId);
					detail.setPoints(0);
					detail.setDate("empty");
					detail.setDescrption("empty");
					list.add(detail);
				}

				db.deleteSurvey(2);
				db.addSurvey(list, 2);

				total_available = result.getString("total_available");
				Constants.TOTAL_AVAILABLE_POINTS = total_available;
				total_earned = result.getString("total_earned");
				Constants.TOTAL_EARNED_POINTS = total_earned;

				tearned = Integer.parseInt(total_earned);
				tavailable = Integer.parseInt(total_available);
				tredeemed = tearned - tavailable;
				String total_redeemed = String.valueOf(tredeemed);
				Constants.TOTAL_REDMMED_POINTS = total_redeemed;

				Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
				adapter = new YourSurveyAdapter(this, getDataSet(),font);
				adapter.notifyDataSetChanged();

				if (list.size() == 0)
				{

					Bundle bundle = new Bundle();
					bundle.putString("panel_id", panel_id);
					bundle.putString("panelist_id", panelist_id);
					bundle.putString("percentage", Constants.PERCENTAGE);

					Fragment Frag = new GetMoreSurveysFragment();
					Frag.setArguments(bundle);
					FragmentTransaction ft  = getFragmentManager().beginTransaction();
					ft.replace(R.id.frame_container, Frag);
					ft.addToBackStack(null);
					ft.commit();
				} else {
					mRecyclerView.setAdapter(adapter);
				}
			}
		} 
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(Constants.FLAG) {
			list.clear();

			Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
			adapter = new YourSurveyAdapter(this, getDataSet(), font);
			adapter.notifyDataSetChanged();

			mRecyclerView.setAdapter(adapter);

			displayYourSurvey();
		}
		Constants.FLAG = false;


			getActivity().findViewById(R.id.btnMyAccount).setBackgroundDrawable(getResources().getDrawable(R.drawable.acct));
			getActivity().findViewById(R.id.btnSurvey).setBackgroundDrawable(getResources().getDrawable(R.drawable.survey_selected));
			getActivity().findViewById(R.id.btnInvite).setBackgroundDrawable(getResources().getDrawable(R.drawable.invite));
			getActivity().findViewById(R.id.btnRedeemRewards).setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards));
			getActivity().findViewById(R.id.btnProfile).setBackgroundDrawable(getResources().getDrawable(R.drawable.profile));

			TextView txtSurvey = (TextView) getActivity().findViewById(R.id.txtSurvey);
		    txtSurvey.setTextColor(getResources().getColor(R.color.color_wexpand));

		    RelativeLayout relativeSurvey = (RelativeLayout) getActivity().findViewById(R.id.relativeSurvey);
			relativeSurvey.setBackgroundColor(getResources().getColor(R.color.white));

			TextView txt = (TextView) getActivity().findViewById(R.id.heading);
			txt.setText("Surveys");


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

	private ArrayList<SurveyDetail> getDataSet() {

		return list;
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
