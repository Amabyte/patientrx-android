package com.matrix.patientrx.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.matrix.patientrx.R;
import com.matrix.patientrx.constants.Constants;
import com.matrix.patientrx.models.Case;

public class ViewCaseDetailActivity extends Activity {
	private TextView textCaseId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_case_detail);
		initializeViews();
		Case caseItem = getIntent().getParcelableExtra(Constants.EXTRA_CASE);
		textCaseId.setText(caseItem.getId() + "");
	}

	private void initializeViews() {
		textCaseId = (TextView) findViewById(R.id.textCaseId);
	}
}
