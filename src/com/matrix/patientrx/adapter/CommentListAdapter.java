package com.matrix.patientrx.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.matrix.patientrx.R;
import com.matrix.patientrx.models.Comment;
import com.matrix.patientrx.utils.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommentListAdapter extends BaseAdapter {
	private ArrayList<Comment> mCommentList;
	private Context mContext;
	protected ImageLoader mImageLoader;

	public CommentListAdapter(Context context, ArrayList<Comment> commentList) {
		mContext = context;
		mCommentList = commentList;
		mImageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return mCommentList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mCommentList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Comment comment = mCommentList.get(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_comment_item, null);
			holder = new ViewHolder();
			holder.textCreatedAt = (TextView) convertView
					.findViewById(R.id.textCreatedAt);
			holder.patientCommentView = (RelativeLayout) convertView
					.findViewById(R.id.patientComment);
			holder.textMessage = (TextView) convertView
					.findViewById(R.id.textMessage);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.imgAudio = (ImageView) convertView
					.findViewById(R.id.imgPlayAudio);
			holder.docCommentView = (RelativeLayout) convertView
					.findViewById(R.id.docComment);
			holder.textDocName = (TextView) convertView
					.findViewById(R.id.textDocName);
			holder.textDocQualification = (TextView) convertView
					.findViewById(R.id.textDocQualification);
			holder.imgDocPlayAudio = (ImageView) convertView
					.findViewById(R.id.imgDocPlayAudio);
			holder.textLabTests = (TextView) convertView
					.findViewById(R.id.textLabTests);
			holder.textDocSuggestions = (TextView) convertView
					.findViewById(R.id.textDocSuggestions);
			holder.textSubRevCase = (TextView) convertView
					.findViewById(R.id.textSubRevCase);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (comment.getWho().equalsIgnoreCase("patient")) {
			holder.patientCommentView.setVisibility(View.VISIBLE);
			holder.docCommentView.setVisibility(View.GONE);
			holder.textMessage.setText(comment.getMessage());
			if ((comment.getImage_url() != null)
					&& (!comment.getImage_url().equals("")))
				mImageLoader.displayImage(comment.getImage_url(), holder.img);
			

			// patient comment
		} else {
			// doc comment
			holder.patientCommentView.setVisibility(View.GONE);
			holder.docCommentView.setVisibility(View.VISIBLE);

		}
		holder.textCreatedAt.setText(Utils.getDateInFormat(comment
				.getCreated_at()));
		return convertView;
	}

	private class ViewHolder {
		private TextView textCreatedAt;
		private RelativeLayout patientCommentView;
		private TextView textMessage;
		private ImageView img;
		private ImageView imgAudio;
		private RelativeLayout docCommentView;
		private TextView textDocName;
		private TextView textDocQualification;
		private ImageView imgDocPlayAudio;
		private TextView textLabTests;
		private TextView textDocSuggestions;
		private TextView textSubRevCase;
	}

}
