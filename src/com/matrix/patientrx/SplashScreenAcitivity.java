package com.matrix.patientrx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenAcitivity extends Activity  {
	private final int SPLASH_SCREEN_TIME_OUT = 1000;
	private Handler mHandler = new Handler();
	private Runnable mRunnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		mRunnable = new Runnable() {

			@Override
			public void run() {
				startActivity(new Intent(SplashScreenAcitivity.this,
						LoginActivity.class));
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
