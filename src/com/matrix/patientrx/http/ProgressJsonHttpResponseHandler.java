package com.matrix.patientrx.http;

import android.app.Activity;
import android.app.ProgressDialog;

import com.loopj.android.http.JsonHttpResponseHandler;

public class ProgressJsonHttpResponseHandler extends JsonHttpResponseHandler {
	ProgressDialog dialog;

	public ProgressJsonHttpResponseHandler(Activity activity) {
		dialog = new ProgressDialog(activity);
	}

	@Override
	public void onStart() {
		dialog.setMessage("Please wait...");
		dialog.setCancelable(false);
		dialog.show();
		super.onStart();
	}

	@Override
	public void onFinish() {
		super.onFinish();
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
