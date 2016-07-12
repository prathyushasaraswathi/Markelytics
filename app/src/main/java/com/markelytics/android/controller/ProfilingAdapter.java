package com.markelytics.android.controller;import android.graphics.Color;import android.graphics.Typeface;import android.support.v4.app.FragmentManager;import android.content.Context;import android.os.Bundle;import android.support.v4.app.Fragment;import android.support.v7.widget.RecyclerView;import android.util.Log;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.TextView;import com.markelytics.android.R;import com.markelytics.android.model.PartnerDetails;import com.markelytics.android.utils.Constants;import com.markelytics.android.view.QuestionFragment;import java.util.ArrayList;public class ProfilingAdapter extends RecyclerView		.Adapter<ProfilingAdapter		.DataObjectHolder> {	private static String LOG_TAG = "MyRecyclerViewAdapter";	public static ArrayList<PartnerDetails> mDataset;	private static MyClickListener myClickListener;	private static Fragment fragment;	Typeface font;	static Context c;	public static class DataObjectHolder extends RecyclerView.ViewHolder			implements View			.OnClickListener {		TextView profilingName, profilingStatus, profilingId;		public DataObjectHolder(View itemView) {			super(itemView);			profilingId = (TextView) itemView.findViewById(R.id.listview_categoryId);			profilingName = (TextView) itemView.findViewById(R.id.listview_profilingName);			profilingStatus = (TextView)itemView.findViewById(R.id.listview_profilingStatus);			Log.i(LOG_TAG, "Adding Listener");			itemView.setOnClickListener(this);			/*itemView.setOnTouchListener(new SwipeTouchListener(c) {				public void onSwipeRight() {					super.onSwipeRight();					Bundle bundle = new Bundle();					bundle.putString("panel_id", Constants.PANEL_ID);					bundle.putString("panelist_id", Constants.PANELIST_ID);					//bundle.putString("points", total_points);					Fragment Frag = new SurveyFragment();					Frag.setArguments(bundle);					FragmentManager fragmentManager = fragment.getFragmentManager();					fragmentManager.beginTransaction()							.replace(R.id.frame_container, Frag).addToBackStack("my_fragment").commit();				}				public void onSwipeLeft() {					super.onSwipeLeft();					Bundle bundle = new Bundle();					bundle.putString("panel_id",  Constants.PANEL_ID);					bundle.putString("panelist_id", Constants.PANELIST_ID);					//bundle.putString("points", total_points);					Fragment Frag = new RedeemrewardsFragment();					Frag.setArguments(bundle);					FragmentManager fragmentManager = fragment.getFragmentManager();					fragmentManager.beginTransaction()							.replace(R.id.frame_container, Frag).addToBackStack("my_fragment").commit();				}			});*/		}		@Override		public void onClick(View v) {			//myClickListener.onItemClick(getAdapterPosition(), v);			int position = getAdapterPosition();			//String date = mDataset.get(position);			System.out.println("+++++++++++++++++++++++"+position);			String id = mDataset.get(position).getProfilingId();			String name = mDataset.get(position).getProfilingName();			String status = mDataset.get(position).getProfilingStatus();			Bundle bundle = new Bundle();			bundle.putString("panel_id", Constants.PANEL_ID);			bundle.putString("panelist_id", Constants.PANELIST_ID);			bundle.putString("category_id", id);			bundle.putString("category_name", name);			Fragment Frag = new QuestionFragment();			Frag.setArguments(bundle);			FragmentManager fragmentManager = fragment.getFragmentManager();			fragmentManager.beginTransaction()					.replace(R.id.frame_container, Frag).addToBackStack("my_fragment").commit();		}	}	public void setOnItemClickListener(MyClickListener myClickListener) {		this.myClickListener = myClickListener;	}	public ProfilingAdapter(Fragment fragment, ArrayList<PartnerDetails> myDataset, Typeface font) {		mDataset = myDataset;		this.fragment = fragment;		this.font = font;	}	@Override	public DataObjectHolder onCreateViewHolder(ViewGroup parent,											   int viewType) {		View view = LayoutInflater.from(parent.getContext())				.inflate(R.layout.content_further_profiling, parent, false);		DataObjectHolder dataObjectHolder = new DataObjectHolder(view);		return dataObjectHolder;	}	@Override	public void onBindViewHolder(DataObjectHolder holder, int position) {		holder.profilingId.setText(mDataset.get(position).getProfilingId());		holder.profilingName.setText(mDataset.get(position).getProfilingName());		holder.profilingStatus.setText(mDataset.get(position).getProfilingStatus());		holder.profilingName.setTypeface(font);		holder.profilingStatus.setTypeface(font);		PartnerDetails detail = mDataset.get(position);		if (detail.getProfilingStatus().equalsIgnoreCase("revise"))		{			holder.profilingStatus.setBackgroundResource(R.drawable.style_revise);			holder.profilingStatus.setTextColor(Color.parseColor("#000000"));		}		else if(detail.getProfilingStatus().equalsIgnoreCase("Take now"))		{			holder.profilingStatus.setBackgroundResource(R.drawable.style_textbackground);			holder.profilingStatus.setTextColor(Color.parseColor("#FFFFFF"));		}		else		{			holder.profilingStatus.setBackgroundResource(R.drawable.style_red);			holder.profilingStatus.setTextColor(Color.parseColor("#000000"));		}	}	public void addItem(PartnerDetails dataObj, int index) {		mDataset.add(index, dataObj);		notifyItemInserted(index);	}	public void deleteItem(int index) {		mDataset.remove(index);		notifyItemRemoved(index);	}	@Override	public int getItemCount() {		return mDataset.size();	}	interface MyClickListener {		public void onItemClick(int position, View v);	}}