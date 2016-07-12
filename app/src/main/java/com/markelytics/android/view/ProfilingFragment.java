package com.markelytics.android.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.markelytics.android.R;
import com.markelytics.android.controller.ProfilingAdapter;
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

import java.util.ArrayList;
import java.util.List;

public class ProfilingFragment extends Fragment
{

	ArrayList<PartnerDetails> list;

	private String netstat = "";
	String panel_id, panelist_id, category_id, categoryName,categoryStatus ;

	private RecyclerView mRecyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager mLayoutManager;
	FontChangeCrawler fontChanger;
	DatabaseHandler db;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) 
	{
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.profiling_info, container,
				false);

		init(rootView);
		// displayDa();
		getPanelistId();
		displayCategory();
		return rootView;
	}

	private void init(View rootView) 
	{
		// TODO Auto-generated method stub
		db = new DatabaseHandler(getActivity());
		list = new ArrayList<PartnerDetails>();

		netstat = NetworkCheck.getNetwork(getActivity());

		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getActivity());
		mRecyclerView.setLayoutManager(mLayoutManager);

		rootView.setOnTouchListener(new SwipeTouchListener(getActivity()) {

			public void onSwipeRight() {
				super.onSwipeRight();
				Bundle bundle = new Bundle();
				bundle.putString("calledFrom", "profile");
				bundle.putString("panel_id", panel_id);
				bundle.putString("panelist_id", panelist_id);

				Fragment Frag = new RecentSurveyFragment();
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

				Fragment Frag = new RedeemPointsFragment();
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
				Bundle bundle = new Bundle();
				bundle.putString("calledFrom", "profile");
				bundle.putString("panel_id", panel_id);
				bundle.putString("panelist_id", panelist_id);

				Fragment Frag = new RecentSurveyFragment();
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

				Fragment Frag = new RedeemPointsFragment();
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
		//Toast.makeText(getActivity(), "panel_id" + panel_id + "++++++++" + "panelist_id" + panelist_id, Toast.LENGTH_SHORT).show();

	}

	private void displayCategory() 
	{
		// TODO Auto-generated method stub

		ArrayList<PartnerDetails> dblist = db.getAllFurther();

		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
		if(dblist.size() >0){
			adapter = new ProfilingAdapter(this, dblist,font);
			mRecyclerView.setAdapter(adapter);
		}


		netstat = NetworkCheck.getNetwork(getActivity());
		if (netstat.equals("false"))
		{
//			AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//					getActivity());
//			alertDialog
//					.setMessage("No network available. Please check the internet connection");
//			alertDialog.setPositiveButton("Ok",
//					new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which)
//						{
//							dialog.cancel();
//						}
//					});
//			alertDialog.show();
		} 
		else 
		{
			try 
			{
				WebService service = new WebService(
						callbackProfilingDetails);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				System.out.println("panelId" + panel_id + "panelist_id"
						+ panelist_id);
				params.add(new BasicNameValuePair("user_id", panelist_id));
				params.add(new BasicNameValuePair("panel_id", panel_id));

				if(dblist.size() == 0){
					service.getService(getActivity(), Constants.displayCategory,
							params);
				}
				else{
					service.getServiceNoLoader(getActivity(), Constants.displayCategory, params, false);
				}



			} 
			catch (NullPointerException e) 
			{
				Log.e("OnClickLogin ex ==", "\n" + e.getMessage());
			}
		}

	}

	Callback callbackProfilingDetails = new Callback() {

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
		JSONObject result;
		String error_code;
		JSONArray resultset;

		try {
			result = json.getJSONObject("Result");

			error_code = result.getString("ErrorCode");
			if (error_code.equals("0")) 
			{
				resultset = result.getJSONArray("result");
				int length = resultset.length();

				for (int i = 0; i < length; i++) 
				{
					category_id = resultset.getJSONObject(i).getString("category_id");
					categoryName = resultset.getJSONObject(i).getString(
							"category_name");
					categoryStatus = resultset.getJSONObject(i)
							.getString("category_message");

					PartnerDetails detail = new PartnerDetails();
					detail.setProfilingId(category_id);
					detail.setProfilingName(categoryName);
					detail.setProfilingStatus(categoryStatus);

					list.add(detail);

				}

				db.Delete_FURTHER();
				db.addFurther(list);
				Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Lato-Regular.ttf");
				adapter = new ProfilingAdapter(this, getDataSet(),font);

				mRecyclerView.setAdapter(adapter);
			}

		} 
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	private ArrayList<PartnerDetails> getDataSet() {

		return list;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().findViewById(R.id.btnMyAccount).setBackgroundDrawable(getResources().getDrawable(R.drawable.acct));
		getActivity().findViewById(R.id.btnSurvey).setBackgroundDrawable(getResources().getDrawable(R.drawable.survey));
		getActivity().findViewById(R.id.btnInvite).setBackgroundDrawable(getResources().getDrawable(R.drawable.invite));
		getActivity().findViewById(R.id.btnRedeemRewards).setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards));
		getActivity().findViewById(R.id.btnProfile).setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_selected));

		TextView txt = (TextView) getActivity().findViewById(R.id.heading);
		txt.setText("Profile");

		TextView txtRewards = (TextView) getActivity().findViewById(R.id.txtRewards);
		txtRewards.setTextColor(getResources().getColor(R.color.white));

		RelativeLayout relativeRewards = (RelativeLayout) getActivity().findViewById(R.id.relativeRewards);
		relativeRewards.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

		TextView txtProfile = (TextView) getActivity().findViewById(R.id.txtProfile);
		txtProfile.setTextColor(getResources().getColor(R.color.color_wexpand));

		RelativeLayout relativeProfile = (RelativeLayout) getActivity().findViewById(R.id.relativeProfile);
		relativeProfile.setBackgroundColor(getResources().getColor(R.color.white));

		TextView txtSurvey = (TextView) getActivity().findViewById(R.id.txtSurvey);
		txtSurvey.setTextColor(getResources().getColor(R.color.white));

		RelativeLayout relativeSurvey = (RelativeLayout) getActivity().findViewById(R.id.relativeSurvey);
		relativeSurvey.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

		TextView txtAccount = (TextView) getActivity().findViewById(R.id.txtAccount);
		txtAccount.setTextColor(getResources().getColor(R.color.white));

		RelativeLayout relativeAccount = (RelativeLayout) getActivity().findViewById(R.id.relativeAccount);
		relativeAccount.setBackgroundColor(getResources().getColor(R.color.color_wexpand));

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

