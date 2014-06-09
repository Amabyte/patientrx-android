package com.matrix.patientrx.http;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RxRestClient {
	private static final String BASE_URL = "http://patientrx.herokuapp.com/api/";

	private static AsyncHttpClient client = null;

	public static AsyncHttpClient getInstance() {
		if (client == null) {
			client = new AsyncHttpClient();
			client.addHeader("Content-Type", "application/json");
			client.addHeader("Accept",
					"application/vnd.patientrx+json;version=1");
		}
		return client;
	}

	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
//		getInstance().addHeader("Cookie",
//				Preference.getString(Constants.SESSION_ID));
		getInstance().get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		Log.e("test", "Url:" + getAbsoluteUrl(url));
		getInstance().post(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void delete(String url,
			AsyncHttpResponseHandler responseHandler) {
		getInstance().delete(getAbsoluteUrl(url),responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
}