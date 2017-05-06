package com.track.trackxtreme.data.track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import android.location.Location;

@DatabaseTable(tableName = "trackrecord")
public class TrackRecord {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "trackid")
	private Track track;

	@ForeignCollectionField
	private ForeignCollection<TrackPoint> points;

	@DatabaseField
	private Long starttime;

	@DatabaseField
	private Long time;

	@DatabaseField
	private float maxspeed;

	@DatabaseField
	private float distance;

	@DatabaseField
	private int type;

	@DatabaseField
	private int status;

	@DatabaseField
	private long user;

	public TrackRecord() {
	}

	public TrackRecord(Track track) {
		this.track = track;
	}

	public Collection<TrackPoint> getPoints() {
		return points;

	}

	public Track getTrack() {
		return track;
	}

	@Override
	public String toString() {
		try {
			String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time),
					TimeUnit.MILLISECONDS.toMinutes(time) % TimeUnit.HOURS.toMinutes(1),
					TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1));
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// dd/MM/yyyy
			Date start = new Date(starttime);
			String strDate = sdfDate.format(start);
			return hms + " - " + strDate;
		}catch (Exception e){
			return "N/A";
		}
	}

	public void updateData(Collection<TrackPoint> trackpoints, boolean updatetrackdistance) {
		if(trackpoints.size()>2) {
            Iterator<TrackPoint> iterator = trackpoints.iterator();
            TrackPoint first = iterator.next();
			starttime = first.getLocation().getTime();
			System.out.println(starttime);
			time = 0L;
			System.out.println("size:" + trackpoints.size());

			distance = 0.0f;
			maxspeed = first.getLocation().getSpeed();
            TrackPoint prev = first;
			while (iterator.hasNext()) {
                TrackPoint next = iterator.next();
                Location location = next.getLocation();
				distance += prev.getLocation().distanceTo(location);

				if (maxspeed < location.getSpeed()) {
					maxspeed = location.getSpeed();
				}
                prev=next;
			}
            time = prev.getLocation().getTime() - starttime;
			if (updatetrackdistance) {
				track.setDistance((int) distance);
			}

			track.updateRecordTime(time);
		}

	}

	public void roundTrim(){

		Iterator<TrackPoint> iterator = points.iterator();
		TrackPoint prev =iterator.next();
		TrackPoint start = prev;
		float d=0;
		boolean done=false;
		while (iterator.hasNext()) {
			TrackPoint tp = iterator.next();
			Location location = tp.getLocation();
			d += prev.getLocation().distanceTo(location);

			if(d>200 && location.distanceTo(start.getLocation())<50){
				System.out.println("ROUND .......");
				done=true;
			}
			if(done){
				iterator.remove();
			}
			prev=tp;
		}
        updateData(points,true);


	}

	public float getDistance() {
		return distance;
	}

    public Long getTime() {
        return time;
    }

    public Long getStarttime() {
        return starttime;
    }

    public float getMaxspeed() {
        return maxspeed;
    }
}
