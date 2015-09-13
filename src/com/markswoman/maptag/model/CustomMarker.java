package com.markswoman.maptag.model;

import com.google.android.gms.maps.model.Marker;

/**
 * Class which is corresponding to the Custom Markers on the map
 * 
 * @author ChengMin - Hong
 * 
 */
public class CustomMarker {
	private String ID;
	private String marker_id;
	private String name;
	private String description;
	private double latitude;
	private double longitude;
	private Marker delegate;

	public String getID() {
		return this.ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

	public String getMarkerID() {
		return this.marker_id;
	}

	public void setMarkerID(String marker_id) {
		this.marker_id = marker_id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return this.longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Marker getDelegate() {
		return this.delegate;
	}

	public void setDelegate(Marker delegate) {
		this.delegate = delegate;
	}
}
