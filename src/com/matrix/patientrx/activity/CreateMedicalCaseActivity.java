package com.matrix.patientrx.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.matrix.patientrx.R;

public class CreateMedicalCaseActivity extends Activity implements
		OnClickListener {
	private ImageView mImageView;
	private Boolean mImageSelected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_medical_case);
		intializeViews();
	}

	private void intializeViews() {
		mImageView = (ImageView) findViewById(R.id.img);
		mImageView.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img:
			if (mImageSelected) {
				// zoom ImageView
			} else {
				showPictureSelectionOptions();
			}
			break;
		}

	}

	private void showPictureSelectionOptions() {
		String[] items = new String[] { "Take Picture", "Select From gallery" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, items);
		new AlertDialog.Builder(this).setTitle("Image Selector")
				.setAdapter(adapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							Toast.makeText(getApplicationContext(),
									"Take picture", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(),
									"Select From gallery", Toast.LENGTH_SHORT)
									.show();
						}
						dialog.dismiss();
					}
				}).create().show();
	}
}
