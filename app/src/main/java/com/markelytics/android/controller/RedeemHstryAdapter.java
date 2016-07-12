package com.markelytics.android.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.markelytics.android.R;
import com.markelytics.android.model.PartnerDetails;
import com.markelytics.android.model.SurveyDetail;
import com.markelytics.android.utils.Constants;
import com.markelytics.android.view.QuestionFragment;

import java.util.ArrayList;

public class RedeemHstryAdapter extends RecyclerView
        .Adapter<RedeemHstryAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    public static ArrayList<SurveyDetail> mDataset;
    private static MyClickListener myClickListener;
    private static Fragment fragment;
    private static Context context;
    Typeface font;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {

        TextView date, description, points;

        public DataObjectHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.listview_date);

            description = (TextView) itemView.findViewById(R.id.listview_description);

            points = (TextView)itemView.findViewById(R.id.listview_points);

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //myClickListener.onItemClick(getAdapterPosition(), v);

        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;

    }

    public RedeemHstryAdapter(Fragment fragment, ArrayList<SurveyDetail> myDataset, Typeface font) {
        mDataset = myDataset;
        this.fragment = fragment;
        this.font = font;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_redeem_history, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        holder.date.setText(mDataset.get(position).getDate());
        holder.description.setText(mDataset.get(position).getDescrption());
        holder.points.setText(String.valueOf(mDataset.get(position).getPoints()));

        holder.date.setTypeface(font);
        holder.description.setTypeface(font);
        holder.points.setTypeface(font);

        SurveyDetail detail = mDataset.get(position);

    }

    public void addItem(SurveyDetail dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
