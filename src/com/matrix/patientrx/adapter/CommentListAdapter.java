package com.matrix.patientrx.adapter;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.matrix.patientrx.R;
import com.matrix.patientrx.activity.ZoomInZoomOut;
import com.matrix.patientrx.constants.Constants;
import com.matrix.patientrx.models.Comment;
import com.matrix.patientrx.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommentListAdapter extends BaseAdapter {
	private ArrayList<Comment> mCommentList;
	private Context mContext;
	protected ImageLoader mImageLoader;
	private boolean mStartPlaying = true;
	private MediaPlayer mPlayer = null;
	private final String LOG_TAG = "CommentListAdapter";
	private DisplayImageOptions mOptions;

	public CommentListAdapter(Context context, ArrayList<Comment> commentList,
			DisplayImageOptions options) {
		mContext = context;
		mCommentList = commentList;
		mImageLoader = ImageLoader.getInstance();
		mOptions = options;
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
	public boolean isEnabled(int position) {
		return false;
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
			holder.textMessage = (TextView) convertView
					.findViewById(R.id.textMessage);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, ZoomInZoomOut.class);
					intent.putExtra(Constants.EXTRA_IMAGE_URL,
							(String) v.getTag());
					mContext.startActivity(intent);
				}
			});

			holder.imgAudio = (ImageView) convertView
					.findViewById(R.id.imgPlayAudio);
			holder.imgAudio.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startStopPlaying((ImageView) v);
				}
			});
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (TextUtils.isEmpty(comment.getMessage()))
			holder.textMessage.setVisibility(View.GONE);
		else
			holder.textMessage.setText(comment.getMessage());
		if ((comment.getImage_url() != null)
				&& (comment.getImage_url().contains("images"))) {
			holder.img.setVisibility(View.VISIBLE);
			holder.img.setTag(comment.getImage_url());
			mImageLoader.displayImage(comment.getImage_url(), holder.img,
					mOptions);
		} else {
			holder.img.setVisibility(View.GONE);
		}
		if ((comment.getAudio_url() != null)
				&& (comment.getAudio_url().contains("audio"))) {
			holder.imgAudio.setVisibility(View.VISIBLE);
			holder.imgAudio.setTag(comment.getAudio_url());
		} else {
			holder.imgAudio.setVisibility(View.GONE);
		}
		holder.textCreatedAt.setText(Utils.getDateInFormat(comment
				.getCreated_at()));
		return convertView;
	}

	private void startStopPlaying(ImageView img) {
		// play recorded file
		onPlay(img, mStartPlaying);
		if (mStartPlaying) {
			img.setImageResource(R.drawable.ic_action_right_stop);
		} else {
			img.setImageResource(R.drawable.ic_action_right_play);
		}
		mStartPlaying = !mStartPlaying;
	}

	private void onPlay(ImageView img, boolean start) {
		if (start) {
			startPlaying(img);
		} else {
			stopPlaying(img);
		}
	}

	private void startPlaying(final ImageView img) {
		String url = (String) img.getTag(); // your URL here
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setDataSource(url);
			mPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					mStartPlaying = true;
				}
			});
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	private void stopPlaying(ImageView img) {
		mPlayer.release();
		mPlayer = null;
	}

	private class ViewHolder {
		private TextView textCreatedAt;
		private TextView textMessage;
		private ImageView img;
		private ImageView imgAudio;
	}

}
