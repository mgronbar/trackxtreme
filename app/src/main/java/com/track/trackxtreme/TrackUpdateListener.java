package com.track.trackxtreme;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.jar.Manifest;

//import com.google.android.gms.location.LocationListener;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapFragment;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.track.trackxtreme.data.track.Track;
import com.track.trackxtreme.data.track.TrackPoint;
import com.track.trackxtreme.data.track.TrackRecord;

public class TrackUpdateListener implements com.google.android.gms.location.LocationListener {
	/**
		 * 
		 */
	private final MainActivity mainActivity;
	private List<Location> locations;
	private TrackRecord trackrecord;
	private ArrayList<TrackPoint> trackpoints;
	private PolylineOptions polylineOptions;


	public TrackUpdateListener(MainActivity mainActivity, final Track track) {
		this.mainActivity = mainActivity;
		trackrecord=new TrackRecord(track);
		trackpoints=new ArrayList<TrackPoint>();
		polylineOptions=new PolylineOptions();
		
		
		locations = new ArrayList<Location>();
		final MapFragment mapFrag = (MapFragment) this.mainActivity
				.getFragmentManager().findFragmentById(R.id.map);


        mapFrag.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
//                googleMap.addPolyline(polylineOptions);
                googleMap.clear();
                TrackUpdateListener.this.mainActivity.updateMap(track.getStartLocation(), googleMap,polylineOptions,Color.RED);
            }
        });
//		mapFrag.getMap().addPolyline(polylineOptions);
//		mapFrag.getMap().clear();

	}


    @Override
	public void onLocationChanged(final Location location) {
		MapFragment mapFrag = (MapFragment) this.mainActivity
				.getFragmentManager().findFragmentById(R.id.map);
		if (mapFrag != null) {
			mapFrag.getMapAsync(new OnMapReadyCallback() {
				@Override
				public void onMapReady(GoogleMap map) {

                    mainActivity.setLocationEnabled(map);

					map.clear();
					mainActivity.updateMap(location, map,polylineOptions,Color.RED);
					
					trackpoints.add(new TrackPoint(trackrecord, location));
					
					trackrecord.getTrack().updateBounds(location);
//					locations.add(location);
				}

				
			});
		}

	}



    private void drawtrack(Track track) {
		final PolylineOptions polyline = new PolylineOptions();
		Collection<TrackPoint> points = track.getRecords().iterator().next().getPoints();
		for (TrackPoint trackPoint : points) {
			Location location = trackPoint.getLocation();
			polyline.add(new LatLng(location.getLatitude(), location.getLongitude()));
			
		}
		
		MapFragment mapFrag = (MapFragment) this.mainActivity
				.getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.addPolyline(polyline);
            }
        });


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

	public List<Location> getPoints() {
		return locations;
		
	}

	public Track getTrack() {
		return trackrecord.getTrack();
	}

	public TrackRecord getTrackRecord() {
		return trackrecord;
	}
	
	public ArrayList<TrackPoint> getTrackpoints() {
		return trackpoints;
	}
}
