package com.matrix.patientrx.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.matrix.patientrx.R;
import com.matrix.patientrx.models.Case;
import com.matrix.patientrx.models.Comment;
import com.matrix.patientrx.utils.Utils;

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
			holder.messageTextView = (TextView) convertView
					.findViewById(R.id.tvMessage);
			holder.idTextView = (TextView) convertView.findViewById(R.id.tvId);
			holder.createdAtTextView = (TextView) convertView
					.findViewById(R.id.tvCreatedAt);
			holder.nameTextView = (TextView) convertView
					.findViewById(R.id.tvName);
			holder.unReadedNotitficationTextView = (TextView) convertView
					.findViewById(R.id.tvUnreadNotification);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Case caseItem = mCaseList.get(position);
		if (caseItem.getTotal_new_case_comments_by_doctor() > 0) {
			holder.unReadedNotitficationTextView.setText(caseItem
					.getTotal_new_case_comments_by_doctor() + "");
			holder.unReadedNotitficationTextView.setVisibility(View.VISIBLE);
		} else {
			holder.unReadedNotitficationTextView.setVisibility(View.GONE);
		}
		Comment comment = caseItem.getFirst_case_comment();
		if (comment != null)
			holder.messageTextView.setText(comment.getMessage());
		else
			holder.messageTextView.setText("");
		holder.idTextView.setText("# " + caseItem.getId());
		holder.createdAtTextView.setText(Utils.getDateInFormat(caseItem
				.getUpdated_at()));
		holder.nameTextView.setText(caseItem.getName());
		return convertView;
	}

	private class ViewHolder {
		private TextView idTextView, unReadedNotitficationTextView,
				messageTextView, nameTextView, createdAtTextView;
	}

}
