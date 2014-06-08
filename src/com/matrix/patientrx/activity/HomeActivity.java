package com.matrix.patientrx.activity;

import com.matrix.patientrx.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

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
			Toast.makeText(getApplicationContext(), "Get Profile", Toast.LENGTH_LONG)
			.show();
			break;
		}

	}

	private void logout() {
		Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_LONG)
				.show();
	}
}
