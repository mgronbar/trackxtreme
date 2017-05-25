package com.track.trackxtreme;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

public class RaceListener extends AbstractTrackListener {
	/**
	 *
	 */


	private PolylineOptions polylineOptions;
	private TrackPoint first;
	private TrackPoint last;



	public RaceListener(MainActivity mainActivity, Track track) {
		super(mainActivity,track);
		TrackRecord trackRecord = track.getRecords().iterator().next();

		Collection<TrackPoint> points = trackRecord.getPoints();
		if(points.size()>0){
            TrackPoint[] array = points.toArray(new TrackPoint[points.size()]);
            first = array[0];
            last = array[points.size() - 1];
        }


		polylineOptions = new PolylineOptions();

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

					if(addTrackPoint( location, false)) {
                        mainActivity.updateMap(location, map, polylineOptions, Color.RED);
                    }


				}



			});
		}

	}

    @Override
    public void startTracking(Location location) {

        setTrackStatus(TrackStatus.WAITING);
        addTrackPoint(location, false);
        //trackpoints.add(new TrackPoint(trackRecord, location));
    }

    @Override
    public void stopTracking(Location lastLocation) {
        if (getTrackStatus().ordinal() >= TrackStatus.RACING.ordinal()) {

            //setTrackStatus(TrackStatus.FINISH);
            trackpoints.add(new TrackPoint(trackRecord, lastLocation));

            //stopRacing(racelistener.getTrackRecord());
            try {
                mainActivity.getTrackXtremeOpenHelper().saveNewTrackRecord(this);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        setTrackStatus(TrackStatus.FINISH);
        mainActivity.stopTracking();


    }

    @Override
	public boolean addTrackPoint(Location location, boolean force) {
        float accuracy = location.getAccuracy();
        if(accuracy>25 && !force){
            return false;
        }
        updateDashboard(location);

		if (getTrackStatus() == TrackStatus.WAITING) {
            if (first.getLocation().distanceTo(location) < 40) {
                setTrackStatus(TrackStatus.RACING);
            } else if (last.getLocation().distanceTo(location) < 40) {
                setTrackStatus(TrackStatus.RACING_REVERSE);
            }else{
                return false;
            }
            trackpoints.add(new TrackPoint(trackRecord, location));
            return false;

        } else {


            if (isFinished(location, TrackStatus.RACING, last)
                    || isFinished(location, TrackStatus.RACING_REVERSE, first) ) {
                //setTrackStatus(TrackStatus.FINISH);
                //mainActivity.stopRacing();
                stopTracking(location);

            }else{
                trackpoints.add(new TrackPoint(trackRecord, location));

            }

        }
        return true;

	}



    private boolean isFinished(final Location location, TrackStatus status, TrackPoint endPoint) {

		return getTrackStatus() == status && endPoint.getLocation().distanceTo(location) < 40;
	}



    @Override
    public int getName() {

        return R.string.button_race;
    }
}
