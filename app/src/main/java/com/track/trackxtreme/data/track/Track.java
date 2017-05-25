package com.track.trackxtreme.data.track;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import android.location.Location;

@DatabaseTable(tableName = "track")
public class Track {
    private Location startLocation;

    @DatabaseField(generatedId = true)
    private int id;

    @ForeignCollectionField
    private ForeignCollection<TrackRecord> trackrecords;
    private Builder builder;

    @DatabaseField
    private String name;

    @DatabaseField
    private double minLat;

    @DatabaseField
    private double minLon;

    @DatabaseField
    private double maxLat;

    @DatabaseField
    private double maxLon;

    @DatabaseField
    private long recordtime;

    @DatabaseField
    private boolean round;


    @DatabaseField
    private long distance;

    @DatabaseField(canBeNull = true, foreign = true)
    private TrackPoint start;

    @DatabaseField(canBeNull = true, foreign = true)
    private TrackPoint end;

    public Track() {
        System.out.println("Track.Track()");
        builder = LatLngBounds.builder();

    }

    public Track(Location location) {
        setStartLocation(location);
    }

    public void setStartLocation(Location location) {

        startLocation = location;
        updateBounds(location);

    }

    public ForeignCollection<TrackRecord> getRecords() {
        return trackrecords;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void updateBounds(Location location) {
        System.out.println("Track.updateBounds()");
        builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
        setMinLat(location.getLatitude());
        setMaxLat(location.getLatitude());

        setMinLon(location.getLongitude());
        setMaxLon(location.getLongitude());
    }

    public double getMinLat() {
        return minLat;
    }

    public void setMinLat(double minLatitude) {
        builder.build().including(new LatLng(minLatitude, getMinLat()));
        minLat = builder.build().southwest.latitude;
    }

    public double getMinLon() {
        return minLon;
    }

    public void setMinLon(double minLongitude) {
        builder.build().including(new LatLng(getMinLat(), minLongitude));
        minLon = builder.build().southwest.longitude;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public void setMaxLat(double maxLatitude) {
        builder.build().including(new LatLng(maxLatitude, getMaxLon()));
        maxLat = builder.build().northeast.latitude;
    }

    public double getMaxLon() {
        return maxLon;
    }

    public void setMaxLon(double maxLongitude) {
        builder.build().including(new LatLng(getMaxLat(), maxLongitude));
        maxLon = builder.build().northeast.longitude;
    }

    public int getId() {
        // TODO Auto-generated method stub
        return id;
    }

    public void updateRecordTime(long newTime) {
        if (recordtime > newTime || recordtime == 0l) {
            recordtime = newTime;
        }
    }

    public void addDistancePoint(TrackPoint p1, TrackPoint p2) {

    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public long getDistance() {
        return distance;
    }

    public void setRound(boolean round) {
        this.round = round;
    }

    public boolean isRound() {
        return round;
    }

    public long getRecordtime() {
        return recordtime;
    }

    public LatLngBounds getBounds() {
        return new LatLngBounds(new LatLng(getMinLat(),getMinLon()),new LatLng(getMaxLat(),getMaxLon()));

    }

    public void setStart(TrackPoint start) {
        this.start = start;
    }

    public void setEnd(TrackPoint end) {
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
