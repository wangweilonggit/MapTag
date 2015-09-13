package com.markswoman.maptag.activity.base;

import java.util.UUID;

import com.markswoman.maptag.R;
import com.markswoman.maptag.fragments.base.BaseFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

@SuppressLint("NewApi")
public class BaseActivity extends FragmentActivity {

	protected BaseFragment rootFragment;
	private BaseFragment activeFragment;
	public final static int content_frame = R.id.container_fragment;
	private static Context context;

	@Override
	public View onCreateView(String name, Context c, AttributeSet attrs) {
		context = c;
		return super.onCreateView(name, context, attrs);
	}

	public void showFragment(int contentFrame, BaseFragment fragment) {
		showFragment(contentFrame, fragment, false);
	}

	public void showFragment(int contentFrame, BaseFragment fragment,
			boolean addToBackStack) {
		this.activeFragment = fragment;
		String tag = UUID.randomUUID().toString();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.setCustomAnimations(R.anim.abc_fade_in,
				R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out);
		transaction.replace(contentFrame, fragment, tag);
		if (addToBackStack) {
			transaction.addToBackStack(tag);
		}
		transaction.commit();
		getSupportFragmentManager().executePendingTransactions();
		if (getSupportFragmentManager().getBackStackEntryCount() == 0)
			rootFragment = fragment;
	}

	/**
	 * Enables the cleanup of all stack before adding this fragment. Can be
	 * useful to make the Dashboard or other fragment the base fragment in terms
	 * of order
	 * 
	 * @param contentFrame
	 * @param fragment
	 * @param addToBackStack
	 * @param remove
	 */
	public void showFragment(int contentFrame, BaseFragment fragment,
			boolean addToBackStack, boolean remove) {

		if (remove) {
			this.popToRoot();
		}
		showFragment(contentFrame, fragment, addToBackStack);
	}

	public void popToRoot() {
		int backStackCount = getSupportFragmentManager()
				.getBackStackEntryCount();
		for (int i = 0; i < backStackCount; i++) {
			// Get the back stack fragment id.
			int backStackId = getSupportFragmentManager()
					.getBackStackEntryAt(i).getId();
			getSupportFragmentManager().popBackStack(backStackId,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		activeFragment = null;
	}

	@Override
	public void onBackPressed() {
		if (activeFragment != null) {
			if (activeFragment.backButtonPressed()) {
				super.onBackPressed();
				int backStackCount = getSupportFragmentManager()
						.getBackStackEntryCount();
				if (backStackCount > 0) {
					String tag = getSupportFragmentManager()
							.getBackStackEntryAt(backStackCount - 1).getName();
					activeFragment = (BaseFragment) getSupportFragmentManager()
							.findFragmentByTag(tag);
				} else {
					if (activeFragment.equals(rootFragment))
						activeFragment = null;
					else
						activeFragment = rootFragment;
				}
			}
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	/** Get the application context of the activity **/
	public static Context getContext() {
		return context;
	}

	/** Show or hide the ActionBar **/
	public void toggleActionBar(boolean flag) {
		if (flag == true) {
			getActionBar().show();
		} else {
			getActionBar().hide();
		}
	}
}