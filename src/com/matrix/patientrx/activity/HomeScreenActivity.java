package com.matrix.patientrx.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.matrix.patientrx.R;
import com.matrix.patientrx.adapter.CaseListAdapter;
import com.matrix.patientrx.models.Case;
import com.matrix.patientrx.models.CaseListResponse;
import com.matrix.patientrx.utils.Utils;

public class HomeScreenActivity extends Activity implements OnClickListener {

	private ListView listViewCase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_screen);
		initialiseViews();
		Utils.getAllCasess(mGetAllCasesResponseHanlder);
	}

	private void initialiseViews() {
		findViewById(R.id.buttonCreateNewCase).setOnClickListener(this);
		listViewCase = (ListView) findViewById(R.id.listViewCase);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonCreateNewCase:
			startActivity(new Intent(HomeScreenActivity.this,
					CreateMedicalCaseActivity.class));
			break;
		}
	}

	private JsonHttpResponseHandler mGetAllCasesResponseHanlder = new JsonHttpResponseHandler() {
		@Override
		public void onSuccess(int statusCode, Header[] headers,
				org.json.JSONArray response) {
			String res = "Status code: " + statusCode + " response:"
					+ new Gson().toJson(response).toString();

			// Log.d("Success", res);
			// Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG)
			// .show();

			ArrayList<Case> caseList = new ArrayList<Case>();
			Gson gson = new Gson();
			Type type = new TypeToken<List<Case>>() {
			}.getType();
			caseList = gson.fromJson(response.toString(), type);
			Toast.makeText(getApplicationContext(),
					"Name:" + caseList.get(0).getName(), Toast.LENGTH_LONG)
					.show();
			CaseListAdapter caseListAdapter = new CaseListAdapter(
					HomeScreenActivity.this, caseList);
			listViewCase.setAdapter(caseListAdapter);
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
