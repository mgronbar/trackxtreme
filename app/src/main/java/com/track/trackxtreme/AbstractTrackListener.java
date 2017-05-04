package com.track.trackxtreme;

import android.location.Location;

import com.track.trackxtreme.data.TrackStatus;
import com.track.trackxtreme.data.track.Track;
import com.track.trackxtreme.data.track.TrackPoint;
import com.track.trackxtreme.data.track.TrackRecord;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by marko on 30/04/2017.
 */
public abstract class AbstractTrackListener implements TrackUpdater {


    protected MainActivity mainActivity;
    private Location previousLocation;
    private double distance = 0;
    protected ArrayList<TrackPoint> trackpoints;
    protected TrackStatus trackingStatus = TrackStatus.NONE;
    protected TrackRecord trackRecord;

    public AbstractTrackListener(MainActivity mainActivity,Track track) {

        this.mainActivity = mainActivity;
        trackpoints = new ArrayList<TrackPoint>();
        trackRecord=new TrackRecord(track);
    }

    public void setPreviousLocation(Location previousLocation) {
        this.previousLocation = previousLocation;
    }

    public Location getPreviousLocation() {
        return previousLocation;
    }

    public void updateDashboard(Location location) {
        if (previousLocation != null) {
            distance += ((int) (previousLocation.distanceTo(location) * 100)) / 100;
        }

        mainActivity.setDashboardDistance(distance);
        setPreviousLocation(location);
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public long timeElapsed() {
        long time=0;
        long timeInMillis =Calendar.getInstance().getTimeInMillis();
        if(trackpoints.size()>0){
            TrackPoint trackPoint = trackpoints.get(0);
            time= timeInMillis-trackPoint.getLocation().getTime();
        }
        return time;
    }

    @Override
    public TrackStatus getTrackStatus() {
        return trackingStatus;
    }

    @Override
    public void setTrackStatus(TrackStatus status) {
        trackingStatus=status;
        if(trackingStatus.ordinal()>TrackStatus.WAITING.ordinal()){
            distance=0;
            mainActivity.startTimer();
        }else{
            mainActivity.stopTimer();
        }
    }

    @Override
    public TrackRecord getTrackRecord() {
        return trackRecord;
    }

    @Override
    public Track getTrack() {
        return trackRecord.getTrack();
    }

    @Override
    public ArrayList<TrackPoint> getTrackpoints() {
        return trackpoints;
    }

    public void setTrack(Track track) {
        trackpoints = new ArrayList<TrackPoint>();
        trackRecord=new TrackRecord(track);
        setTrackStatus(TrackStatus.NONE);
    }
}
