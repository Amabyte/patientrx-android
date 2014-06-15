package com.matrix.patientrx.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.matrix.patientrx.R;
import com.matrix.patientrx.constants.Constants;
import com.matrix.patientrx.models.Case;
import com.matrix.patientrx.models.Comment;
import com.matrix.patientrx.utils.Utils;

public class ViewCaseDetailActivity extends Activity {
	private TextView textCreatedAt;
	private TextView textName;
	private TextView textGender;
	private TextView textAge;
	private TextView textMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_case_detail);
		initializeViews();
		Case caseItem = getIntent().getParcelableExtra(Constants.EXTRA_CASE);
		textCreatedAt.setText(Utils.getDateInFormat(caseItem.getCreated_at()));
		textName.setText(caseItem.getName());
		textGender.setText(caseItem.getGender());
		textAge.setText(caseItem.getAge() + "");
		Comment comment = caseItem.getFirst_case_comment();
		if (comment != null)
			textMessage.setText(comment.getMessage());
		else
			textMessage.setText("");
	}

	private void initializeViews() {
		textCreatedAt = (TextView) findViewById(R.id.textCreatedAt);
		textName = (TextView) findViewById(R.id.textName);
		textGender = (TextView) findViewById(R.id.textGender);
		textAge = (TextView) findViewById(R.id.textAge);
		textMessage = (TextView) findViewById(R.id.textMessage);
	}
}
