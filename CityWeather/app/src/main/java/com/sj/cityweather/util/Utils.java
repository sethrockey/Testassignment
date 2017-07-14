package com.sj.cityweather.util;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

/**
 * Provide some helper method that can be used at multiple places.
 */
public class Utils {

	/**
	 * Method to hide the soft key pad from the screen
	 * @param activity
	 */
	public static void hideSoftKeypad(AppCompatActivity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}

	public static void log(Object object, String msg) {
		String tag = object.toString();
		if (object instanceof Class) {
			Class aClass = (Class) object;
			tag = aClass.getName();
		}
		Log.i(tag, msg);
	}
}
