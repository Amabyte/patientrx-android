package com.matrix.patientrx.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.matrix.patientrx.constants.Constants;
import com.matrix.patientrx.http.RxRestClient;
import com.matrix.patientrx.listeners.AudioUploadListener;
import com.matrix.patientrx.listeners.ImageUploadListener;

public class Utils {

	public static void loginToPatientRx(String provider, String token,
			JsonHttpResponseHandler jsonHttpResponseHandler) {
		RequestParams params = new RequestParams();
		params.put("provider", provider);
		params.put("token", token);
		RxRestClient.post("patients/social_login.json", params,
				jsonHttpResponseHandler);
	}

	public static void getProfile(
			JsonHttpResponseHandler jsonHttpResponseHandler) {
		RxRestClient
				.get("patients/profile.json", null, jsonHttpResponseHandler);
	}

	public static void logout(JsonHttpResponseHandler jsonHttpResponseHandler) {
		RxRestClient.delete("patients/sign_out.json", jsonHttpResponseHandler);
	}

	public static void getAllCasess(
			JsonHttpResponseHandler asyncHttpResponseHandler) {
		RxRestClient.get("cases.json", null, asyncHttpResponseHandler);
	}

	public static void setPic(String photoPath, ImageView image) {
		// Get the dimensions of the View
		int targetW = image.getWidth();
		int targetH = image.getHeight();

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(photoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
		image.setImageBitmap(bitmap);
	}

	public static void uploadImageToS3(String imagePath,
			final ImageUploadListener listener) {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());
		String imageFileName = "images/JPEG_" + timeStamp + "_.JPG";

		File file = new File(imagePath);
		TransferManager tx = AwsTransferManager.getTransferManager();
		final Upload myUpload = tx.upload(Constants.AWS_S3_VIDEO_BUCKET,
				imageFileName, file);

		// listener to track upload progress
		ProgressListener myProgressListener = new ProgressListener() {

			@Override
			public void progressChanged(final ProgressEvent progressEvent) {
				final int percentage = (int) myUpload.getProgress()
						.getPercentTransferred();
				Log.e("Test", "Percentage:" + percentage);
				if (progressEvent.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) {
					listener.onImageUploadCompleted(true);
				}
				if (progressEvent.getEventCode() == ProgressEvent.FAILED_EVENT_CODE) {
					listener.onImageUploadCompleted(false);
				}
			}
		};
		// Transfers also allow you to set a ProgressListener to receive
		// asynchronous notifications about your transfer's progress.
		myUpload.addProgressListener(myProgressListener);
	}

	public static void uploadAudioToS3(String imagePath,
			final AudioUploadListener listener) {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());
		String imageFileName = "audios/3GP_" + timeStamp + "_.3gp";

		File file = new File(imagePath);
		TransferManager tx = AwsTransferManager.getTransferManager();
		final Upload myUpload = tx.upload(Constants.AWS_S3_VIDEO_BUCKET,
				imageFileName, file);

		// listener to track upload progress
		ProgressListener myProgressListener = new ProgressListener() {
			@Override
			public void progressChanged(final ProgressEvent progressEvent) {
				final int percentage = (int) myUpload.getProgress()
						.getPercentTransferred();
				Log.e("Test", "Percentage:" + percentage);
				if (progressEvent.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) {
					listener.onAudioUploadCompleted(true);
				}
				if (progressEvent.getEventCode() == ProgressEvent.FAILED_EVENT_CODE) {
					listener.onAudioUploadCompleted(false);
				}
			}
		};
		// Transfers also allow you to set a ProgressListener to receive
		// asynchronous notifications about your transfer's progress.
		myUpload.addProgressListener(myProgressListener);
	}
}
