package com.matrix.patientrx.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.matrix.patientrx.R;
import com.matrix.patientrx.adapter.CommentListAdapter;
import com.matrix.patientrx.constants.Constants;
import com.matrix.patientrx.http.ServerUtils;
import com.matrix.patientrx.models.Case;
import com.matrix.patientrx.models.Comment;
import com.matrix.patientrx.utils.DialogManager;
import com.matrix.patientrx.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ViewCaseDetailActivity extends Activity implements
		OnClickListener, OnScrollListener {
	private final int REQUEST_CREATE_COMMENT = 0;
	private TextView mTextCreatedAt;
	private TextView mTextName;
	private TextView mTextGender;
	private TextView mTextAge;
	private TextView mTextMessage;
	private ListView mListComments;
	private View addButton;
	private DialogManager mDialogManager;
	private int mCaseId;
	DisplayImageOptions options;

	private Animation showAnimation, hideAnimation;
	private int lastFisrtItem = 0;
	private boolean isAddButtonShowing = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_case_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		initializeViews();
		initAnimation();
		mDialogManager = new DialogManager();
		Case caseItem = getIntent().getParcelableExtra(Constants.EXTRA_CASE);
		mTextCreatedAt.setText(Utils.getDateInFormat(caseItem.getCreated_at()));
		mTextName.setText(caseItem.getName());
		mTextGender.setText("Sex : " + caseItem.getGender());
		mTextAge.setText("Age : " + caseItem.getAge());
		Comment comment = caseItem.getFirst_case_comment();
		if (comment != null)
			mTextMessage.setText(comment.getMessage());
		else
			mTextMessage.setText("");
		mCaseId = caseItem.getId();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.camera)
				.showImageForEmptyUri(R.drawable.camera)
				.showImageOnFail(R.drawable.camera).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();

		mDialogManager.showProgressDialog(ViewCaseDetailActivity.this,
				"Loading...");
		ServerUtils.getAllComments(mCaseId, mGetAllCommentsResponseHanlder);
	}

	private void initAnimation() {
		hideAnimation = new TranslateAnimation(0, 0, 0,
				Utils.getDisplayHeight(this) + addButton.getHeight());
		hideAnimation.setDuration(300);
		hideAnimation.setFillAfter(true);

		hideAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				addButton.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		showAnimation = new TranslateAnimation(0, 0,
				Utils.getDisplayHeight(this) + addButton.getHeight(), 0);
		showAnimation.setDuration(300);
		showAnimation.setFillAfter(true);

		showAnimation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				addButton.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
	}

	private void initializeViews() {
		mTextCreatedAt = (TextView) findViewById(R.id.textCreatedAt);
		mTextName = (TextView) findViewById(R.id.textName);
		mTextGender = (TextView) findViewById(R.id.textGender);
		mTextAge = (TextView) findViewById(R.id.textAge);
		mTextMessage = (TextView) findViewById(R.id.textMessage);
		mListComments = (ListView) findViewById(R.id.listComments);
		mListComments.setOnScrollListener(this);
		addButton = findViewById(R.id.buttonCreate);
		addButton.setOnClickListener(this);
	}

	private JsonHttpResponseHandler mGetAllCommentsResponseHanlder = new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				org.json.JSONArray response) {
			// String res = "Status code: " + statusCode + " response:"
			// + new Gson().toJson(response).toString();
			// Log.d("Success", res);
			// Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG)
			// .show();
			ArrayList<Comment> commentList = new ArrayList<Comment>();
			Gson gson = new Gson();
			Type type = new TypeToken<List<Comment>>() {
			}.getType();
			commentList = gson.fromJson(response.toString(), type);
			// Toast.makeText(getApplicationContext(),
			// "Name:" + caseList.get(0).getName(), Toast.LENGTH_LONG)
			// .show();
			CommentListAdapter caseListAdapter = new CommentListAdapter(
					ViewCaseDetailActivity.this, commentList, options);
			mListComments.setAdapter(caseListAdapter);
			mDialogManager.removeProgressDialog();
		}

		@Override
		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				java.lang.Throwable e, org.json.JSONObject errorResponse) {
			// Response failed :(
			String err = "onFailure status code:" + statusCode
					+ " response body:" + errorResponse + " error:" + e
					+ " headers:" + headers;
			Log.d("err", err);
			mDialogManager.removeProgressDialog();
			Toast.makeText(ViewCaseDetailActivity.this, err, Toast.LENGTH_LONG)
					.show();
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonCreate:
			Intent intent = new Intent(ViewCaseDetailActivity.this,
					CreateCommentActivity.class);
			intent.putExtra(Constants.EXTRA_CASE_ID, mCaseId);
			startActivityForResult(intent, REQUEST_CREATE_COMMENT);
			break;
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case REQUEST_CREATE_COMMENT:
			mDialogManager.showProgressDialog(ViewCaseDetailActivity.this,
					"Loading...");
			ServerUtils.getAllComments(mCaseId, mGetAllCommentsResponseHanlder);
			break;
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (lastFisrtItem < firstVisibleItem) {
			hideAddButton();
		}
		if (lastFisrtItem > firstVisibleItem) {
			showAddButton();
		}
		lastFisrtItem = firstVisibleItem;
	}

	private void showAddButton() {
		if (!isAddButtonShowing) {
			isAddButtonShowing = true;
			addButton.startAnimation(showAnimation);
		}
	}

	private void hideAddButton() {
		if (isAddButtonShowing) {
			isAddButtonShowing = false;
			addButton.startAnimation(hideAnimation);
		}
	}

}
