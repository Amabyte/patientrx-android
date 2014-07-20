package com.matrix.patientrx.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

public class Utils {

	public static boolean isInternetConnection(Context context) {
		ConnectivityManager cn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nf = cn.getActiveNetworkInfo();
		if (nf != null && nf.isConnected() == true)
			return true;
		else
			return false;
	}

	public static String getImei(Context context) {

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		return telephonyManager.getDeviceId();
	}

	public static int getDisplayWidth(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		return displaymetrics.widthPixels;
	}

	public static int getDisplayHeight(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		return displaymetrics.heightPixels;
	}

	public static String getDateInFormat(String date) {
		SimpleDateFormat fromFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
		SimpleDateFormat toFormat = new SimpleDateFormat("dd MMM yy HH:mm aaa",
				Locale.getDefault());

		Calendar cal = Calendar.getInstance();

		try {
			cal.setTime(fromFormat.parse(date));
			return toFormat.format(cal.getTime());
		} catch (ParseException e) {
		}
		return "";
	}

}
