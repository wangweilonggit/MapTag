package com.markswoman.maptag.adapters;

import static com.markswoman.maptag.utils.CommonUtils.CUSTOM_MARKERS;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.markswoman.maptag.R;
import com.markswoman.maptag.fragments.DashboardFragment;
import com.markswoman.maptag.model.CustomMarker;
import com.markswoman.maptag.utils.CommonUtils;

public class TagListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private FragmentManager fm;

	public TagListAdapter(Context context, FragmentManager fm) {
		this.inflater = LayoutInflater.from(context);
		this.fm = fm;
	}

	@Override
	public int getCount() {
		return CUSTOM_MARKERS.size();
	}

	@Override
	public CustomMarker getItem(int position) {
		return CUSTOM_MARKERS.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.tag_list_item, parent,
					false);
		}
		View vi = convertView;
		TextView marker_name = (TextView) vi.findViewById(R.id.list_item_name);
		TextView marker_desc = (TextView) vi.findViewById(R.id.list_item_desc);
		TextView marker_coordinates = (TextView) vi
				.findViewById(R.id.list_item_coordinates);
		CustomMarker marker = getItem(position);
		marker_name.setText(marker.getName());
		marker_desc.setText(marker.getDescription());
		// String pos_text = Double.toString(marker.getLatitude()) + ","
		// + Double.toString(marker.getLongitude());
		String coordinates = CommonUtils.locationStringFromDouble(marker
				.getLatitude())
				+ " , "
				+ CommonUtils.locationStringFromDouble(marker.getLongitude());
		marker_coordinates.setText(coordinates);

		ImageView marker_button = (ImageView) vi
				.findViewById(R.id.list_item_icon);

		final CustomMarker selected_marker = marker;
		marker_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DashboardFragment.selectedMarker = selected_marker;
				fm.popBackStack();
			}
		});
		return convertView;
	}
}
