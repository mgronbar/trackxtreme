package com.track.trackxtreme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.PolylineOptions;
import com.track.trackxtreme.data.TrackStatus;
import com.track.trackxtreme.data.track.Track;
import com.track.trackxtreme.data.track.TrackPoint;
import com.track.trackxtreme.data.track.TrackRecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

public class RaceListener implements LocationListener {
	/**
	 *
	 */
	private final MainActivity mainActivity;
	private List<Location> locations;
	private TrackRecord trackrecord;
	private ArrayList<TrackPoint> trackpoints;
	private PolylineOptions polylineOptions;
	private TrackPoint first;
	private TrackPoint last;
	private TrackRecord raceRecord;
	protected TrackStatus trackinStatus = TrackStatus.NONE;

	public RaceListener(MainActivity mainActivity, Track track) {
		this.mainActivity = mainActivity;
		TrackRecord trackRecord = track.getRecords().iterator().next();
		Collection<TrackPoint> points = trackRecord.getPoints();
		TrackPoint[] array = points.toArray(new TrackPoint[points.size()]);
		first = array[0];
		last = array[points.size() - 1];

		raceRecord = new TrackRecord(track);
		trackpoints = new ArrayList<TrackPoint>();
		polylineOptions = new PolylineOptions();

		locations = new ArrayList<Location>();
		MapFragment mapFrag = (MapFragment) this.mainActivity.getFragmentManager().findFragmentById(R.id.map);

	}

	@Override
	public void onLocationChanged(final Location location) {
		MapFragment mapFrag = (MapFragment) this.mainActivity.getFragmentManager().findFragmentById(R.id.map);
		if (mapFrag != null) {
			mapFrag.getMapAsync(new OnMapReadyCallback() {

				@Override
				public void onMapReady(GoogleMap map) {
					if (ActivityCompat.checkSelfPermission(RaceListener.this.mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RaceListener.this.mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						// TODO: Consider calling
						//    ActivityCompat#requestPermissions
						// here to request the missing permissions, and then overriding
						//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
						//                                          int[] grantResults)
						// to handle the case where the user grants the permission. See the documentation
						// for ActivityCompat#requestPermissions for more details.
						return;
					}
					map.setMyLocationEnabled(true);

					addRacePoint( location);
					mainActivity.updateMap(location, map, polylineOptions, Color.RED);

				}



			});
		}

	}
	protected void addLastRacePoint(Location location){
		trackpoints.add(new TrackPoint(raceRecord, location));
		trackinStatus = TrackStatus.FINISH;
	}

	protected void addRacePoint(Location location) {
		if (trackinStatus.ordinal() < TrackStatus.FINISH.ordinal()) {
            if (first.getLocation().distanceTo(location) < 40) {
                trackinStatus = TrackStatus.TRACKING;
            } else if (last.getLocation().distanceTo(location) < 40) {
                trackinStatus = TrackStatus.TRACKING_REVERSE;
            }
        } else {


            if (isFinished(location, TrackStatus.TRACKING, last)
                    || isFinished(location, TrackStatus.TRACKING_REVERSE, first) || trackinStatus == TrackStatus.FINISH) {
                trackinStatus = TrackStatus.FINISH;
                mainActivity.stopRacing(raceRecord);
				return;
            }

        }
		trackpoints.add(new TrackPoint(raceRecord, location));
	}

	private boolean isFinished(final Location location, TrackStatus status, TrackPoint endPoint) {

		return trackinStatus == status && endPoint.getLocation().distanceTo(location) < 40;
	}

	public List<Location> getPoints() {
		return locations;

	}

	public Track getTrack() {
		return raceRecord.getTrack();
	}

	public TrackRecord getTrackRecord() {
		return raceRecord;
	}

	public ArrayList<TrackPoint> getTrackpoints() {
		return trackpoints;
	}
}
