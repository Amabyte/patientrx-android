package com.matrix.patientrx.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.matrix.patientrx.R;
import com.matrix.patientrx.models.Case;

public class CaseListAdapter extends BaseAdapter {
	private ArrayList<Case> mCaseList;
	private Context mContext;

	public CaseListAdapter(Context context, ArrayList<Case> caseList) {
		mContext = context;
		mCaseList = caseList;
	}

	@Override
	public int getCount() {
		return mCaseList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mCaseList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_case_item, null);
			holder = new ViewHolder();
			holder.imgDocReply = (ImageView) convertView
					.findViewById(R.id.imgDocReply);
			holder.textCaseDescription = (TextView) convertView
					.findViewById(R.id.textCaseDescription);
			holder.textCaseId = (TextView) convertView
					.findViewById(R.id.textCaseId);
			holder.textCaseTime = (TextView) convertView
					.findViewById(R.id.textCaseTime);
			holder.textDocReply = (TextView) convertView
					.findViewById(R.id.textDocReply);
			holder.textPatientName = (TextView) convertView
					.findViewById(R.id.textPatientName);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Case caseItem = mCaseList.get(position);
		if (caseItem.getTotal_new_case_comments_by_doctor() > 0) {
			holder.textDocReply.setText(caseItem
					.getTotal_new_case_comments_by_doctor());
			holder.imgDocReply.setVisibility(View.VISIBLE);
			holder.textDocReply.setVisibility(View.VISIBLE);
		} else {
			holder.imgDocReply.setVisibility(View.INVISIBLE);
			holder.textDocReply.setVisibility(View.INVISIBLE);
		}
		
		holder.textCaseDescription.setText("Fever");
		holder.textCaseId.setText(caseItem.getId()+"");
		holder.textCaseTime.setText(caseItem.getUpdated_at());
		holder.textPatientName.setText(caseItem.getName());
		return convertView;
	}

	private class ViewHolder {
		private ImageView imgDocReply;
		private TextView textDocReply;
		private TextView textCaseDescription;
		private TextView textCaseId;
		private TextView textPatientName;
		private TextView textCaseTime;
	}

}
