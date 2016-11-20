package com.track.trackxtreme.rest;

import com.track.trackxtreme.R;
import com.track.trackxtreme.rest.data.LibraryResteasyClient;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class LibraryApplication extends Application {

	private LibraryClient library;
	public static String KEY_BOOK_ISBN = "KEY_BOOK_INDEX";
	public static String LOG_TAG = "JaxrsSample";

	@Override
	public void onCreate() {
		super.onCreate();
		library = new LibraryResteasyClient(this);
	}

	public LibraryClient getLibraryClient() {
		return library;
	}

	public static String getRequestURI(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String hostKey = context.getString(R.string.pref_host_key);
		String host = prefs.getString(hostKey, context.getString(R.string.pref_host_default));
		String portKey = context.getString(R.string.pref_port_key);
		String port = prefs.getString(portKey, context.getString(R.string.pref_port_default));
		String requestURI = "http://www.mocky.io/v2";
		Log.i(LOG_TAG, "requestURI: " + requestURI);
		return requestURI;
	}
}
