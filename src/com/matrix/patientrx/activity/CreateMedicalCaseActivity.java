package com.matrix.patientrx.activity;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.matrix.patientrx.R;
import com.matrix.patientrx.activity.AudioRecordTest.PlayButton;
import com.matrix.patientrx.activity.AudioRecordTest.RecordButton;
import com.matrix.patientrx.constants.Constants;
import com.matrix.patientrx.utils.DialogManager;

public class CreateMedicalCaseActivity extends Activity implements
		OnClickListener {
	private static final int IMAGE_GALLERY_PICKER_SELECT = 0;
	private ImageView mImageView;
	private Button mEditImageView;
	private Button mButtonAudio;
	private TextView mTextAudioStatus;
	private Button mEditAudio;
	private Boolean mImageSelected = false;
	private String mImagePath;
	boolean mStartRecording = true;
	private static final String LOG_TAG = "CreateMedicalCaseActivity";
	private static String mFileName = null;

	private RecordButton mRecordButton = null;
	private MediaRecorder mRecorder = null;

	private PlayButton mPlayButton = null;
	private MediaPlayer mPlayer = null;
	boolean mStartPlaying = true;

	private DialogManager mDialogManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_medical_case);
		mDialogManager = new DialogManager();
		mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
		mFileName += "/audiorecordtest.3gp";
		intializeViews();
	}

	private void intializeViews() {
		mImageView = (ImageView) findViewById(R.id.img);
		mEditImageView = (Button) findViewById(R.id.buttonEditImage);
		mTextAudioStatus = (TextView) findViewById(R.id.textRecordStatus);
		mEditAudio = (Button) findViewById(R.id.buttonEditAudio);
		mButtonAudio = (Button) findViewById(R.id.buttonRecordAudio);
		mImageView.setOnClickListener(this);
		mEditImageView.setOnClickListener(this);
		mEditAudio.setOnClickListener(this);
		mButtonAudio.setOnClickListener(this);
	}

	private void recordPauseAudio() {
		onRecord(mStartRecording);
		if (mStartRecording) {
			mTextAudioStatus.setText("Stop recording");
		} else {
			mTextAudioStatus.setText("Start recording");
		}
		mStartRecording = !mStartRecording;
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
		case R.id.buttonRecordAudio:
			if (!mAudioRecordCompleted) {
				recordPauseAudio();
			} else {
				// play recored file
				onPlay(mStartPlaying);
				if (mStartPlaying) {
					mTextAudioStatus.setText("Stop playing");
				} else {
					mTextAudioStatus.setText("Start playing");
				}
				mStartPlaying = !mStartPlaying;
			}
			break;
		case R.id.buttonEditAudio:
			// show a alert dialog
			mDialogManager.showAlertDialog(CreateMedicalCaseActivity.this,
					"Alert", "Press OK to erase erase the audio", "OK",
					"Cancel", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							mAudioRecordCompleted = false;
						}

					}, null);
			break;

		}

	}

	private void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}

	private void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mFileName);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	private void stopPlaying() {
		mPlayer.release();
		mPlayer = null;
	}

	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

		mRecorder.start();
	}

	private Boolean mAudioRecordCompleted = false;

	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		// TODO enable play the recorded audio
		mAudioRecordCompleted = true;
		mEditAudio.setVisibility(View.VISIBLE);
		mButtonAudio.setBackgroundDrawable(getResources().getDrawable(R.drawable.play));
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
	public void onPause() {
		super.onPause();
		if (mRecorder != null) {
			mRecorder.release();
			mRecorder = null;
		}

		if (mPlayer != null) {
			mPlayer.release();
			mPlayer = null;
		}
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