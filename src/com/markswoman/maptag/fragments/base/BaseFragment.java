package com.markswoman.maptag.fragments.base;

import com.markswoman.maptag.activity.base.BaseActivity;
import com.markswoman.maptag.helper.ConnectionDetector;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragment extends Fragment {

	protected BaseActivity baseActivity;
	protected View rootView;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			baseActivity = (BaseActivity) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must extend BaseActivity");
		}
	}

	public boolean backButtonPressed() {
		return true;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/**
	 * 
	 * @param inflater
	 * @param container
	 * @param resource
	 * @return true if view was inflated
	 */
	protected boolean inflateViewIfNull(LayoutInflater inflater,
			ViewGroup container, int resource) {
		if (rootView == null) {
			rootView = inflater.inflate(resource, container, false);
			return true;
		} else {
			((ViewGroup) rootView.getParent()).removeView(rootView);
			return false;
		}
	}

	protected boolean isConnectedToInternet() {
		ConnectionDetector detector = new ConnectionDetector(baseActivity);
		return detector.isConnectingToInternet();
	}
}
