package com.markswoman.maptag.utils;

import com.markswoman.maptag.activity.base.BaseActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.WindowManager.BadTokenException;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UIUtils {

	public static void showMessage(int resId) {
		showMessage(BaseActivity.getContext().getString(resId));
	}

	public static void showMessage(String message) {
		Toast.makeText(BaseActivity.getContext(), message, Toast.LENGTH_LONG)
				.show();
	}

	public static ProgressDialog createBorderlessProgressDialog(Context context) {
		ProgressDialog dialog = new ProgressDialog(context);
		try {
			dialog.show();
		} catch (BadTokenException e) {
			// do nothing
		}
		dialog.setCancelable(false);
		dialog.setContentView(new ProgressBar(context));

		return dialog;
	}

	public static String getStringRes(int resId) {
		return BaseActivity.getContext().getString(resId);
	}
}
