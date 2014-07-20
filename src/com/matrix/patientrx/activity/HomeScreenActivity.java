package com.matrix.patientrx.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.matrix.patientrx.R;
import com.matrix.patientrx.adapter.CaseListAdapter;
import com.matrix.patientrx.constants.Constants;
import com.matrix.patientrx.http.ProgressJsonHttpResponseHandler;
import com.matrix.patientrx.http.ServerUtils;
import com.matrix.patientrx.models.Case;
import com.matrix.patientrx.utils.Preference;
import com.matrix.patientrx.utils.Utils;

public class HomeScreenActivity extends ListActivity implements
		OnClickListener, OnScrollListener, OnItemClickListener {
	private static final int REQUEST_CREATE_NEW_CASE = 0;
	private static final int TIME_BETWEEN_HOME_SCREEN_REFRESH = 30000;// 30 sec
	private Date lastRefreshTime;
	private int lastFisrtItem = 0;
	private boolean isAddButtonShowing = true;
	private Animation showAnimation, hideAnimation;
	private View addButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		initialiseViews();
		initAnimation();
		lastRefreshTime = new Date();
		getAllCases();
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

	private void initialiseViews() {
		addButton = findViewById(R.id.buttonCreateNewCase);
		addButton.setOnClickListener(this);
		getListView().setOnItemClickListener(this);
		getListView().setOnScrollListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int pos, long id) {
		Case caseItem = (Case) adapter.getItemAtPosition(pos);
		Intent intent = new Intent(HomeScreenActivity.this,
				ViewCaseDetailActivity.class);
		intent.putExtra(Constants.EXTRA_CASE, caseItem);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		long curTime = new Date().getTime();
		if ((curTime - lastRefreshTime.getTime()) > TIME_BETWEEN_HOME_SCREEN_REFRESH) {
			getAllCases();
			lastRefreshTime.setTime(curTime);
		}
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonCreateNewCase:
			startActivityForResult((new Intent(HomeScreenActivity.this,
					CreateMedicalCaseActivity.class)), REQUEST_CREATE_NEW_CASE);
			break;
		}
	}

	private void logout() {
		ServerUtils.logout(new ProgressJsonHttpResponseHandler(this) {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					org.json.JSONObject response) {
				String res = "Status code: " + statusCode + " response:"
						+ new Gson().toJson(response).toString();
				Log.d("Success", res);
				Preference.remove(Constants.SESSION_ID);
				finish();
				startActivity(new Intent(HomeScreenActivity.this,
						GoogleLoginActivity.class));
			}

			@Override
			public void onFailure(int statusCode,
					org.apache.http.Header[] headers, java.lang.Throwable e,
					org.json.JSONObject errorResponse) {
				// Response failed :(
				String err = "onFailure status code:" + statusCode
						+ " response body:" + errorResponse + " error:" + e
						+ " headers:" + headers;
				Log.d("err", err);

				Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG)
						.show();
			}

		});
	}

	private void getAllCases() {
		ServerUtils.getAllCasess(new ProgressJsonHttpResponseHandler(this) {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					org.json.JSONArray response) {
				ArrayList<Case> caseList = new ArrayList<Case>();
				Gson gson = new Gson();
				Type type = new TypeToken<List<Case>>() {
				}.getType();
				caseList = gson.fromJson(response.toString(), type);
				CaseListAdapter caseListAdapter = new CaseListAdapter(
						HomeScreenActivity.this, caseList);
				setListAdapter(caseListAdapter);
			}

			@Override
			public void onFailure(int statusCode,
					org.apache.http.Header[] headers, java.lang.Throwable e,
					org.json.JSONObject errorResponse) {
				// Response failed :(
				String err = "onFailure status code:" + statusCode
						+ " response body:" + errorResponse + " error:" + e
						+ " headers:" + headers;
				Log.d("err", err);
				Toast.makeText(HomeScreenActivity.this, err, Toast.LENGTH_LONG)
						.show();
			}

		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case REQUEST_CREATE_NEW_CASE:
			getAllCases();
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_logout:
			logout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
