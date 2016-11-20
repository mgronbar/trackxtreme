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

import android.graphics.Color;
import android.location.Location;

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
					map.setMyLocationEnabled(true);

					if (trackinStatus.ordinal() < TrackStatus.TRACKING.ordinal()) {
						if (first.getLocation().distanceTo(location) < 40) {
							trackinStatus = TrackStatus.TRACKING;
						} else if (last.getLocation().distanceTo(location) < 40) {
							trackinStatus = TrackStatus.TRACKING_REVERSE;
						}
					} else {
						mainActivity.updateMap(location, map, polylineOptions, Color.RED);
						trackpoints.add(new TrackPoint(raceRecord, location));
						if (isFinished(location, TrackStatus.TRACKING, last)
								|| isFinished(location, TrackStatus.TRACKING_REVERSE, first)) {
							trackinStatus = TrackStatus.FINISH;
							mainActivity.stopRacing(raceRecord);
						}

					}

				}

				private boolean isFinished(final Location location, TrackStatus status, TrackPoint endPoint) {

					return trackinStatus == status && endPoint.getLocation().distanceTo(location) < 40;
				}

			});
		}

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
