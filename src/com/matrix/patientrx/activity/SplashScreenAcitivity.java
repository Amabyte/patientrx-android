package com.matrix.patientrx.activity;

import com.matrix.patientrx.R;
import com.matrix.patientrx.constants.Constants;
import com.matrix.patientrx.utils.Preference;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenAcitivity extends Activity {
	private final int SPLASH_SCREEN_TIME_OUT = 1000;
	private Handler mHandler = new Handler();
	private Runnable mRunnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		Preference.setContext(this);

		mRunnable = new Runnable() {

			@Override
			public void run() {
				if (Preference.getString(Constants.SESSION_ID) != null) {
					startActivity(new Intent(SplashScreenAcitivity.this,
							HomeActivity.class));
				} else {
					// TODO uncomment this after implementing facebook login
					// startActivity(new Intent(SplashScreenAcitivity.this,
					// LoginActivity.class));

					startActivity(new Intent(SplashScreenAcitivity.this,
							GoogleLoginActivity.class));
				}
				finish();
			}

		};
		mHandler.postDelayed(mRunnable, SPLASH_SCREEN_TIME_OUT);
	}

	@Override
	public void onBackPressed() {
		mHandler.removeCallbacks(mRunnable);
		super.onBackPressed();
	}
}
