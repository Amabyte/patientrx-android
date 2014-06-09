package com.matrix.patientrx.activity;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.matrix.patientrx.R;
import com.matrix.patientrx.constants.Constants;
import com.matrix.patientrx.utils.Preference;
import com.matrix.patientrx.utils.Utils;

public class HomeActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		initializeViews();
	}

	private void initializeViews() {
		findViewById(R.id.buttonCreateMedicalCase).setOnClickListener(this);
		findViewById(R.id.buttonPreviouslySubmittedCases).setOnClickListener(
				this);
		findViewById(R.id.buttonReplyFromDoctor).setOnClickListener(this);
		findViewById(R.id.buttonLogout).setOnClickListener(this);
		findViewById(R.id.buttonGetUserProfile).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonCreateMedicalCase:
			startActivity(new Intent(HomeActivity.this,
					CreateMedicalCaseActivity.class));
			break;
		case R.id.buttonLogout:
			logout();
			break;
		case R.id.buttonGetUserProfile:
			getProfile();
			break;
		}

	}

	private void getProfile() {
		Utils.getProfile(mProfileResponseHanlder);
	}

	private void logout() {
//		Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_LONG)
//				.show();
		Utils.logout(mLogoutResponseHandler);
	}

	private JsonHttpResponseHandler mProfileResponseHanlder = new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				org.json.JSONObject response) {
			String res = "Status code: " + statusCode + " response:"
					+ new Gson().toJson(response).toString();

			Log.d("Success", res);
			Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG)
					.show();
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
	private JsonHttpResponseHandler mLogoutResponseHandler = new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				org.json.JSONObject response) {
			String res = "Status code: " + statusCode + " response:"
					+ new Gson().toJson(response).toString();

			Log.d("Success", res);
//			Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG)
//					.show();
			Preference.remove(Constants.SESSION_ID);	
			finish();
			startActivity(new Intent(HomeActivity.this,
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
}
