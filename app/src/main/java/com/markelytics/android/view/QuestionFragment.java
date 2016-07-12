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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionFragment extends Fragment
{
	TextView tv_queCategory, tv_question, tv_questionNo;
	ImageView img_que;
	Button btn_finish  ;
	LinearLayout btn_nextQue,btn_previousQue;

	RadioGroup rg;
	private String netstat = "";
	String panel_id, panelist_id, category_id, category_name, question_id, total_ques, 
	question_text, question_type, question_html_type, next;
	String que_variableId, variable_txt, check_status;
	LinearLayout parentLayout, imgLayout;
	int length;
	String[][] variableArray;
	LayoutParams layoutparam ;
	LayoutParams layoutChildParam;
	String ans_id = "";
	CheckBox chkbox = null;
	RadioButton rb;
	Spinner ansSpinner;
	String last_que = "2";
	String next_que = "1";

	String q_id = "0";
	int qId = 0;
	int count = 1;
	FontChangeCrawler fontChanger;
	int queNo = 0;
	String QuestionNo="";
	private boolean flagPrevious = false;
	boolean checkStatusArray[];
	String ans_array[];

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.question, container, false);

		init(rootView);
		getPanelistId();
		//firstQue();
		displayQuestion();
		return rootView;
	}

	private void init(View rootView)
	{
		// TODO Auto-generated method stub
		tv_queCategory = (TextView)rootView.findViewById(R.id.txt_queCategory);
		tv_question = (TextView)rootView.findViewById(R.id.txt_question);
		tv_questionNo = (TextView)rootView.findViewById(R.id.txt_queNo);

		parentLayout = (LinearLayout)rootView.findViewById(R.id.parent_layout);
		imgLayout = (LinearLayout)rootView.findViewById(R.id.linear_img);
		layoutparam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layoutChildParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		btn_finish = (Button)rootView.findViewById(R.id.btn_finish);
		btn_nextQue = (LinearLayout)rootView.findViewById(R.id.btn_nextQue);
		btn_previousQue = (LinearLayout)rootView.findViewById(R.id.btn_previousQue);

	}

	private void getPanelistId()
	{
		// TODO Auto-generated method stub
		panel_id = getArguments().getString("panel_id");
		panelist_id = getArguments().getString("panelist_id");
		category_id = getArguments().getString("category_id");
		category_name = getArguments().getString("category_name");
		//Toast.makeText(getActivity(), "Activity to QuestionFragment" + "Panel id" + panel_id + "panelist id" + panelist_id + "category_id" + category_id, Toast.LENGTH_SHORT).show();
		tv_queCategory.setText(category_name);
	}

	//Display first question
	private void displayQuestion()
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
				WebService service = new WebService(callbackQuestion);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				System.out.println("panelId"+panel_id+"panelist_id"+panelist_id);
				params.add(new BasicNameValuePair("panelist_id", panelist_id));
				params.add(new BasicNameValuePair("pid", panel_id));
				params.add(new BasicNameValuePair("cid", category_id));
				q_id = String.valueOf(qId);
				queNo = qId+1;
				params.add(new BasicNameValuePair("qid", q_id));

				// Bydefault 0

				service.getService(getActivity(), Constants.displayQues, params);

			}
			catch (NullPointerException e)
			{
				Log.e("in OnClickLogin exception ==", "\n" + e.getMessage());
			}
		}

	}

	Callback callbackQuestion = new Callback() {

		@Override
		public void onSuccess(int reqestcode, JSONObject rootjson)
		{
			// TODO Auto-generated method stub
			Log.v("Markelytics", "json result="+rootjson);
			parseResult(rootjson);

		}

		@Override
		public void onError(int reqestcode, String error)
		{
			// TODO Auto-generated method stub

		}
	};




	//To parse details of first question
	protected void parseResult(JSONObject json)
	{
		// TODO Auto-generated method stub
		JSONObject result;
		JSONObject resultset = null;
		JSONArray variables;
		String error_code;


		try
		{
			result = json.getJSONObject("Result");
			resultset = result.getJSONObject("result");
			variables = resultset.getJSONArray("variables");

			length = variables.length();
			variableArray = new String[length][3];
			error_code = result.getString("ErrorCode");
			if (error_code.equals("0"))
			{
				if(parentLayout != null)
				{
					parentLayout.removeAllViews();
				}

				category_id = resultset.getString("category");
				question_id = resultset.getString("question_id");
				total_ques = resultset.getString("total_question");

				question_text = resultset.getString("question_text");
				question_type = resultset.getString("question_type");
				question_html_type = resultset.getString("question_html_type");
				if(checkStatusArray == null){
					checkStatusArray = new boolean[Integer.parseInt(total_ques)];

					for(int i=0;i<Integer.parseInt(total_ques);i++)
					{
						checkStatusArray[i]=false;
					}
				}
				else
				{

					if(!flagPrevious == true)
					{
						qId++;
					}
					else
						flagPrevious = false;


				}

				String question_detail = "category:"+category_id+"\nques_id:"+question_id+
						"\ntotal_que:"+total_ques+"\nque_txt:"+question_text+"\nque_type:"+question_type+
						"\nque_htmlType:"+question_html_type;

				System.out.println("---------Question detail------------ \n" + question_detail);
				if(!question_text.equalsIgnoreCase("null"))
				{
					tv_question.setText(question_text);
				}

				QuestionNo = String.valueOf(queNo);
				tv_questionNo.setText(QuestionNo+") ");



				for(int i=0; i<length; i++)
				{
					que_variableId = variables.getJSONObject(i).getString("qst_variable_id");
					variable_txt = variables.getJSONObject(i).getString("variable_text");
					check_status = variables.getJSONObject(i).getString("check_status");

					variableArray[i][0] = que_variableId;
					variableArray[i][1] = variable_txt;
					variableArray[i][2] = check_status;

				}

				if(question_html_type.equals("0"))
				{
					createUIforRadioButton();

					displayNextOrLastQue();

				}
				else if(question_html_type.equals("1"))
				{
					createUIforCheckbox();

					displayNextOrLastQue();
				}
				else if(question_html_type.equals("2"))
				{
					createUIforDropDown();

					displayNextOrLastQue();
				}
			}

		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void displayNextOrLastQue() {
		// TODO Auto-generated method stub

		int t1 = qId+1;
		if(total_ques.equalsIgnoreCase("1")
				|| total_ques.equalsIgnoreCase(String.valueOf(t1))) //(String.valueOf(count)))
		{

			if(total_ques.equalsIgnoreCase("1"))
			{
				btn_previousQue.setVisibility(View.GONE);
			}
			else
			{
				btn_previousQue.setVisibility(View.VISIBLE);
				btn_previousQue.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						ans_id = "";

						flagPrevious = true;
						qId--;

						displayQuestion();
					}
				});
			}

			btn_finish.setVisibility(View.VISIBLE);
			btn_nextQue.setVisibility(View.GONE);

			btn_finish.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(question_html_type.equals("0"))
					{
						System.out.println("RadioButton Answers::::::::::::"+ans_id);

					}
					else if(question_html_type.equals("1"))
					{
						System.out.println("CheckBox Answer::::::::::::"+ans_id);
						 if (ans_id.startsWith(","))
						 {
						        ans_id = ans_id.substring(1);
						 }
						 System.out.println("CheckBox Answer::::::::::::"+ans_id);
					}
					else if(question_html_type.equals("2"))
					{
						ans_id = ansSpinner.getSelectedItem().toString();
						for(int i=0; i<length; i++)
						{
							if(ans_id.equalsIgnoreCase(variableArray[i][1]))
							{
								ans_id = variableArray[i][0];
							}

						}
					}
					next = last_que;
					saveQuestionDetails();
					count=0;

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
		else
		{

			if(qId == 0)
			{
				btn_previousQue.setVisibility(View.GONE);
			}
			else
			{
				btn_previousQue.setVisibility(View.VISIBLE);
				btn_previousQue.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						ans_id = "";

						flagPrevious = true;

						qId--;

						displayQuestion();
					}
				});
			}


			btn_finish.setVisibility(View.GONE);
			btn_nextQue.setVisibility(View.VISIBLE);
			btn_nextQue.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v)
				{
					// TODO Auto-generated method stub
					queNo = queNo+1;

					if(question_html_type.equals("0"))
					{
						System.out.println("RadioButton Answers::::::::::::"+ans_id);

					}
					else if(question_html_type.equals("1"))
					{
						System.out.println("CheckBox Answer::::::::::::"+ans_id);
						if (ans_id.startsWith(","))
						 {
						        ans_id = ans_id.substring(1);
						 }
						 System.out.println("CheckBox Answer::::::::::::"+ans_id);
					}
					else if(question_html_type.equals("2"))
					{
						ans_id = ansSpinner.getSelectedItem().toString();
						for(int i=0; i<length; i++)
						{
							if(ans_id.equalsIgnoreCase(variableArray[i][1]))
							{
								ans_id = variableArray[i][0];
							}
							System.out.println("Dropdown Answers::::::::::::"+ans_id);
						}
					}

					if(!ans_id.equalsIgnoreCase(""))
					{
						checkStatusArray[qId]=true;
					}
					else
					{
						checkStatusArray[qId]=false;
					}

					int total = Integer.parseInt(total_ques);

					if(qId == total-1)
					{
						next = last_que;

						saveQuestionDetails();

						btn_finish.setVisibility(View.VISIBLE);
						btn_nextQue.setVisibility(View.GONE);

					}
					else
					{
						next = next_que;

						saveQuestionDetails();

					}

				}
			});

		}

	}

	private void createUIforDropDown()
	{
		// TODO Auto-generated method stub

		if(imgLayout != null)
		{
			imgLayout.removeAllViews();
		}

		ansSpinner = new Spinner(getActivity());
		ansSpinner.setLayoutParams(layoutChildParam);

		List<String> ansSpinnerList = new ArrayList<String>();
		for(int j=0; j<length; j++)
		{
			ansSpinnerList.add(variableArray[j][1]);

		}
		ArrayAdapter<String> paymentAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, ansSpinnerList);
		paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ansSpinner.setAdapter(paymentAdapter);

		for (int i = 0; i < ansSpinnerList.size(); i++)
		{
			if(variableArray[i][2].equalsIgnoreCase("checked"))
			{

				ansSpinner.setSelection(i);
				break;

			}

		}
		tv_queCategory.setText(category_name);

		int que = Integer.parseInt(total_ques);
		for(int i=0; i<que; i++)
		{

			if(checkStatusArray[i] == true)
			{
				img_que = new ImageView(getActivity());
				img_que.setLayoutParams(layoutChildParam);
				img_que.setPadding(3, 3, 3, 3);

				img_que.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_ans));
				imgLayout.addView(img_que);
			}
			else
			{
				img_que = new ImageView(getActivity());
				img_que.setLayoutParams(layoutChildParam);

				img_que.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_que));
				imgLayout.addView(img_que);
			}

		}

		parentLayout.addView(ansSpinner);

	}

	private void createUIforCheckbox()
	{
		// TODO Auto-generated method stub
		ans_array = new String[length];
		LinearLayout child_layout = null;

		if(imgLayout != null)
		{
			imgLayout.removeAllViews();
		}

		TextView chkTxt = null;
		for(int j=0; j<length; j++)
		{
			child_layout = new LinearLayout(getActivity());
			child_layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			child_layout.setOrientation(LinearLayout.HORIZONTAL);

			chkbox = new CheckBox(getActivity());
			chkbox.setLayoutParams(layoutChildParam);

			if(variableArray[j][2].equalsIgnoreCase("checked"))
			{
				ans_array[j] = variableArray[j][0];

				ans_id = ans_id+","+variableArray[j][0];

				checkStatusArray[qId]=true;

				if(checkStatusArray[qId] == true)
				{

				}
				chkbox.setChecked(true);
			}
			chkbox.setTag(variableArray[j][0]);

			chkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if (isChecked) {
						ans_id = ans_id + "," + buttonView.getTag().toString();
						System.out.println("Get Tag:" + ans_id);
						if (ans_id.startsWith(",")) {
							ans_id = ans_id.substring(1);
						}
					} else {
						String arr[] = ans_id.split(",");
						String temp_ansId = "";
						for (int i = 0; i < arr.length; i++) {
							if (arr[i].equalsIgnoreCase(buttonView.getTag().toString())) {

							} else {
								temp_ansId = temp_ansId + "," + arr[i];
							}
						}
						ans_id = temp_ansId;
						if (ans_id.startsWith(",")) {
							ans_id = ans_id.substring(1);
						}
					}
				}
			});
			////////////////////////new////////////////////////////////////
			if(!variableArray[j][1].equalsIgnoreCase("null"))
			{
				chkbox.setText(variableArray[j][1]);
			}
			//chkbox.setText(variableArray[j][1]);
			chkbox.setTextColor(getResources().getColor(R.color.black));
			chkbox.setTextSize(17);

			child_layout.addView(chkbox);

			parentLayout.addView(child_layout);
		}

		if(total_ques.equalsIgnoreCase("1"))
		{
			child_layout = new LinearLayout(getActivity());
			child_layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.5f));
			child_layout.setOrientation(LinearLayout.HORIZONTAL);
		}

		int que = Integer.parseInt(total_ques);
		for(int i=0; i<que; i++)
		{

			if(checkStatusArray[i] == true)
			{
				img_que = new ImageView(getActivity());
				img_que.setLayoutParams(layoutChildParam);
				img_que.setPadding(3, 3, 3, 3);

				img_que.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_ans));
				imgLayout.addView(img_que);
			}
			else
			{
				img_que = new ImageView(getActivity());
				img_que.setLayoutParams(layoutChildParam);

				img_que.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_que));
				imgLayout.addView(img_que);
			}

		}

	}

	private void createUIforRadioButton()
	{
		// TODO Auto-generated method stub
		
		rg= new RadioGroup(getActivity());
		rg.setLayoutParams(layoutparam);
		//----------------------------------------

		if(imgLayout != null)
		{
			imgLayout.removeAllViews();
		}

		

		for(int j=0; j<length; j++)
		{

			rb = new RadioButton(getActivity());
			rb.setLayoutParams(layoutparam);
			rb.setText(variableArray[j][1]);
			if(variableArray[j][2].equalsIgnoreCase("checked") )
			{
				ans_id = variableArray[j][0];
				checkStatusArray[qId]=true;
				
				if(checkStatusArray[qId] == true)
				{

				}

				System.out.println("Set tag: "+ans_id);

				rb.setChecked(true);

			}
			
			rb.setTag(variableArray[j][0]);

			rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub

					if(isChecked)
					{
						ans_id = ""+buttonView.getTag();
						
						radioButtonCheckChangeListener();
					}

				}
			});

			rg.addView(rb);
					
		}


		int que = Integer.parseInt(total_ques);
		for(int i=0; i<que; i++)
		{
			if(checkStatusArray[i])
			{
				img_que = new ImageView(getActivity());
				img_que.setLayoutParams(layoutChildParam);
				img_que.setPadding(3, 3, 3, 3);
				img_que.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_ans));
				imgLayout.addView(img_que);
			}
			else
			{
				img_que = new ImageView(getActivity());
				img_que.setLayoutParams(layoutChildParam);

				img_que.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_que));
				imgLayout.addView(img_que);
			}

		}
		
		parentLayout.addView(rg);


	}


	private void radioButtonCheckChangeListener(){
		
		parentLayout.removeAllViews();
		rg = new RadioGroup(getActivity());
		rg.setLayoutParams(layoutparam);
		for(int j =0; j<length; j++)
		{
			rb = new RadioButton(getActivity());
			rb.setLayoutParams(layoutparam);
			rb.setText(variableArray[j][1]);
			if(ans_id.equalsIgnoreCase(variableArray[j][0]))
			{

				rb.setChecked(true);
			}

			rb.setTag(variableArray[j][0]);
			rb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked)
					{
						ans_id = ""+buttonView.getTag();
						
						radioButtonCheckChangeListener();
					}
				}
			});
			rg.addView(rb);
		}
		
		parentLayout.addView(rg);
	}


	private void saveQuestionDetails() 
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
				WebService service = new WebService(callbackQuestion);

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				System.out.println("panelId"+panel_id+"panelist_id"+panelist_id);
				params.add(new BasicNameValuePair("panelist_id", panelist_id));
				params.add(new BasicNameValuePair("pid", panel_id));
				params.add(new BasicNameValuePair("cid", category_id));
				
				if(!ans_id.equalsIgnoreCase(""))
				{
					checkStatusArray[qId]=true;
				}
				q_id = String.valueOf(qId+1);

				params.add(new BasicNameValuePair("qid", q_id));
				params.add(new BasicNameValuePair("question_id", question_id));
				params.add(new BasicNameValuePair("variable_id", ans_id));
				params.add(new BasicNameValuePair("next", next));
				System.out.println("\n++++++++++++++++Before saving+++++++++++++++++\n panelist_id:"+panelist_id+"\n panel_id:"+panel_id+"\ncategory_id:"+category_id
						+"\nq_id:"+q_id+"\nquestion_id:"+question_id+"\nans_id:"+ans_id+"\nnext:"+next+"\n");
				
				ans_id = "";
				service.getService(getActivity(), Constants.displayQues, params);

			} 
			catch (NullPointerException e) 
			{
				Log.e("in OnClickLogin exception ==", "\n" + e.getMessage());	
			}

		}

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
