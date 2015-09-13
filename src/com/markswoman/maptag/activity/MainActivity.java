package com.markswoman.maptag.activity;

import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.markswoman.maptag.R;
import com.markswoman.maptag.activity.base.BaseActivity;
import com.markswoman.maptag.fragments.LogoFragment;
import com.markswoman.maptag.helper.DatabaseHandler;

import static com.markswoman.maptag.utils.CommonUtils.CUSTOM_MARKERS;

public class MainActivity extends BaseActivity {

	private DatabaseHandler database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		database = new DatabaseHandler(this);
		// database.upgradeTable();
		CUSTOM_MARKERS = database.getAllRows();

		AdView adView = (AdView) findViewById(R.id.adView);

		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice("INSERT_YOUR_HASHED_DEVICE_ID_HERE").build();

		// Start loading the ad in the background.
		adView.loadAd(adRequest);

		toggleActionBar(false);
		showFragment(BaseActivity.content_frame, LogoFragment.newInstance());
	}
}
