package com.track.trackxtreme.rest.data;

import java.util.List;

import com.track.trackxtreme.MainActivity;
import com.track.trackxtreme.data.track.Track;

import android.os.AsyncTask;

public class Trackretriever extends AsyncTask<String, Void, List<Track>> {

	private MainActivity mainActivity;
	private Exception exception;

	public Trackretriever(MainActivity main) {
		mainActivity = main;
	}

	@Override
	protected List<Track> doInBackground(String... arg0) {
		LibraryResteasyClient client = new LibraryResteasyClient(mainActivity.getApplicationContext());

		try {
			return client.getTracks();
		} catch (RuntimeException e) {
			this.exception = e;
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(List<Track> result) {
		mainActivity.newTracks(result);
	};

}
