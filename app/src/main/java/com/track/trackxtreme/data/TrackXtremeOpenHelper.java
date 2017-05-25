package com.track.trackxtreme.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.android.gms.maps.model.LatLngBounds;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.ColumnArg;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
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
import android.util.Log;
import android.view.MenuItem;

public class TrackXtremeOpenHelper extends OrmLiteSqliteOpenHelper {

    private static final int DB_VERSION = 3;
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
        Log.d("Database upgrade", "old:" + oldVer + " new :" + newVer);
        if (newVer == 3) {
            try {
                Dao<Track, Integer> dao = getTrackDao();
                dao.executeRaw("ALTER TABLE `track` ADD COLUMN round BOOLEAN;");
                dao.executeRaw("ALTER TABLE `track` ADD COLUMN start_id INTEGER;");
                dao.executeRaw("ALTER TABLE `track` ADD COLUMN end_id INTEGER;");

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

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



    public long createNewTrackPoint(long trackid, long trackrecordid, Location location, boolean round) throws SQLException {
        //Track track = getTrackDao().queryForId((int) trackid);
        TrackRecord trackRecord = getTrackRecordDao().queryForId((int) trackrecordid);
        TrackPoint tp = new TrackPoint(trackRecord, location);
        return getTrackPointDao().create(tp);


    }

    public List<Track> searchTracks(LatLngBounds latLngBounds) {
        System.out.println("searching----------------------------------" + latLngBounds);

        try {
            // List<Track> list =getTrackDao().queryForAll();
            List<Track> list = getTrackDao().queryBuilder().where()
                    .gt("minLat", latLngBounds.southwest.latitude).and()
                    .lt("maxLat", latLngBounds.northeast.latitude).and()
                    .gt("minLon", latLngBounds.southwest.longitude).and()
                    .lt("maxLon", latLngBounds.northeast.longitude).query();
            return list;

        } catch (SQLException e) {
            return Collections.emptyList();
        }

    }

    public Collection<Track> searchTracksStartEnd(LatLngBounds latLngBounds) {
        try {
            // List<Track> list =getTrackDao().queryForAll();

            Set<Track> set = new HashSet<>();
            QueryBuilder<TrackPoint, Integer> trackPointQueryBuilder = getTrackPointDao().queryBuilder();
            trackPointQueryBuilder.where()
                    .gt("latitude", latLngBounds.southwest.latitude).and()
                    .lt("latitude", latLngBounds.northeast.latitude).and()
                    .gt("longitude", latLngBounds.southwest.longitude).and()
                    .lt("longitude", latLngBounds.northeast.longitude);
            set.addAll(getTrackDao().queryBuilder()
                    .join("start_id", "id", trackPointQueryBuilder)
                    .query());
            set.addAll(getTrackDao().queryBuilder()
                    .join("end_id", "id", trackPointQueryBuilder)
                    .query());
            return set;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Track> searchTracksPartially(LatLngBounds latLngBounds) {
        try {

            Where<Track, Integer> where = getTrackDao().queryBuilder().where();

            List<Track> list = where
                    .or(
                            where.and(
                                    where.or(
                                            where.and(where.gt("minLat", latLngBounds.southwest.latitude),
                                                    where.lt("minLat", latLngBounds.northeast.latitude)),
                                            where.and(where.gt("maxLat", latLngBounds.southwest.latitude),
                                                    where.lt("maxLat", latLngBounds.northeast.latitude))),

                                    where.or(
                                            where.and(where.gt("minLon", latLngBounds.southwest.longitude),
                                                    where.lt("minLon", latLngBounds.northeast.longitude)),
                                            where.and(where.gt("maxLon", latLngBounds.southwest.longitude),
                                                    where.lt("maxLon", latLngBounds.northeast.longitude))
                                    )
                            ),
                            where.and(
                                    where.lt("minLat", latLngBounds.southwest.latitude),
                                    where.gt("maxLat", latLngBounds.northeast.latitude),
                                    where.lt("minLon", latLngBounds.southwest.longitude),
                                    where.gt("maxLon", latLngBounds.northeast.longitude)
                            )

                    ).query();
            return list;

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
        if (trackpoints.size() > 0) {
            TrackPoint start = trackpoints.get(0);
            TrackPoint end = trackpoints.get(trackpoints.size() - 1);
            track.setStart(start);
            track.setEnd(end);
        }

        //trackRecord.setTrackPoints(trackpoints);


        trackRecord.updateData(trackpoints,true);
        track.setDistance((int) trackRecord.getDistance());
        getTrackDao().create(track);
        getTrackRecordDao().create(trackRecord);
        getTrackPointDao().create(trackpoints);








    }

    public void saveNewTrackRecord(TrackUpdater racelistener) throws SQLException {
        Track track = racelistener.getTrack();
        TrackRecord trackRecord = racelistener.getTrackRecord();
        ArrayList<TrackPoint> trackpoints = racelistener.getTrackpoints();
        trackRecord.setTrackPoints(trackpoints);

        trackRecord.updateData(trackpoints,false);

        getTrackDao().update(track);
        getTrackRecordDao().create(trackRecord);
        getTrackPointDao().create(trackpoints);

    }

    public int deleteTrack(TrackRecord item1) throws SQLException {
        Track track = item1.getTrack();
        ForeignCollection<TrackRecord> records = track.getRecords();

        records.getDao().delete(item1);

        if (records.size() == 0) {
            getTrackDao().delete(track);
        }
        return records.size();

    }


    public void update(TrackRecord trackRecord) {
        try {
            getTrackRecordDao().update(trackRecord);
            getTrackDao().update(trackRecord.getTrack());
            //getTrackPointDao().upda(trackRecord.getPoints());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
