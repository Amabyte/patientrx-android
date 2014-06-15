package com.matrix.patientrx.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
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
import com.matrix.patientrx.models.Case;

public class Utils {

	public static void loginToPatientRx(String provider, String token,
			JsonHttpResponseHandler jsonHttpResponseHandler) {
		RequestParams params = new RequestParams();
		params.put("provider", provider);
		params.put("token", token);
		RxRestClient.post("patients/social_login.json", params,
				jsonHttpResponseHandler);
	}

	public static void createCase(Context context, Case newCase,
			JsonHttpResponseHandler asyncHttpResponseHandler) {
		JSONObject jsonParams = new JSONObject();
		StringEntity entity = null;
		try {
			jsonParams.put("name", newCase.getName());
			jsonParams.put("age", newCase.getAge());
			jsonParams.put("gender", newCase.getGender());
			JSONObject caseObject = new JSONObject();
			caseObject.put("case", jsonParams);
			entity = new StringEntity(caseObject.toString());
		} catch (JSONException e) {
		} catch (UnsupportedEncodingException e) {
		}

		RxRestClient.post(context, "cases.json", entity,
				asyncHttpResponseHandler);
	}

	public static void createFirstComment(Context context, int id,
			String message, String imageFileName, String audioFileName,
			JsonHttpResponseHandler createCommentResponseHandler) {
		JSONObject jsonParams = new JSONObject();
		StringEntity entity = null;
		// {
		// "case_comment": {
		// "message": "its a testing message",
		// "image": "1228332323537123.png",
		// "audio": "21623421431243612.mp3"
		// }
		// }
		try {
			jsonParams.put("message", message);
			if (imageFileName != null && !imageFileName.equals("")) {
				jsonParams.put("image", imageFileName);
			}
			if (audioFileName != null && !audioFileName.equals("")) {
				jsonParams.put("audio", audioFileName);
			}
			JSONObject caseObject = new JSONObject();
			caseObject.put("case_comment", jsonParams);
			entity = new StringEntity(caseObject.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = "cases/" + id + "/case_comments.json";
		RxRestClient.post(context, url, entity, createCommentResponseHandler);
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

	public static void uploadImageToS3(String imagePath, String imageFileName,
			final ImageUploadListener listener) {
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

	public static void uploadAudioToS3(String imagePath, String imageFileName,
			final AudioUploadListener listener) {
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

	public static String getFileExtension(String str) {
		String s[] = str.split("/");
		return s[s.length - 1].split("\\.")[1];
	}

	public static String getDateInFormat(String date) {
		// TODO change the format
		return date;
	}

}
