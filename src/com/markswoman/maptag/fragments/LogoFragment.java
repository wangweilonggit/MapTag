package com.markswoman.maptag.fragments;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.markswoman.maptag.R;
import com.markswoman.maptag.activity.base.BaseActivity;
import com.markswoman.maptag.fragments.base.BaseFragment;
import com.markswoman.maptag.utils.CommonUtils;
import com.markswoman.maptag.utils.UIUtils;

/**
 * Fragment class for the logo screen
 * 
 * @author ChengMin - Hong
 * 
 */
public class LogoFragment extends BaseFragment {

	private View view;
	private Context _context;

	public static LogoFragment newInstance() {
		LogoFragment fragment = new LogoFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.view = inflater.inflate(R.layout.fragment_logo, container, false);
		return this.view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		_context = BaseActivity.getContext();

		if (!CommonUtils.checkGoogleServices(_context)) {
			UIUtils.showMessage("Unable to start app. You have to install Google Play Services");
			baseActivity.finish();
		}

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				baseActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						baseActivity.showFragment(BaseActivity.content_frame,
								DashboardFragment.newInstance());
					}
				});
			}
		}, 3000);
	}
}
