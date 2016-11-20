package com.track.trackxtreme;

import android.app.AlertDialog;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class TrackDbUpdateListener implements LocationListener {
	/**
		 * 
		 */
	private final MainActivity mainActivity;
	private long trackid;
	private long trackrecordid;
	/**
	 * @param mainActivity
	 */
	TrackDbUpdateListener(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		
	}
	
	public void setTrackid(long trackid) {
		this.trackid = trackid;
	}
	public void setTrackrecordid(long trackrecordid) {
		this.trackrecordid = trackrecordid;
	}

	@Override
	public void onLocationChanged(final Location location) {
		mainActivity.getTrackXtremeOpenHelper().createNewTrackPoint(trackid, trackrecordid, location, false);

	}

	private void showAlert(String message) {
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);

		// 2. Chain together various setter methods to set the dialog
		// characteristics
		builder.setMessage(message).setTitle("Location");

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
