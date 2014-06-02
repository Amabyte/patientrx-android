package com.matrix.patientrx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonCreateMedicalCase:
			startActivity(new Intent(HomeActivity.this,
					CreateMedicalCaseActivity.class));
			break;
		}

	}
}
