package com.track.trackxtreme.iu;

import java.sql.SQLException;

import com.track.trackxtreme.R;
import com.track.trackxtreme.data.TrackXtremeOpenHelper;
import com.track.trackxtreme.data.track.Track;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RecordActivity extends Activity {

	private ListView listView;
	private TrackXtremeOpenHelper trackXtremeOpenHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		trackXtremeOpenHelper = new TrackXtremeOpenHelper(getApplicationContext());
		int object = getIntent().getExtras().getInt("TRACK_ID");
		try {
			Track track = trackXtremeOpenHelper.getTrackDao().queryForId(object);

			Object[] records = track.getRecords().toArray();
			String[] values = new String[records.length];
			for (int i = 0; i < records.length; i++) {
				values[i] = records[i].toString();
			}

			setContentView(R.layout.activity_record);

			// Get ListView object from xml
			listView = (ListView) findViewById(R.id.record_list);

			// Defined Array values to show in ListView

			// Define a new Adapter
			// First parameter - Context
			// Second parameter - Layout for the row
			// Third parameter - ID of the TextView to which the data is written
			// Forth - the Array of data

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
					android.R.id.text1, values);

			// Assign adapter to ListView
			listView.setAdapter(adapter);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
