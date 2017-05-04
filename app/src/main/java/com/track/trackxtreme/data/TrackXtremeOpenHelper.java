package com.track.trackxtreme.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.android.gms.maps.model.LatLngBounds;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.track.trackxtreme.RaceListener;
import com.track.trackxtreme.TrackUpdateListener;
import com.track.trackxtreme.TrackUpdater;
import com.track.trackxtreme.data.track.Track;
import com.track.trackxtreme.data.track.TrackPoint;
import com.track.trackxtreme.data.track.TrackRecord;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.JsonReader;

public class TrackXtremeOpenHelper extends OrmLiteSqliteOpenHelper {

	private static final int DB_VERSION = 2;
	private static final String TRACKXTREME_DB = "trackxtreme";
	private Context context;
	private Dao<Track, Integer> trackDao;
	private Dao<TrackRecord, Integer> trackRecordDao;
	private Dao<TrackPoint, Integer> trackPointDao;

	public TrackXtremeOpenHelper(Context context) {
		super(context, TRACKXTREME_DB, null, DB_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {

		// try {
		//
		// InputStream in =
		// context.getResources().getAssets().open("database/db.json");
		// createDatabases(db, new JsonReader(new InputStreamReader(in)));
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		try {
			TableUtils.createTable(connectionSource, Track.class);
			TableUtils.createTable(connectionSource, TrackRecord.class);
			TableUtils.createTable(connectionSource, TrackPoint.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("TrackXtremeOpenHelper.onCreate()");

		// TODO Auto-generated method stub

	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		System.out.println("TrackXtremeOpenHelper.onOpen()");
		super.onOpen(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVer, int newVer) {
		// TODO Auto-generated method stub

	}

	public void dropall() {
		SQLiteDatabase db = getWritableDatabase();
		InputStream in;
		try {
			in = context.getResources().getAssets().open("database/db.json");
			JsonReader json = new JsonReader(new InputStreamReader(in));
			json.beginObject();
			while (json.hasNext()) {
				String dbName = json.nextName();
				System.out.println(dbName);
				String sql = "DROP TABLE IF EXISTS " + dbName + ";";
				db.execSQL(sql);
				json.beginObject();
				while (json.hasNext()) {
					json.nextName();
					json.nextString();
				}
				json.endObject();
			}
			json.endObject();
			json.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.deleteDatabase(TRACKXTREME_DB);

	}

//	private void createDatabases(SQLiteDatabase db, JsonReader json) throws IOException {
//		json.beginObject();
//		while (json.hasNext()) {
//			String dbName = json.nextName();
//			System.out.println(dbName);
//
//			String createSql = "CREATE TABLE IF NOT EXISTS ";
//			createSql += dbName + "(";
//			List<String[]> columns = readTables(json);
//			for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
//				String[] fields = (String[]) iterator.next();
//
//				createSql += fields[0] + ' ' + fields[1];
//				// createSql+=')'
//				if (iterator.hasNext())
//					createSql += ",";
//			}
//			createSql += " )";
//			System.out.println(createSql);
//			db.execSQL(createSql);
//		}
//		json.endObject();
//	}

//	private List<String[]> readTables(JsonReader json) throws IOException {
//		json.beginObject();
//		List<String[]> columns = new ArrayList<String[]>();
//		while (json.hasNext()) {
//			String[] column = new String[3];
//			column[0] = json.nextName();
//			column[1] = json.nextString();
//
//			columns.add(column);
//		}
//		json.endObject();
//		return columns;
//	}
//
//	private long createNewTrack(String name, TrackType type, String sport, TrackStatus status, double lat, double lon,
//			boolean round) {
//
//		// writableDatabase.execSQL("INSERT INTO track");
//
//		// ContentValues placeValues = new ContentValues();
//		// placeValues.put("lat", lat);
//		// placeValues.put("lon", lon);
//		// placeValues.put("name", "lat:" + lat + " lon:" + lon);
//		// long start = getWritableDatabase().insert("place", null,
//		// placeValues);
//
//		ContentValues values = new ContentValues();
//		values.put("name", name);
//		values.put("type", type.name());
//		values.put("sport", sport);
//		// values.put("status", status.name());
//		values.put("sport", sport);
//		// values.put("start", start);
//		// if (round) {
//		// values.put("end", start);
//		// }
//		return getWritableDatabase().insert("track", null, values);
//
//	}

//	private long createNewTrackRecord(long trackid, TrackStatus status, boolean base) {
//		ContentValues values = new ContentValues();
//		values.put("trackId", trackid);
//		values.put("startTime", System.currentTimeMillis());
//		values.put("status", status.name());
//		values.put("base", base);
//
//		return getWritableDatabase().insert("trackRecord", null, values);
//	}

//	private long createNewTrackPoint(long trackid, long trackrecordid, double lat, double lon, double altitude,
//			double accuracyHor, double accuracyVer, long timestamp, boolean round) {
//		ContentValues values = new ContentValues();
//		values.put("trackrecordid", trackrecordid);
//		values.put("lat", lat);
//		values.put("lon", lon);
//		values.put("altitude", altitude);
//		values.put("accuracyHor", accuracyHor);
//		values.put("accuracyVer", accuracyVer);
//		values.put("timestamp", timestamp);
//		values.put("round", round);
//        //new TrackPoint();
//
//		return getWritableDatabase().insert("trackPoint", null, values);
//	}

	public long createNewTrackPoint(long trackid, long trackrecordid, Location location, boolean round) throws SQLException{
        //Track track = getTrackDao().queryForId((int) trackid);
        TrackRecord trackRecord = getTrackRecordDao().queryForId((int) trackrecordid);
        TrackPoint tp =new TrackPoint(trackRecord, location);
        return getTrackPointDao().create(tp);

		//return createNewTrackPoint(trackid, trackrecordid, location.getLatitude(), location.getLongitude(),
		//		location.getAltitude(), location.getAccuracy(), location.getAccuracy(), location.getTime(), round);

	}

	public List<Track> searchTracks(LatLngBounds latLngBounds) {
		System.out.println("searching----------------------------------" + latLngBounds);

		try {
			// List<Track> list =getTrackDao().queryForAll();
			List<Track> list = getTrackDao().queryBuilder().where().gt("minLat", latLngBounds.southwest.latitude).and()
					.lt("maxLat", latLngBounds.northeast.latitude).and().gt("minLon", latLngBounds.southwest.longitude)
					.and().lt("maxLon", latLngBounds.northeast.longitude).query();
			return list;
			// for (Track track : list) {
			// ForeignCollection<TrackRecord> collection = track.getRecords();
			// for (TrackRecord trackRecord : collection) {
			// System.out.println("Jee" + trackRecord);
			//
			// }
			//
			// }
		} catch (SQLException e) {
			return Collections.emptyList();
		}

	}

	public Dao<Track, Integer> getTrackDao() throws SQLException {
		if (trackDao == null) {
			trackDao = getDao(Track.class);

		}
		return trackDao;

	}

	public Dao<TrackPoint, Integer> getTrackPointDao() throws SQLException {
		if (trackPointDao == null) {
			trackPointDao = getDao(TrackPoint.class);

		}
		return trackPointDao;

	}

	public Dao<TrackRecord, Integer> getTrackRecordDao() throws SQLException {
		if (trackRecordDao == null) {
			trackRecordDao = getDao(TrackRecord.class);

		}
		return trackRecordDao;

	}

	public void saveNewTrack(TrackUpdateListener maplistener) throws SQLException {
		Track track = maplistener.getTrack();
		TrackRecord trackRecord = maplistener.getTrackRecord();
		ArrayList<TrackPoint> trackpoints = maplistener.getTrackpoints();

		trackRecord.updateData(trackpoints, true);
        track.setDistance((int)trackRecord.getDistance());

		getTrackDao().update(track);
		getTrackRecordDao().update(trackRecord);
		getTrackPointDao().create(trackpoints);

	}

	public void saveNewTrackRecord(TrackUpdater racelistener) throws SQLException {
		Track track = racelistener.getTrack();
		TrackRecord trackRecord = racelistener.getTrackRecord();
		ArrayList<TrackPoint> trackpoints = racelistener.getTrackpoints();

		trackRecord.updateData(trackpoints, false);

		getTrackDao().update(track);
		getTrackRecordDao().create(trackRecord);
		getTrackPointDao().create(trackpoints);

	}

	public int deleteTrack(TrackRecord item1) throws SQLException {
		Track track = item1.getTrack();
		ForeignCollection<TrackRecord> records = track.getRecords();

		records.getDao().delete(item1);

		if(records.size()==0){
			getTrackDao().delete(track);
		}
        return records.size();

	}
}
