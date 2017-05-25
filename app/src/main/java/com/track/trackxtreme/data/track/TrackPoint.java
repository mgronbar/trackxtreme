package com.track.trackxtreme.data.track;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.location.Location;

@DatabaseTable(tableName = "trackpoint")
public class TrackPoint {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "trackrecordid")
	private TrackRecord trackrecord;

	@DatabaseField
	private double latitude;

	@DatabaseField
	private double longitude;

	@DatabaseField
	private Long timestamp;

	@DatabaseField
	private float speed;

	@DatabaseField
	private float accuracy;

	@DatabaseField
	private double altitude;

	public TrackPoint() {
	}

	public TrackPoint(TrackRecord trackrecord, Location location) {
		this.trackrecord = trackrecord;
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.timestamp = System.currentTimeMillis();
		this.speed = location.getSpeed();
		this.accuracy = location.getAccuracy();
		this.altitude = location.getAltitude();
		System.out.println(location);
	}

	public Location getLocation() {
		Location loc = new Location("SQLITE");
		loc.setLatitude(latitude);
		loc.setLongitude(longitude);
		loc.setAltitude(altitude);
		loc.setTime(timestamp);
		loc.setAccuracy(accuracy);
		loc.setSpeed(speed);

		return loc;
	}

	public float getAccuracy() {
		return accuracy;
	}
}
