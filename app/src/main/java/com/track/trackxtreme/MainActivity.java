package com.track.trackxtreme;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.track.trackxtreme.data.TrackStatus;
import com.track.trackxtreme.data.TrackXtremeOpenHelper;
import com.track.trackxtreme.data.track.Track;
import com.track.trackxtreme.data.track.TrackPoint;
import com.track.trackxtreme.iu.RecordActivity;
import com.track.trackxtreme.iu.UiTools;
import com.track.trackxtreme.rest.data.Trackretriever;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    public static final int LOCATION_REQUEST_CODE = 123;
    private MapUpdateListener listener;
    private TrackUpdater maplistener;
    //private GoogleApiClient mGoogleApiClient;
    private TrackXtremeOpenHelper trackXtremeOpenHelper;
    //private PolylineOptions polylineOptions;
    //private TrackUpdater racelistener;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = new Timer();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }

        if (listener == null) {
            listener = new MapUpdateListener(this);

            //mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addApi(LocationServices.API)
            //        .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
            maplistener = new TrackUpdateListener(this, new Track());

            trackXtremeOpenHelper = new TrackXtremeOpenHelper(getApplicationContext());
            //polylineOptions = new PolylineOptions();
            //
            final MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(new OnMapReadyCallback() {

                @Override
                public void onMapReady(final GoogleMap map) {
                    listener.setLocationEnabled(map);
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    map.animateCamera(CameraUpdateFactory.zoomTo(18f));


                }
            });
        }
    }

    public void addDashboardListener(){
        listener.requestLocation(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                TextView speed = (TextView) findViewById(R.id.dashboard_speed);
                speed.setText(location.getSpeed()+"km/h");

                TextView lat = (TextView) findViewById(R.id.dashboard_lat);
                lat.setText(""+location.getLatitude()+", "+location.getLongitude());



            }
        }, 15);
    }
    // registerForContextMenu(findViewById(R.id.buttons));


    public TrackXtremeOpenHelper getTrackXtremeOpenHelper() {
        return trackXtremeOpenHelper;
    }

    @Override
    protected void onStart() {
        super.onStart();


    }


    public void clearButton(View view) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.track_buttons);

        MapFragment mapFrag = (MapFragment) this.getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.clear();
            }
        });
        Button b = (Button) findViewById(R.id.start);
        b.setText(R.string.button_start);
        removeButtons(layout);
        maplistener = new TrackUpdateListener(this, new Track());
        // getTrackXtremeOpenHelper().dropall();
    }



    public void startButton(View view) {

        //maplistener=getMapListener();
        //polylineOptions = new PolylineOptions();
        //Button button = (Button) findViewById(R.id.start);
        if(maplistener.getTrackStatus().ordinal()<=TrackStatus.FINISH.ordinal()){
            startTracking();
        }else{
            stopTracking();
        }

        showDashBoard();

    }



    private TrackUpdater getMapListener() {
        return maplistener;
    }

    public void webButton(View view) {
        new Trackretriever(this).execute("test");

    }

    public void deleteButton(View view) {
        trackXtremeOpenHelper.dropall();
    }

    public void searchButton(View view){
        searchButton();
    }

    public void searchButton() {
        MapFragment mapFrag = (MapFragment) this.getFragmentManager().findFragmentById(R.id.map);
        if (mapFrag != null) {
            mapFrag.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    LatLngBounds latLngBounds = map.getProjection().getVisibleRegion().latLngBounds;
                    List<Track> searchTracks = trackXtremeOpenHelper.searchTracks(latLngBounds);
                    map.clear();
                    List<Integer> colorlist = Arrays.asList(Color.BLUE, Color.RED, Color.CYAN, Color.GREEN,
                            Color.MAGENTA);
                    RadioGroup group = (RadioGroup) findViewById(R.id.radio_buttons);
                    LinearLayout layout = (LinearLayout) findViewById(R.id.track_buttons);
                    removeButtons(layout);

                    for (int i = 0; i < searchTracks.size() && i < colorlist.size(); i++) {
                        System.out.println("test" + i);
                        Track track = searchTracks.get(i);
                        int color = colorlist.get(i % colorlist.size());

                        addButton(track, color, group);
                        drawTrack(map, color, track);
                    }
                    if (searchTracks.size() > 0) {
                        Button button = new Button(getApplicationContext());
                        button.setText("recs");
                        layout.addView(button, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {

                                if (maplistener != null) {
                                    openList(maplistener);
                                }

                            }

                        });
                    }

                }

            });
        }

    }

    private void removeButtons(LinearLayout layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {

            View v = layout.getChildAt(i);
            if (v instanceof Button) {
                layout.removeViewAt(i);
            }
            if (v instanceof RadioGroup) {
                ((RadioGroup) v).removeAllViews();
            }
        }
    }

    private void openList(TrackUpdater rListener) {
        Intent intent = new Intent(this, RecordActivity.class);
        intent.putExtra("TRACK_ID", rListener.getTrack().getId());
        startActivity(intent);

    }

    private void drawTrack(GoogleMap map, int color, Track track) {
        if (track.getRecords().iterator().hasNext()) {
            Collection<TrackPoint> points = track.getRecords().iterator().next().getPoints();
            PolylineOptions trackpolyOptions = new PolylineOptions();

            for (TrackPoint trackPoint : points) {
                updateMap(trackPoint.getLocation(), map, trackpolyOptions, color);
            }
        }
    }

    public void updateMap(final Location location, GoogleMap map, PolylineOptions polylines, int color) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        polylines.add(latLng).color(color).width(10);

        map.addPolyline(polylines);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, map.getCameraPosition().zoom));
    }

    public void stopTracking() {
        Location lastLocation = listener.getLastLocation();
        listener.stopMapUpdates(maplistener);
        if(maplistener.getTrackStatus()!=TrackStatus.FINISH)
            maplistener.stopTracking(lastLocation);

        enableButtons(true);

    }
    public void startTracking() {
        Location lastLocation = listener.getLastLocation();
        listener.requestLocation(maplistener,1);
        maplistener.startTracking(lastLocation);

        enableButtons(false);

    }


    private void enableButtons(boolean enabled) {

        List<Integer> list = Arrays.asList(R.id.newmarker_clear, R.id.search);
        for (Integer i:list
             ) {
            Button button = (Button) findViewById(i);
            button.setEnabled(enabled);
        }
        Button startButton = (Button) findViewById(R.id.start);
        if(enabled){
            startButton.setText(maplistener.getName());
        }else{
            startButton.setText(R.string.button_stop);
        }

    }

    private void showDashBoard(){
        findViewById(R.id.dashboard_track).setVisibility(View.VISIBLE);
    }


    private LocationRequest getLocationRequest(long interval) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval * 1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(20f);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }


    private void addButton(final Track track, final int color, RadioGroup group) {
        final RadioButton button = new RadioButton(getApplicationContext());
        button.setText(R.string.button_track);
        button.setTextColor(color);
        if(track.getRecords().iterator().hasNext()) {
            group.addView(button, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

                    Button startButton = (Button) findViewById(R.id.start);
                    startButton.setText(R.string.button_race);

                    maplistener = new RaceListener(MainActivity.this, track);
                    mapFrag.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            googleMap.clear();
                            MainActivity.this.drawTrack(googleMap, color, track);
                        }
                    });

                    button.setPressed(true);

                }

            });
        }


    }

    public void newTracks(List<Track> result) {
        Toast.makeText(getApplicationContext(), "new tracks", Toast.LENGTH_LONG);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted

                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LinearLayout layout = (LinearLayout) findViewById(R.id.track_buttons);
        RadioGroup group = (RadioGroup) findViewById(R.id.radio_buttons);

        if(layout.getChildCount()>0){
            searchButton();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setDashboardDistance(double distance) {
        TextView distanceView =(TextView)findViewById(R.id.track_distance);
        distanceView.setText(UiTools.getDistance(distance));
    }
    public void setDashboardTime(long time) {
        TextView timeView =(TextView)findViewById(R.id.track_time);

        timeView.setText(UiTools.getTime(time));
        if(time!=0) {
            double dist = maplistener.getDistance();
            String kmh =UiTools.getAvg(dist,time);

            TextView avg = (TextView) findViewById(R.id.track_avg_speed);
            avg.setText(kmh);
        }

    }


    public void startTimer() {
        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                mHandler.obtainMessage(1).sendToTarget();
            }
        }, 0, 1000);
    }

    public void stopTimer(){
        timer.purge();
        timer.cancel();
    }


    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            setDashboardTime(maplistener.timeElapsed());

        }
    };
}
