package com.matrix.patientrx.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.matrix.patientrx.R;
import com.matrix.patientrx.adapter.CaseListAdapter;
import com.matrix.patientrx.constants.Constants;
import com.matrix.patientrx.models.Case;
import com.matrix.patientrx.utils.DialogManager;
import com.matrix.patientrx.utils.Preference;
import com.matrix.patientrx.utils.Utils;

public class HomeScreenActivity extends Activity implements OnClickListener {
	private static final int REQUEST_CREATE_NEW_CASE = 0;
	private static final int TIME_BETWEEN_HOME_SCREEN_REFRESH = 30000;// 30 sec
	private ListView listViewCase;
	private DialogManager mDialogManager;
	private Date lastRefreshTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		initialiseViews();
		mDialogManager = new DialogManager();
		lastRefreshTime = new Date();
		mDialogManager
				.showProgressDialog(HomeScreenActivity.this, "Loading...");
		Utils.getAllCasess(mGetAllCasesResponseHanlder);
	}

	private void initialiseViews() {
		findViewById(R.id.buttonCreateNewCase).setOnClickListener(this);
		findViewById(R.id.buttonLogout).setOnClickListener(this);
		listViewCase = (ListView) findViewById(R.id.listViewCase);
		listViewCase.setOnItemClickListener(listCaseItemClickListener);

	}

	OnItemClickListener listCaseItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int pos,
				long id) {
			Case caseItem = (Case) adapter.getItemAtPosition(pos);
			Intent intent = new Intent(HomeScreenActivity.this,
					ViewCaseDetailActivity.class);
			intent.putExtra(Constants.EXTRA_CASE, caseItem);
			startActivity(intent);
			Toast.makeText(HomeScreenActivity.this,
					"List item:" + caseItem.getName(), Toast.LENGTH_SHORT)
					.show();
		}
	};

	@Override
	protected void onResume() {
		long curTime = new Date().getTime();
		if ((curTime - lastRefreshTime.getTime()) > TIME_BETWEEN_HOME_SCREEN_REFRESH) {
			mDialogManager.showProgressDialog(HomeScreenActivity.this,
					"Loading...");
			Utils.getAllCasess(mGetAllCasesResponseHanlder);
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
		case R.id.buttonLogout:
			logout();
			break;
		}
	}

	private void logout() {
		Utils.logout(mLogoutResponseHandler);
	}

	private JsonHttpResponseHandler mLogoutResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				org.json.JSONObject response) {
			String res = "Status code: " + statusCode + " response:"
					+ new Gson().toJson(response).toString();

			Log.d("Success", res);
			// Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG)
			// .show();
			Preference.remove(Constants.SESSION_ID);
			finish();
			startActivity(new Intent(HomeScreenActivity.this,
					GoogleLoginActivity.class));
		}

		@Override
		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				java.lang.Throwable e, org.json.JSONObject errorResponse) {
			// Response failed :(
			String err = "onFailure status code:" + statusCode
					+ " response body:" + errorResponse + " error:" + e
					+ " headers:" + headers;
			Log.d("err", err);

			Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG)
					.show();

		}

	};

	private JsonHttpResponseHandler mGetAllCasesResponseHanlder = new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				org.json.JSONArray response) {
			// String res = "Status code: " + statusCode + " response:"
			// + new Gson().toJson(response).toString();
			// Log.d("Success", res);
			// Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG)
			// .show();
			ArrayList<Case> caseList = new ArrayList<Case>();
			Gson gson = new Gson();
			Type type = new TypeToken<List<Case>>() {
			}.getType();
			caseList = gson.fromJson(response.toString(), type);
			// Toast.makeText(getApplicationContext(),
			// "Name:" + caseList.get(0).getName(), Toast.LENGTH_LONG)
			// .show();
			CaseListAdapter caseListAdapter = new CaseListAdapter(
					HomeScreenActivity.this, caseList);
			listViewCase.setAdapter(caseListAdapter);
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
			Toast.makeText(HomeScreenActivity.this, err, Toast.LENGTH_LONG)
					.show();
		}

	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case REQUEST_CREATE_NEW_CASE:
			mDialogManager.showProgressDialog(HomeScreenActivity.this,
					"Loading...");
			Utils.getAllCasess(mGetAllCasesResponseHanlder);
			break;
		}
	};
}
