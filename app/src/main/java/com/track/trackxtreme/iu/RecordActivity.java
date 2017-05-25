package com.track.trackxtreme.iu;

import java.sql.SQLException;
import java.util.ArrayList;

import com.track.trackxtreme.R;
import com.track.trackxtreme.data.TrackXtremeOpenHelper;
import com.track.trackxtreme.data.track.Track;
import com.track.trackxtreme.data.track.TrackRecord;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RecordActivity extends Activity {

    public static final int TRACK_DELETED_CODE = 1;
    private ListView listView;
    private TrackXtremeOpenHelper trackXtremeOpenHelper;
    private ArrayList<TrackRecord> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        trackXtremeOpenHelper = new TrackXtremeOpenHelper(getApplicationContext());
        int object = getIntent().getExtras().getInt("TRACK_ID");
        try {

            setContentView(R.layout.activity_record);

            Track track = trackXtremeOpenHelper.getTrackDao().queryForId(object);
            int size = track.getRecords().size();
            TextView countView = (TextView) findViewById(R.id.track_count);

            countView.setText("Count: "+size);
            if(track.getRecords().iterator().hasNext()) {
                long distance = (long) track.getRecords().iterator().next().getDistance();

                TextView distanceView = (TextView) findViewById(R.id.track_distance);
                distanceView.setText("Distance:"+UiTools.getDistance(distance));

            }
            TextView recordTime = (TextView) findViewById(R.id.track_fastest_time);
            recordTime.setText("Record:"+UiTools.getTime(track.getRecordtime()));

            TextView trackAvg = (TextView) findViewById(R.id.track_record_avg_time);
            trackAvg.setText("Avg:"+ UiTools.getTime(totalTime(track)/size));




            records = new ArrayList(track.getRecords());



            // Get ListView object from xml
            listView = (ListView) findViewById(R.id.record_list);
            registerForContextMenu(listView);

            // Defined Array values to show in ListView

            // Define a new Adapter
            // First parameter - Context
            // Second parameter - Layout for the row
            // Third parameter - ID of the TextView to which the data is written
            // Forth - the Array of data

            //ArrayAdapter<TrackRecord> adapter = new ArrayAdapter<TrackRecord>(this, android.R.layout.simple_list_item_1,
            //        android.R.id.text1, records);
            TrackRecordAdapter adapter = new TrackRecordAdapter(this,records);
            // Assign adapter to ListView
            listView.setAdapter(adapter);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.record, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        TrackRecord item1 = ((ArrayAdapter<TrackRecord>) listView.getAdapter()).getItem(info.position);
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            try {
                int size = trackXtremeOpenHelper.deleteTrack(item1);
                if (size == 0) {
                    finish();
                }

                ((ArrayAdapter<TrackRecord>) listView.getAdapter()).remove(item1);
            } catch (SQLException e) {

            }

            return true;
        }else if(id==R.id.action_settings){
            item1.roundTrim(true);
            try {
                trackXtremeOpenHelper.getTrackRecordDao().update(item1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.action_trim){
            item1.updateData(true);
            trackXtremeOpenHelper.update(item1);// getTrackRecordDao().update(item1);


        }
        return super.onOptionsItemSelected(item);
    }

    public long totalTime(Track track){
        long totalTime=0;
        for (TrackRecord trackRecord :track.getRecords()){
            totalTime+=trackRecord.getTime();
        }
        return totalTime;

    }
}
