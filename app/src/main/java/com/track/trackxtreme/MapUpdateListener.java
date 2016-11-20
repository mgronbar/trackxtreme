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

public class MapUpdateListener implements LocationListener {
	/**
		 * 
		 */
	private final MainActivity mainActivity;
	/**
	 * @param mainActivity
	 */
	MapUpdateListener(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public void onLocationChanged(final Location location) {
		MapFragment mapFrag = (MapFragment) this.mainActivity
				.getFragmentManager().findFragmentById(R.id.map);
		if (mapFrag != null) {
			mapFrag.getMapAsync(new OnMapReadyCallback() {
				@Override
				public void onMapReady(GoogleMap map) {
                    MapUpdateListener.this.mainActivity.setLocationEnabled(map);
					LatLng latLng = new LatLng(location.getLatitude(), location
							.getLongitude());
					
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,
							map.getCameraPosition().zoom));
				}
			});
			String message = "Location:" + location.getLatitude() + ","
					+ location.getLongitude();
//			showAlert(message);
		}

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
