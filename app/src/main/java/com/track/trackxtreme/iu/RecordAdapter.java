package com.track.trackxtreme.iu;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.track.trackxtreme.data.track.TrackRecord;

/**
 * Created by marko on 23/04/2017.
 */

public class RecordAdapter extends ArrayAdapter<String> {

    public RecordAdapter(Context context, int resource, TrackRecord values) {
        super(context, resource);
    }
}
