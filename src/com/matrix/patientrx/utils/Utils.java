package com.matrix.patientrx.utils;

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
}
