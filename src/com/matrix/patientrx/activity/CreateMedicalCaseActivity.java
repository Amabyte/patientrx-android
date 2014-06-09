package com.matrix.patientrx.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.matrix.patientrx.R;
import com.matrix.patientrx.constants.Constants;
import com.matrix.patientrx.utils.DialogManager;
import com.matrix.patientrx.utils.Utils;

public class CreateMedicalCaseActivity extends Activity implements
		OnClickListener {
	private static final int REQUEST_SELECT_IMAGE_FROM_GALLERY = 0;
	private static final int REQUEST_TAKE_PHOTO = 1;
	private static final String LOG_TAG = "CreateMedicalCaseActivity";
	private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";

	private ImageView mImageView;
	private TextView mTextAudioStatus;
	private Button mEditAudio;
	private Button mEditImageView;
	private Button mButtonAudio;
	private Spinner mSpinnerGender;

	private boolean mStartRecording = true;
	private boolean mImageSelected = false;
	private boolean mStartPlaying = true;
	private String mImagePath;
	private String mAudioFileName = null;
	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;

	private DialogManager mDialogManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_medical_case);
		mDialogManager = new DialogManager();
		mAudioFileName = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		mAudioFileName += "/audiorecordtest.3gp";
		intializeViews();
	}

	private void intializeViews() {
		mImageView = (ImageView) findViewById(R.id.img);
		mEditImageView = (Button) findViewById(R.id.buttonEditImage);
		mTextAudioStatus = (TextView) findViewById(R.id.textRecordStatus);
		mEditAudio = (Button) findViewById(R.id.buttonEditAudio);
		mButtonAudio = (Button) findViewById(R.id.buttonRecordAudio);
		mSpinnerGender = (Spinner) findViewById(R.id.spinnerGender);
		String[] gender = { "Male", "Female", "Third" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, gender);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerGender.setAdapter(adapter);
		mImageView.setOnClickListener(this);
		mEditImageView.setOnClickListener(this);
		mEditAudio.setOnClickListener(this);
		mButtonAudio.setOnClickListener(this);
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
				// play recorded file
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
			mDialogManager.showAlertDialog(CreateMedicalCaseActivity.this,
					"Alert", "Press OK to erase erase the audio", "OK",
					"Cancel", new DialogInterface.OnClickListener() {
						@SuppressWarnings("deprecation")
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							mAudioRecordCompleted = false;
							mTextAudioStatus.setText("Start Recording");
							mButtonAudio.setBackgroundDrawable(getResources()
									.getDrawable(R.drawable.record));
							mEditAudio.setVisibility(View.INVISIBLE);
						}
					}, null);
			break;
		}

	}

	private void recordPauseAudio() {
		if (mStartRecording) {
			mTextAudioStatus.setText("Stop recording");
		} else {
			mTextAudioStatus.setText("Start recording");
		}
		onRecord(mStartRecording);
		mStartRecording = !mStartRecording;
	}

	private void onPlay(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}

	@SuppressWarnings("deprecation")
	private void startPlaying() {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(mAudioFileName);
			mPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					mButtonAudio.setBackgroundDrawable(getResources()
							.getDrawable(R.drawable.play));
					mTextAudioStatus.setText("Start playing");
					mEditAudio.setEnabled(true);
				}
			});
			mPlayer.prepare();
			mPlayer.start();
			mButtonAudio.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.stop));
			mEditAudio.setEnabled(false);
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}
	}

	@SuppressWarnings("deprecation")
	private void stopPlaying() {
		mPlayer.release();
		mButtonAudio.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.play));
		mEditAudio.setEnabled(true);
		mPlayer = null;
	}

	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	@SuppressWarnings("deprecation")
	private void startRecording() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mAudioFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() failed");
		}

		mRecorder.start();
		mButtonAudio.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.stop));
		mEditAudio.setEnabled(false);
	}

	private Boolean mAudioRecordCompleted = false;

	@SuppressWarnings("deprecation")
	private void stopRecording() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		// TODO enable play the recorded audio
		mAudioRecordCompleted = true;
		mEditAudio.setVisibility(View.VISIBLE);
		mButtonAudio.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.play));
		mTextAudioStatus.setText("Start playing");
		mEditAudio.setEnabled(true);
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
							dispatchTakePictureIntent();
						} else {
							selectImageFromGallery();
						}
						dialog.dismiss();
					}

				}).create().show();
	}

	/** * Start the camera by dispatching a camera intent. */
	protected void dispatchTakePictureIntent() {
		// Check if there is a camera.

		PackageManager packageManager = getPackageManager();
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
			Toast.makeText(getApplicationContext(),
					"This device does not have a camera.", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		// Camera exists? Then proceed...
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent

		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go.
			// If you don't do this, you may get a crash in some devices.
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
				Toast toast = Toast.makeText(getApplicationContext(),
						"There was a problem saving the photo...",
						Toast.LENGTH_SHORT);
				toast.show();
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				Uri fileUri = Uri.fromFile(photoFile);
				mImagePath = fileUri.getPath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		// mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		mImagePath = image.getAbsolutePath();
		return image;
	}

	private void selectImageFromGallery() {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, REQUEST_SELECT_IMAGE_FROM_GALLERY);
	}

	/**
	 * * Use for decoding camera response data. * * @param data * @param context
	 * * @return
	 */
	public void setImageFromCameraData(Intent data, Context context) {
		Uri selectedImage = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		mImagePath = cursor.getString(columnIndex);
		cursor.close();
		Utils.setPic(mImagePath, mImageView);
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
	public void onSaveInstanceState(Bundle savedInstanceState) {
		if (mImagePath != null) {
			savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, mImagePath);
		}
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
			mImagePath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK)
			return;
		switch (requestCode) {
		case REQUEST_SELECT_IMAGE_FROM_GALLERY:
			setImageFromCameraData(data, CreateMedicalCaseActivity.this);
			mImageSelected = true;
			mEditImageView.setVisibility(View.VISIBLE);
			break;
		case REQUEST_TAKE_PHOTO:
			addPicToGallery(mImagePath);
			// Show the full sized image.
			Utils.setPic(mImagePath, mImageView);
			mImageSelected = true;
			mEditImageView.setVisibility(View.VISIBLE);
			break;
		}

	}

	private void addPicToGallery(String imagePath) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(imagePath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		this.sendBroadcast(mediaScanIntent);
	}

}