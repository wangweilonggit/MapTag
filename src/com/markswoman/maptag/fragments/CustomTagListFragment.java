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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;

import com.markswoman.maptag.R;
import com.markswoman.maptag.activity.base.BaseActivity;
import com.markswoman.maptag.adapters.TagListAdapter;
import com.markswoman.maptag.fragments.base.BaseFragment;
import com.markswoman.maptag.helper.DatabaseHandler;
import com.markswoman.maptag.model.CustomMarker;

public class CustomTagListFragment extends BaseFragment {

	private String current_name;
	private String current_desc;
	private int current_pos;

	private View view;
	private TagListAdapter adapter;
	private Context _context;

	private DatabaseHandler database;

	public static CustomTagListFragment newInstance() {
		CustomTagListFragment fragment = new CustomTagListFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ActionBar actionBar = baseActivity.getActionBar();
		View custom_view = actionBar.getCustomView();
		custom_view.findViewById(R.id.btn_show_tag_list).setVisibility(
				View.GONE);
		custom_view.findViewById(R.id.btn_get_current_position).setVisibility(
				View.GONE);
		custom_view.findViewById(R.id.btn_tag_current_position).setVisibility(
				View.GONE);

		this.view = inflater.inflate(R.layout.fragment_tag_list, container,
				false);
		return this.view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		_context = BaseActivity.getContext();

		database = new DatabaseHandler(_context);

		View vi = this.view;
		ListView tag_list = (ListView) vi.findViewById(R.id.list_view_tag_list);
		adapter = new TagListAdapter(BaseActivity.getContext(),
				getFragmentManager());
		tag_list.setAdapter(adapter);

		tag_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				current_pos = position;
				current_name = CUSTOM_MARKERS.get(current_pos).getName();
				current_desc = CUSTOM_MARKERS.get(current_pos).getDescription();
				TagEditDialog dialog = new TagEditDialog();
				dialog.show(getFragmentManager());
			}
		});

		tag_list.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(_context);
				final CustomMarker marker = CUSTOM_MARKERS.get(position);
				String message = "Are you sure want to remove \""
						+ marker.getName() + "\" Tag?";
				builder.setMessage(message);
				current_pos = position;
				builder.setPositiveButton("NO", null);
				builder.setNegativeButton("YES",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								database.deleteRow(marker);
								CUSTOM_MARKERS.get(current_pos).getDelegate()
										.remove();
								CUSTOM_MARKERS.remove(current_pos);
								adapter.notifyDataSetChanged();
							}
						});
				builder.setCancelable(true);
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
			}
		});
	}

	private void updateTag() {
		CustomMarker marker = CUSTOM_MARKERS.get(current_pos);
		marker.setName(current_name);
		marker.setDescription(current_desc);
		CUSTOM_MARKERS.set(current_pos, marker);
		database.updateRow(marker);
		adapter.notifyDataSetChanged();
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
			LayoutInflater inflater = LayoutInflater.from(_context);
			view = inflater.inflate(R.layout.dialog_tag_edit, null);

			builder.setView(view);

			view.findViewById(R.id.btn_save_marker).setOnClickListener(this);
			view.findViewById(R.id.btn_cancel_marker).setOnClickListener(this);

			((EditText) view.findViewById(R.id.edit_text_marker_name))
					.setText(current_name);
			((EditText) view.findViewById(R.id.edit_text_marker_description))
					.setText(current_desc);

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
				updateTag();
				dismiss();
			} else if (button_view.getId() == R.id.btn_cancel_marker) {
				dismiss();
			}
		}
	}
}
