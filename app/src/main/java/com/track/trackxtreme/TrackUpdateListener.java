package com.track.trackxtreme;

import android.app.AlertDialog;
import android.graphics.Color;
import android.location.Location;

import java.sql.SQLException;
import java.util.Collection;

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
import com.track.trackxtreme.data.TrackStatus;
import com.track.trackxtreme.data.track.Track;
import com.track.trackxtreme.data.track.TrackPoint;

public class TrackUpdateListener extends AbstractTrackListener  {
    /**
     *
     */

    //private List<Location> locations;


    private PolylineOptions polylineOptions;


    private TrackStatus trackStatus = TrackStatus.NONE;


    public TrackUpdateListener(MainActivity mainActivity, final Track track) {
        super(mainActivity, track);


        polylineOptions = new PolylineOptions();


    }


    @Override
    public void onLocationChanged(final Location location) {
        MapFragment mapFrag = (MapFragment) this.mainActivity
                .getFragmentManager().findFragmentById(R.id.map);
        if (mapFrag != null) {
            mapFrag.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {

                    map.clear();
                    mainActivity.updateMap(location, map, polylineOptions, Color.RED);

                    addTrackPoint(location, false);
                }


            });
        }

    }


    @Override
    public void startTracking(Location location ) {
        setTrack(new Track());
        addTrackPoint(location, false);
        getTrack().setStartLocation(location);
        final MapFragment mapFrag = (MapFragment) this.mainActivity
                .getFragmentManager().findFragmentById(R.id.map);


        mapFrag.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.clear();
                TrackUpdateListener.this.mainActivity.updateMap(getTrack().getStartLocation(), googleMap, polylineOptions, Color.RED);
            }
        });

        try {
            mainActivity.getTrackXtremeOpenHelper().getTrackDao().create(trackRecord.getTrack());
            mainActivity.getTrackXtremeOpenHelper().getTrackRecordDao().create(trackRecord);
        } catch (java.sql.SQLException e) {

        }
        setTrackStatus(TrackStatus.TRACKING);

    }

    @Override
    public void stopTracking(Location lastLocation) {
        addTrackPoint(lastLocation, false);
        setTrackStatus(TrackStatus.FINISH);
        try {
            mainActivity.getTrackXtremeOpenHelper().saveNewTrack(this);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    }

    @Override
    public boolean addTrackPoint(Location location, boolean force) {


        trackpoints.add(new TrackPoint(trackRecord, location));

        trackRecord.getTrack().updateBounds(location);
        int size = trackpoints.size();

        super.updateDashboard(location);
        return true;

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



    @Override
    public int getName() {
        return R.string.button_track;
    }



}
