package com.markswoman.maptag.utils;

import java.util.ArrayList;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.markswoman.maptag.model.CustomMarker;

public class CommonUtils {

	public static ArrayList<CustomMarker> CUSTOM_MARKERS;

	/**** Check if the value is exist in the list ****/
	public static boolean isExistInList(ArrayList<String> list, String value) {
		if (list == null)
			return false;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(value)) {
				return true;
			}
		}
		return false;
	}

	/**** Check whether Google Play Services installed or not ****/
	public static boolean checkGoogleServices(Context context) {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);

		if (resultCode != ConnectionResult.SUCCESS) {
			return false;
		}
		return true;
	}

	public static String locationStringFromDouble(final double value) {
		int degrees = (int) value;
		double decimal = Math.abs(value - degrees);
		int minutes = (int) (decimal * 60);
		double seconds = decimal * 3600 - minutes * 60;
		String result = String.format("%d° %d' %1.3f\"", degrees, minutes,
				seconds);
		return result;
	}
}
