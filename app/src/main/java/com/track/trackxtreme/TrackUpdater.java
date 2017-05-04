package com.track.trackxtreme;

import android.location.Location;

import com.google.android.gms.location.LocationListener;
import com.track.trackxtreme.data.TrackStatus;
import com.track.trackxtreme.data.track.Track;
import com.track.trackxtreme.data.track.TrackPoint;
import com.track.trackxtreme.data.track.TrackRecord;

import java.util.ArrayList;

/**
 * Created by marko on 29/04/2017.
 */

public interface TrackUpdater extends LocationListener{


    void startTracking(Location location);
    
    void stopTracking(Location lastLocation);


    boolean addTrackPoint(Location lastLocation, boolean force);

    TrackStatus getTrackStatus();
    void setTrackStatus(TrackStatus finish);

    Track getTrack();

    TrackRecord getTrackRecord();

    ArrayList<TrackPoint> getTrackpoints();

    int getName();

    long timeElapsed();

    double getDistance();
}
