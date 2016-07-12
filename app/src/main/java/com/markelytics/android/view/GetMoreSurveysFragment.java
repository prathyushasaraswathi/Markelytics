package com.markelytics.android.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.markelytics.android.R;
import com.markelytics.android.network.NetworkCheck;
import com.markelytics.android.utils.Constants;
import com.markelytics.android.webservice.WebService;
import com.markelytics.android.webservice.WebService.Callback;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetMoreSurveysFragment extends Fragment implements OnClickListener
{
	Button btn_viewProfile;
	String panel_id, panelist_id, percentage;
	ProgressBar progressbar;
	TextView tv_progressTxt;
	private String netstat="";
	FontChangeCrawler fontChanger;
	LinearLayout btn_recentSurvey;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.profile_completness, container, false);

		 init(rootView);

		 getPanelistId();
		 parseResult();
		 return rootView;
	}

	private void getPanelistId() 
	{
		// TODO Auto-generated method stub
		panel_id = getArguments().getString("panel_id");   
		panelist_id = getArguments().getString("panelist_id");
		percentage = getArguments().getString("percentage");
		//Toast.makeText(getActivity(), "panel_id"+panel_id+"++++++++"+"panelist_id"+panelist_id, Toast.LENGTH_SHORT).show();
	}

	private void init(View rootView)
	{
		// TODO Auto-generated method stub


		progressbar = (ProgressBar)rootView.findViewById(R.id.progressbar);
		tv_progressTxt = (TextView)rootView.findViewById(R.id.txt_progressTxt);
		btn_viewProfile = (Button)rootView.findViewById(R.id.btn_viewProfile);
		btn_recentSurvey = (LinearLayout) rootView.findViewById(R.id.btn_recentSurvey);
		btn_recentSurvey.setOnClickListener(this);
		btn_viewProfile.setOnClickListener(this);

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
				FragmentTransaction ft  = getFragmentManager().beginTransaction();
				ft.replace(R.id.frame_container, Frag);
				ft.addToBackStack(null);
				ft.commit();

			}

		});
	}
	
	private void displayYourSurvey() 
	{
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
			});alertDialog.show();
		}
		else
		{
			try {
				//				JSONObject json = userFunction.loginUser(userNameText, passwordText);
				WebService service = new WebService(callbackYourSurvey);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("panelist_id", panelist_id));
				params.add(new BasicNameValuePair("panel_id", panel_id));
				
				service.getService(getActivity(), Constants.yourSurvey, params);
				
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
			Log.v("Ennovate", "json result="+rootjson);
			//parseResult(rootjson);
			
		}

		@Override
		public void onError(int reqestcode, String error) 
		{
			// TODO Auto-generated method stub

		}
	};

	

	protected void parseResult()
	{
		// TODO Auto-generated method stub

		tv_progressTxt.setText(percentage+"%");
		int progress = Integer.parseInt(percentage);
		progressbar.setProgress(progress);
	}
	
	@Override
	public void onClick(View v) 
	{
		// TODO Auto-generated method stub
		Bundle bundle;
		Fragment Frag;
		FragmentTransaction ft;
		switch (v.getId()) 
		{
			case R.id.btn_viewProfile:
				
				bundle = new Bundle();
				bundle.putString("calledFrom", "GetMoreSrvy");
				bundle.putString("panel_id", panel_id);
				bundle.putString("panelist_id", panelist_id);
				// set Fragmentclass Arguments
				Frag = new ProfilingFragment();
				Frag.setArguments(bundle);
				ft  = getFragmentManager().beginTransaction();
				ft.replace(R.id.frame_container, Frag);
				ft.addToBackStack(null);
				ft.commit();
				
				break;

			case R.id.btn_recentSurvey:

				bundle = new Bundle();
				bundle.putString("calledFrom", "my");
				bundle.putString("panel_id", panel_id);
				bundle.putString("panelist_id", panelist_id);
				// set Fragmentclass Arguments
				Frag = new RecentSurveyFragment();
				Frag.setArguments(bundle);
				ft  = getFragmentManager().beginTransaction();
				ft.replace(R.id.frame_container, Frag);
				ft.addToBackStack(null);
				ft.commit();

				break;


			default:
				break;
		}
		
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().findViewById(R.id.btnMyAccount).setBackgroundDrawable(getResources().getDrawable(R.drawable.acct));
		getActivity().findViewById(R.id.btnSurvey).setBackgroundDrawable(getResources().getDrawable(R.drawable.survey_selected));
		getActivity().findViewById(R.id.btnInvite).setBackgroundDrawable(getResources().getDrawable(R.drawable.invite));
		getActivity().findViewById(R.id.btnRedeemRewards).setBackgroundDrawable(getResources().getDrawable(R.drawable.rewards));
		getActivity().findViewById(R.id.btnProfile).setBackgroundDrawable(getResources().getDrawable(R.drawable.profile));

		TextView txt = (TextView) getActivity().findViewById(R.id.heading);
		txt.setText("Surveys");

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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

		fontChanger = new FontChangeCrawler(getActivity().getAssets());
		fontChanger.replaceFonts((ViewGroup) getActivity().findViewById(android.R.id.content));

	}
}
