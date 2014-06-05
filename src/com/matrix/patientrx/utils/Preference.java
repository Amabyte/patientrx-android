package com.matrix.patientrx.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {

	private static final String PREFERENCE_NAME = Preference.class.getName();

	private static Context context = null;

	public static void setContext(Context context) {
		Preference.context = context;
	}

	private static SharedPreferences getPreferences() {
		return context.getSharedPreferences(PREFERENCE_NAME, 0);
	}

	public static String getString(String key) {
		return getPreferences().getString(key, null);
	}

	public static boolean getBoolean(String key) {
		return getPreferences().getBoolean(key, false);
	}

	public static void setString(String key, String value) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void setBoolean(String key, boolean value) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static void remove(String key) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.remove(key);
		editor.commit();
	}
}
