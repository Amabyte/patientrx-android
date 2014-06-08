package com.matrix.patientrx.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.matrix.patientrx.R;
import com.matrix.patientrx.constants.Constants;

public class CreateMedicalCaseActivity extends Activity implements
		OnClickListener {
	private static final int IMAGE_GALLERY_PICKER_SELECT = 0;
	private ImageView mImageView;
	private Button mEditImageView;
	private Boolean mImageSelected = false;
	private String mImagePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_medical_case);
		intializeViews();
	}

	private void intializeViews() {
		mImageView = (ImageView) findViewById(R.id.img);
		mEditImageView = (Button) findViewById(R.id.buttonEditImage);
		mImageView.setOnClickListener(this);
		mEditImageView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img:
			if (mImageSelected) {
				// zoom ImageView
				Intent intent = new Intent(CreateMedicalCaseActivity.this,
						ZoomInZoomOut.class);
				intent.putExtra(Constants.EXTRA_IMAGE_PATH, mImagePath);
				startActivity(intent);
			} else {
				showPictureSelectionOptions();
			}
			break;
		case R.id.buttonEditImage:
			showPictureSelectionOptions();
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
							selectImageFromGallery();
						}
						dialog.dismiss();
					}

				}).create().show();
	}

	private void selectImageFromGallery() {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, IMAGE_GALLERY_PICKER_SELECT);
	}

	/**
	 * * Use for decoding camera response data. * * @param data * @param context
	 * * @return
	 */
	public Bitmap getBitmapFromCameraData(Intent data, Context context) {
		Uri selectedImage = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		mImagePath = cursor.getString(columnIndex);
		cursor.close();
		return BitmapFactory.decodeFile(mImagePath);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == IMAGE_GALLERY_PICKER_SELECT
				&& resultCode == Activity.RESULT_OK) {
			Bitmap bitmap = getBitmapFromCameraData(data,
					CreateMedicalCaseActivity.this);
			mImageView.setImageBitmap(bitmap);
			mImageSelected = true;
			mEditImageView.setVisibility(View.VISIBLE);
		}
	}
}