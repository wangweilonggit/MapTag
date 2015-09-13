package com.markswoman.maptag.fragments;

import static com.markswoman.maptag.utils.CommonUtils.CUSTOM_MARKERS;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.markswoman.maptag.R;
import com.markswoman.maptag.activity.base.BaseActivity;
import com.markswoman.maptag.fragments.base.BaseFragment;
import com.markswoman.maptag.helper.DatabaseHandler;
import com.markswoman.maptag.helper.GPSTracker;
import com.markswoman.maptag.model.CustomMarker;
import com.markswoman.maptag.utils.CommonUtils;
import com.markswoman.maptag.utils.UIUtils;

/**
 * DashBoard Fragment class which is corresponding to the main screen of the
 * application
 * 
 * @author ChengMin - Hong
 * 
 */
public class DashboardFragment extends BaseFragment implements OnClickListener,
		OnMarkerClickListener, OnInfoWindowClickListener {

	private View view;
	private Context _context;

	private GoogleMap googleMap;
	private Marker currentMarker;
	private Marker updatingMarker;

	private double latitude;
	private double longitude;

	private GPSTracker gps;
	private LayoutInflater inflater;

	private DatabaseHandler database;

	private boolean isTagged = false;

	private final int MAGNIFY_RATE = 16;

	private boolean is_new_tag = true;

	private String current_name = "";
	private String current_desc = "";

	public static CustomMarker selectedMarker;

	public static DashboardFragment newInstance() {
		DashboardFragment fragment = new DashboardFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		inflateViewIfNull(inflater, container, R.layout.fragment_dashboard);
		this.inflater = inflater;

		_context = BaseActivity.getContext();

		baseActivity.toggleActionBar(true);
		ActionBar actionBar = baseActivity.getActionBar();

		View custom_view = inflater.inflate(R.layout.custom_actionbar,
				container, false);
		actionBar.setCustomView(custom_view);

		custom_view.findViewById(R.id.btn_show_tag_list).setOnClickListener(
				this);
		custom_view.findViewById(R.id.btn_get_current_position)
				.setOnClickListener(this);
		custom_view.findViewById(R.id.btn_tag_current_position)
				.setOnClickListener(this);

		this.view = rootView;
		return this.view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		gps = new GPSTracker(_context);
		database = new DatabaseHandler(_context);

		try {
			initializeMap();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (selectedMarker != null) {
			navigateToPosition(new LatLng(selectedMarker.getLatitude(),
					selectedMarker.getLongitude()));
		}
	}

	/** Get Google Map object from Map Fragment **/
	private void initializeMap() {
		if (googleMap == null) {
			SupportMapFragment map_fragment = (SupportMapFragment) getFragmentManager()
					.findFragmentById(R.id.map_tag_fragment_map);
			googleMap = map_fragment.getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				UIUtils.showMessage("Sorry! unable to create maps");
				return;
			}

			googleMap.setMyLocationEnabled(false);
			googleMap.getUiSettings().setRotateGesturesEnabled(false);
			googleMap.setOnMarkerClickListener(this);
			googleMap.setOnInfoWindowClickListener(this);

			// googleMap.setOnMapLongClickListener(new OnMapLongClickListener()
			// {
			//
			// @Override
			// public void onMapLongClick(LatLng latLng) {
			// CustomMarker clicked = new CustomMarker();
			// int index = -1;
			// if (CUSTOM_MARKERS.size() == 0) {
			// return;
			// }
			// for (int i = 0; i < CUSTOM_MARKERS.size(); i++) {
			// CustomMarker marker = CUSTOM_MARKERS.get(i);
			// if (Math.abs(marker.getLatitude() - latLng.latitude) < 0.0005
			// && Math.abs(marker.getLongitude()
			// - latLng.longitude) < 0.0005) {
			// clicked = marker;
			// index = i;
			// break;
			// }
			// }
			// if (index == -1) {
			// return;
			// }
			// final int current_pos = index;
			// AlertDialog.Builder builder = new AlertDialog.Builder(
			// _context);
			// String message = "Are you sure want to remove \""
			// + clicked.getName() + "\" Tag?";
			// builder.setMessage(message);
			// builder.setPositiveButton("NO", null);
			// builder.setNegativeButton("YES",
			// new DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog,
			// int which) {
			// database.deleteRow(CUSTOM_MARKERS
			// .get(current_pos));
			// CUSTOM_MARKERS.get(current_pos)
			// .getDelegate().remove();
			// CUSTOM_MARKERS.remove(current_pos);
			// }
			// });
			// builder.setCancelable(true);
			// AlertDialog dialog = builder.create();
			// dialog.show();
			// }
			// });

			googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

				@Override
				public View getInfoWindow(Marker marker) {
					return null;
				}

				@Override
				public View getInfoContents(Marker marker) {
					View vi = inflater.inflate(R.layout.ballon, null);
					TextView text_marker_name = (TextView) vi
							.findViewById(R.id.text_view_marker_name);
					TextView text_marker_description = (TextView) vi
							.findViewById(R.id.text_view_marker_description);
					TextView pos_text = (TextView) vi
							.findViewById(R.id.text_view_marker_position);

					String marker_id = marker.getId();
					CustomMarker custom_marker = findMarker(marker_id);

					if (custom_marker != null) {
						text_marker_name.setText(custom_marker.getName());
						text_marker_description.setText(custom_marker
								.getDescription());
					}

					// String coordinates =
					// Double.toString(marker.getPosition().latitude)
					// + ", "
					// + Double.toString(marker.getPosition().longitude);
					String coordinates = CommonUtils
							.locationStringFromDouble(latitude)
							+ " , "
							+ CommonUtils.locationStringFromDouble(longitude);
					pos_text.setText(coordinates);

					return vi;
				}
			});

			showCurrentPositionOnMap();

			if (CUSTOM_MARKERS.size() > 0) {
				for (int i = 0; i < CUSTOM_MARKERS.size(); i++) {
					CustomMarker marker = CUSTOM_MARKERS.get(i);
					Marker delegate = addCurrentMarker(marker);
					marker.setMarkerID(delegate.getId());
					marker.setDelegate(delegate);
					CUSTOM_MARKERS.set(i, marker);
				}
			}
		}

		currentMarker.setVisible(true);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_show_tag_list) {
			baseActivity.showFragment(BaseActivity.content_frame,
					CustomTagListFragment.newInstance(), true);
		} else if (v.getId() == R.id.btn_get_current_position) {
			showCurrentPositionOnMap();
		} else if (v.getId() == R.id.btn_tag_current_position) {
			for (int i = 0; i < CUSTOM_MARKERS.size(); i++) {
				CustomMarker marker = CUSTOM_MARKERS.get(i);
				if (Math.abs(marker.getLatitude() - latitude) < 0.0000001
						&& Math.abs(marker.getLongitude() - longitude) < 0.0000001) {
					UIUtils.showMessage("You have already tagged");
					return;
				}
			}
			is_new_tag = true;
			TagEditDialog dialog = new TagEditDialog();
			dialog.show(getFragmentManager());
		}
	}

	/** Shows user's current position on the map **/
	private void showCurrentPositionOnMap() {
		if (gps.canGetLocation()) {
			if (latitude != gps.getLatitude()
					|| longitude != gps.getLongitude()) {
				isTagged = false;
				if (currentMarker != null) {
					currentMarker.setVisible(true);
				}
			}
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
		} else {
			gps.showSettingsAlert();
		}

		navigateToPosition(new LatLng(latitude, longitude));

		MarkerOptions marker = new MarkerOptions().position(
				new LatLng(latitude, longitude)).title("Current Position");
		marker.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.current_position));
		if (currentMarker == null) {
			currentMarker = googleMap.addMarker(marker);
		} else {
			currentMarker.setPosition(new LatLng(latitude, longitude));
		}
	}

	/** Save current position as the new tag **/
	private void tagCurrentPosition() {
		if (!isTagged && currentMarker != null) {
			CustomMarker marker = new CustomMarker();
			MarkerOptions options = new MarkerOptions().position(new LatLng(
					latitude, longitude));
			options.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.tag_on_map));

			Marker delegate = googleMap.addMarker(options);
			marker.setMarkerID(delegate.getId());
			marker.setName(current_name);
			marker.setDescription(current_desc);
			marker.setLatitude(latitude);
			marker.setLongitude(longitude);
			marker.setDelegate(delegate);

			long new_id = database.addNewRow(marker);
			marker.setID(Long.toString(new_id));
			CUSTOM_MARKERS.add(marker);

			isTagged = true;
			currentMarker.setVisible(false);
		}
	}

	/** Move the camera to the specified position **/
	private void navigateToPosition(LatLng position) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(position).zoom(MAGNIFY_RATE).build();

		googleMap.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}

	/** Add marker on the current position **/
	private Marker addCurrentMarker(CustomMarker marker) {
		MarkerOptions options = new MarkerOptions().position(new LatLng(marker
				.getLatitude(), marker.getLongitude()));
		options.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.tag_on_map));
		return googleMap.addMarker(options);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}

	private CustomMarker findMarker(String marker_id) {
		for (int i = 0; i < CUSTOM_MARKERS.size(); i++) {
			if (marker_id.equals(CUSTOM_MARKERS.get(i).getMarkerID())) {
				return CUSTOM_MARKERS.get(i);
			}
		}
		return null;
	}

	@SuppressLint("ValidFragment")
	public class TagEditDialog extends DialogFragment implements
			OnClickListener {

		private final String TAG = TagEditDialog.class.getName();
		private View view;

		public final void show(FragmentManager fm) {
			TagEditDialog dialog = new TagEditDialog();
			dialog.show(fm, TAG);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			view = inflater.inflate(R.layout.dialog_tag_edit, null);

			builder.setView(view);

			view.findViewById(R.id.btn_save_marker).setOnClickListener(this);
			view.findViewById(R.id.btn_cancel_marker).setOnClickListener(this);

			if (!is_new_tag) {
				((EditText) view.findViewById(R.id.edit_text_marker_name))
						.setText(current_name);
				((EditText) view
						.findViewById(R.id.edit_text_marker_description))
						.setText(current_desc);
			}

			return builder.create();
		}

		@Override
		public void onClick(View button_view) {
			if (button_view.getId() == R.id.btn_save_marker) {
				View v = view;
				EditText edit_text_marker_name = (EditText) v
						.findViewById(R.id.edit_text_marker_name);
				EditText edit_text_marker_desc = (EditText) v
						.findViewById(R.id.edit_text_marker_description);

				String marker_name = edit_text_marker_name.getText().toString();
				String marker_desc = edit_text_marker_desc.getText().toString();

				current_name = marker_name;
				current_desc = marker_desc;

				if (is_new_tag) {
					tagCurrentPosition();
				} else {
					updateMarker();
				}
				dismiss();
			} else if (button_view.getId() == R.id.btn_cancel_marker) {
				dismiss();
			}
		}
	}

	private void updateMarker() {
		CustomMarker marker = findMarker(updatingMarker.getId());
		int index = CUSTOM_MARKERS.indexOf(marker);
		marker.setName(current_name);
		marker.setDescription(current_desc);
		CUSTOM_MARKERS.set(index, marker);
		database.updateRow(marker);
		updatingMarker.showInfoWindow();
	}

	/**
	 * Triggers when user taps on info window
	 * 
	 * @param marker
	 *            {@link Marker} object which triggers click event
	 */
	@Override
	public void onInfoWindowClick(Marker marker) {
		is_new_tag = false;
		updatingMarker = marker;
		CustomMarker custom_marker = findMarker(marker.getId());
		if (custom_marker != null) {
			current_name = custom_marker.getName();
			current_desc = custom_marker.getDescription();
		} else {
			UIUtils.showMessage("Taps on add button on the menu");
			return;
		}
		marker.hideInfoWindow();
		TagEditDialog dialog = new TagEditDialog();
		dialog.show(getFragmentManager());
	}
}
