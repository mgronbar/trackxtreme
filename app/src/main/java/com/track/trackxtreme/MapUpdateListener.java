package com.track.trackxtreme;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapUpdateListener implements LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    private GoogleApiClient mGoogleApiClient;
    /**
     *
     */
    private final MainActivity mainActivity;

    /**
     * @param mainActivity
     */
    MapUpdateListener(MainActivity mainActivity) {
        this.mainActivity = mainActivity;


        mGoogleApiClient = new GoogleApiClient.Builder(mainActivity.getApplicationContext()).addApi(LocationServices.API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(final Location location) {
        MapFragment mapFrag = (MapFragment) this.mainActivity
                .getFragmentManager().findFragmentById(R.id.map);
        if (mapFrag != null) {
            mapFrag.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    MapUpdateListener.this.setLocationEnabled(map);
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


    public Location getLastLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission

            if (ActivityCompat.checkSelfPermission(mainActivity.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            throw new RuntimeException("no location service available");
        }else {
            return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    public void getMainActivity() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        System.out.println("MainActivity.onConnectionFailed()");
    }

    public void setLocationEnabled(GoogleMap map) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission

            if (ActivityCompat.checkSelfPermission(mainActivity.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            }
        } else {
            //Not in api-23, no need to prompt
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        System.out.println("MainActivity.onConnected()");
        requestLocation(this, 10);
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub
        System.out.println("MainActivity.onConnectionSuspended()");

    }

    public void requestLocation(LocationListener listener, long interval) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission

            if (ActivityCompat.checkSelfPermission(mainActivity.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, getLocationRequest(interval), listener);
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, getLocationRequest(interval), listener);
        }
    }

    private LocationRequest getLocationRequest(long interval) {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval * 1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(20f);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    public void stopMapUpdates(LocationListener maplistener) {

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, maplistener);

    }

    public void startMapUpdates(LocationListener maplistener) {
        requestLocation(maplistener,10);
    }
}
