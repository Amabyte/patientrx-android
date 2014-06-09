package com.matrix.patientrx.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.matrix.patientrx.http.RxRestClient;

public class Utils {

	public static void loginToPatientRx(String provider, String token,
			JsonHttpResponseHandler asyncHttpResponseHandler) {
		RequestParams params = new RequestParams();
		params.put("provider", provider);
		params.put("token", token);
		RxRestClient.post("patients/social_login.json", params,
				asyncHttpResponseHandler);
	}

	public static void getProfile(
			JsonHttpResponseHandler asyncHttpResponseHandler) {
		RxRestClient.get("patients/profile.json", null,
				asyncHttpResponseHandler);
	}

	public static void logout(JsonHttpResponseHandler asyncHttpResponseHandler) {
		RxRestClient.delete("patients/sign_out.json", asyncHttpResponseHandler);
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
}
