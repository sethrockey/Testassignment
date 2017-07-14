package com.sj.cityweather.dialog;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.sj.cityweather.R;

/**
 * Simple class that provides helper method for dialogs
 */
public class DialogFactory {

	/**
	 * Listener passed to dialog, so we can get the response
	 * we need from the dialog.
	 * @param <T>
	 */
	public interface DialogListener<T> {
		void onSuccess(T t);
		void onFailure();
	}

	/**
	 * Shown an input dialog view, where user need's to enter a city name,
	 * result is passed to the caller.
	 * @param activity
	 * @param listener
	 */
	public static void showCityInputDialog(AppCompatActivity activity, final DialogListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		View view = LayoutInflater.from(activity).inflate(R.layout.ciyt_input_view, null);
		final EditText editText = (EditText) view.findViewById(R.id.cityInput);
		editText.setHint(R.string.city_hint);
		builder.setView(view);
		builder.setTitle(R.string.city_dialog_title);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.button_set, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				String cityName = editText.getText().toString();
				listener.onSuccess(cityName);
			}
		});

		builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				listener.onFailure();
			}
		});

		builder.create().show();
	}
}
