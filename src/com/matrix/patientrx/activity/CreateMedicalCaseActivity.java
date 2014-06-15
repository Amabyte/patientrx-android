package com.matrix.patientrx.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.Header;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.matrix.patientrx.R;
import com.matrix.patientrx.constants.Constants;
import com.matrix.patientrx.listeners.AudioUploadListener;
import com.matrix.patientrx.listeners.ImageUploadListener;
import com.matrix.patientrx.models.Case;
import com.matrix.patientrx.utils.DialogManager;
import com.matrix.patientrx.utils.Preference;
import com.matrix.patientrx.utils.Utils;

public class CreateMedicalCaseActivity extends Activity implements
		OnClickListener, ImageUploadListener, AudioUploadListener {
	private static final int REQUEST_SELECT_IMAGE_FROM_GALLERY = 0;
	private static final int REQUEST_SELECT_AUDIO_FROM_GALLERY = 1;
	private static final int REQUEST_TAKE_PHOTO = 2;
	private static final String LOG_TAG = "CreateMedicalCaseActivity";
	private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";

	private ImageView mImageView;
	private TextView mTextAudioStatus;
	private Button mEditAudio;
	private Button mEditImageView;
	private Button mButtonAudio;
	private Spinner mSpinnerGender;
	private EditText mEditAge;
	private EditText mEditName;
	private EditText mEditDetails;

	private boolean mStartRecording = true;
	private boolean mImageSelected = false;
	private boolean mStartPlaying = true;
	private String mImageFilePath;
	private String mAudioFilePath = null;
	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;

	private DialogManager mDialogManager;
	private String mImageFileName;
	private String mAudioFileName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_medical_case);
		mDialogManager = new DialogManager();
		intializeViews();
		mEditAge.setText("23");
		mEditName.setText(Preference.getString(Constants.USER_NAME));
	}

	private void intializeViews() {
		mImageView = (ImageView) findViewById(R.id.img);
		mEditImageView = (Button) findViewById(R.id.buttonEditImage);
		mTextAudioStatus = (TextView) findViewById(R.id.textRecordStatus);
		mEditAudio = (Button) findViewById(R.id.buttonEditAudio);
		mButtonAudio = (Button) findViewById(R.id.buttonRecordAudio);
		mEditAge = (EditText) findViewById(R.id.editAge);
		mEditName = (EditText) findViewById(R.id.editName);
		mEditDetails = (EditText) findViewById(R.id.editDetails);
		mSpinnerGender = (Spinner) findViewById(R.id.spinnerGender);
		String[] gender = { "Male", "Female" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, gender);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerGender.setAdapter(adapter);
		mImageView.setOnClickListener(this);
		mEditImageView.setOnClickListener(this);
		mEditAudio.setOnClickListener(this);
		mButtonAudio.setOnClickListener(this);
		findViewById(R.id.buttonSubmit).setOnClickListener(this);
	}

	private Boolean noAudioSelected = true;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img:
			// TODO stop playing audio when user clicks on image selection
			// if (mStartPlaying) {
			// mStartPlaying = false;
			// startStopPlaying();
			// }
			if (mImageSelected) {
				// zoom ImageView
				Intent intent = new Intent(CreateMedicalCaseActivity.this,
						ZoomInZoomOut.class);
				intent.putExtra(Constants.EXTRA_IMAGE_PATH, mImageFilePath);
				startActivity(intent);
			} else {
				showPictureSelectionOptions();
			}
			break;
		case R.id.buttonEditImage:
			showPictureSelectionOptions();
			break;
		case R.id.buttonRecordAudio:
			if (noAudioSelected)
				showAudioSelectionOptions();
			else {
				if (!mAudioRecordCompleted) {
					recordStopAudio();
				} else {
					startStopPlaying();

				}
			}
			break;
		case R.id.buttonEditAudio:
			showAudioSelectionOptions();
			// mDialogManager.showAlertDialog(CreateMedicalCaseActivity.this,
			// "Alert", "Press OK to erase erase the audio", "OK",
			// "Cancel", new DialogInterface.OnClickListener() {
			// @SuppressWarnings("deprecation")
			// @Override
			// public void onClick(DialogInterface arg0, int arg1) {
			// mAudioRecordCompleted = false;
			// mTextAudioStatus.setText("Start Recording");
			// mButtonAudio.setBackgroundDrawable(getResources()
			// .getDrawable(R.drawable.record));
			// mEditAudio.setVisibility(View.INVISIBLE);
			// }
			// }, null);
			break;
		case R.id.buttonSubmit:
			createNewCase();
		}

	}

	private void startStopPlaying() {
		// play recorded file
		onPlay(mStartPlaying);
		if (mStartPlaying) {
			mTextAudioStatus.setText("Stop playing");
		} else {
			mTextAudioStatus.setText("Start playing");
		}
		mStartPlaying = !mStartPlaying;
	}

	private void recordStopAudio() {
		noAudioSelected = false;
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
			mPlayer.setDataSource(mAudioFilePath);
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
		mAudioFilePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());
		mAudioFilePath += "/audio" + timeStamp + ".3gp";
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mAudioFilePath);
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

	private void showAudioSelectionOptions() {
		String[] items = new String[] { "Record Audio", "Select From gallery" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, items);
		new AlertDialog.Builder(this).setTitle("Audio Selector")
				.setAdapter(adapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							recordStopAudio();
						} else {
							selectAudioFromGallery();
						}
						dialog.dismiss();
					}

				}).create().show();
	}

	private void selectAudioFromGallery() {
		Intent intent_upload = new Intent();
		intent_upload.setType("audio/*");
		intent_upload.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent_upload, REQUEST_SELECT_AUDIO_FROM_GALLERY);

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
				mImageFilePath = fileUri.getPath();
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
		mImageFilePath = image.getAbsolutePath();
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
		mImageFilePath = cursor.getString(columnIndex);
		cursor.close();
		compressImage(mImageFilePath);
		Utils.setPic(mImageFilePath, mImageView);
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
		if (mImageFilePath != null) {
			savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY,
					mImageFilePath);
		}
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
			mImageFilePath = savedInstanceState
					.getString(CAPTURED_PHOTO_PATH_KEY);
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
			compressImage(mImageFilePath);
			addPicToGallery(mImageFilePath);
			// Show the full sized image.
			Utils.setPic(mImageFilePath, mImageView);
			mImageSelected = true;
			mEditImageView.setVisibility(View.VISIBLE);
			break;
		case REQUEST_SELECT_AUDIO_FROM_GALLERY:
			// the selected audio.
			Uri uri = data.getData();
			mAudioFilePath = getRealPathFromURI(CreateMedicalCaseActivity.this,
					uri);
			mAudioRecordCompleted = true;
			noAudioSelected = false;
			mEditAudio.setVisibility(View.VISIBLE);
			mButtonAudio.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.play));
			mTextAudioStatus.setText("Start playing");
			mEditAudio.setEnabled(true);

			break;
		}

	}

	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
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

	private void createNewCase() {
		mDialogManager.showProgressDialog(CreateMedicalCaseActivity.this,
				"Creating case", "Please wait...");
		if (mImageFilePath != null) {
			uploadImage();
		} else if (mAudioRecordCompleted) {
			uploadAudio();
		} else {
			Utils.createCase(CreateMedicalCaseActivity.this, getCaseDetails(),
					mCreateCaseResponseHandler);
		}
	}

	private void uploadImage() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());
		// get the file extension
		String ext = Utils.getFileExtension(mImageFilePath);
		mImageFileName = "images/JPEG_" + timeStamp + "." + ext;
		Utils.uploadImageToS3(mImageFilePath, mImageFileName, this);
	}

	@Override
	public void onImageUploadCompleted(Boolean status) {

		if (status) {
			if (mAudioRecordCompleted) {
				mDialogManager.showProgressDialog(
						CreateMedicalCaseActivity.this, "Uploading Image...");
				uploadAudio();

			} else {
				mDialogManager.showProgressDialog(
						CreateMedicalCaseActivity.this, "Creating Case...");
				// create case
				Utils.createCase(CreateMedicalCaseActivity.this,
						getCaseDetails(), mCreateCaseResponseHandler);
			}
		} else {
			// upload failure
			// TODO give retry option
			mDialogManager.removeProgressDialog();
		}

	}

	private void uploadAudio() {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());
		// get the file extension
		String ext = Utils.getFileExtension(mAudioFilePath);
		mAudioFileName = "audios/3GP_" + timeStamp + "." + ext;
		Utils.uploadAudioToS3(mAudioFilePath, mAudioFileName, this);

	}

	@Override
	public void onAudioUploadCompleted(Boolean status) {
		if (status) {
			mDialogManager.showProgressDialog(CreateMedicalCaseActivity.this,
					"Creating case...");
			// create case
			Utils.createCase(CreateMedicalCaseActivity.this, getCaseDetails(),
					mCreateCaseResponseHandler);
		} else {
			// TODO give retry option
			mDialogManager.removeProgressDialog();
		}
	}

	private JsonHttpResponseHandler mCreateCaseResponseHandler = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				org.json.JSONObject response) {
			// mDialogManager.removeProgressDialog();
			Case newCase = new Case();
			Gson gson = new Gson();
			newCase = gson.fromJson(response.toString(), newCase.getClass());
			Utils.createFirstComment(CreateMedicalCaseActivity.this,
					newCase.getId(), mEditDetails.getText().toString(),
					mImageFileName, mAudioFileName,
					mCreateCommentResponseHandler);
			// Toast.makeText(getApplicationContext(),
			// "Success:" + new Gson().toJson(response).toString(),
			// Toast.LENGTH_LONG).show();
		}

		@Override
		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				java.lang.Throwable e, org.json.JSONObject errorResponse) {
			// Response failed :(
			String err = "onFailure status code:" + statusCode
					+ " response body:" + errorResponse + " error:" + e
					+ " headers:" + headers;
			Log.d("err", err);
			mDialogManager.removeProgressDialog();
			Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG)
					.show();

		}

	};

	private JsonHttpResponseHandler mCreateCommentResponseHandler = new JsonHttpResponseHandler() {

		@Override
		public void onSuccess(int statusCode, Header[] headers,
				org.json.JSONObject response) {
			mDialogManager.removeProgressDialog();
			// Case newCase = new Case();
			// Gson gson = new Gson();
			// newCase = gson.fromJson(response.toString(), newCase.getClass());
			// Utils.createFirstComment(newCase.getId(),mEditDetails.getText().toString(),mImageFileName,mAudioFileName);
			// Toast.makeText(CreateMedicalCaseActivity.this,
			// "Success:" + new Gson().toJson(response).toString(),
			// Toast.LENGTH_LONG).show();
			setResult(RESULT_OK);
			finish();
		}

		@Override
		public void onFailure(int statusCode, org.apache.http.Header[] headers,
				java.lang.Throwable e, org.json.JSONObject errorResponse) {
			// Response failed :(
			String err = "onFailure status code:" + statusCode
					+ " response body:" + errorResponse + " error:" + e
					+ " headers:" + headers;
			Log.d("err", err);
			mDialogManager.removeProgressDialog();
			Toast.makeText(CreateMedicalCaseActivity.this, err,
					Toast.LENGTH_LONG).show();

		}

	};

	private Case getCaseDetails() {
		Case newCase = new Case();
		newCase.setAge(Integer.parseInt(mEditAge.getText().toString()));
		newCase.setName(mEditName.getText().toString());
		newCase.setGender((String) mSpinnerGender.getSelectedItem());
		return newCase;
	}

	public String compressImage(String filePath) {

		Bitmap scaledBitmap = null;

		BitmapFactory.Options options = new BitmapFactory.Options();

		// by setting this field as true, the actual bitmap pixels are not
		// loaded in the memory. Just the bounds are loaded. If
		// you try the use the bitmap here, you will get null.
		options.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

		int actualHeight = options.outHeight;
		int actualWidth = options.outWidth;

		// max Height and width values of the compressed image is taken as
		// 816x612

		float maxHeight = 816.0f;
		float maxWidth = 612.0f;
		float imgRatio = actualWidth / actualHeight;
		float maxRatio = maxWidth / maxHeight;

		// width and height values are set maintaining the aspect ratio of the
		// image

		if (actualHeight > maxHeight || actualWidth > maxWidth) {
			if (imgRatio < maxRatio) {
				imgRatio = maxHeight / actualHeight;
				actualWidth = (int) (imgRatio * actualWidth);
				actualHeight = (int) maxHeight;
			} else if (imgRatio > maxRatio) {
				imgRatio = maxWidth / actualWidth;
				actualHeight = (int) (imgRatio * actualHeight);
				actualWidth = (int) maxWidth;
			} else {
				actualHeight = (int) maxHeight;
				actualWidth = (int) maxWidth;

			}
		}

		// setting inSampleSize value allows to load a scaled down version of
		// the original image

		options.inSampleSize = calculateInSampleSize(options, actualWidth,
				actualHeight);

		// inJustDecodeBounds set to false to load the actual bitmap
		options.inJustDecodeBounds = false;

		// this options allow android to claim the bitmap memory if it runs low
		// on memory
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16 * 1024];

		try {
			// load the bitmap from its path
			bmp = BitmapFactory.decodeFile(filePath, options);
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();

		}
		try {
			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,
					Bitmap.Config.ARGB_8888);
		} catch (OutOfMemoryError exception) {
			exception.printStackTrace();
		}

		float ratioX = actualWidth / (float) options.outWidth;
		float ratioY = actualHeight / (float) options.outHeight;
		float middleX = actualWidth / 2.0f;
		float middleY = actualHeight / 2.0f;

		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
				middleY - bmp.getHeight() / 2, new Paint(
						Paint.FILTER_BITMAP_FLAG));

		// check the rotation of the image and display it properly
		ExifInterface exif;
		try {
			exif = new ExifInterface(filePath);

			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, 0);
			Log.d("EXIF", "Exif: " + orientation);
			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 3) {
				matrix.postRotate(180);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 8) {
				matrix.postRotate(270);
				Log.d("EXIF", "Exif: " + orientation);
			}
			scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
					scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
					true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filePath);

			// write the compressed bitmap at the destination specified by
			// filename.
			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return filePath;

	}

	public int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		final float totalPixels = width * height;
		final float totalReqPixelsCap = reqWidth * reqHeight * 2;
		while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
			inSampleSize++;
		}

		return inSampleSize;
	}
}