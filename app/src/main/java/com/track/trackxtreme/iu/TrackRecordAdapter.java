package com.track.trackxtreme.iu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.track.trackxtreme.R;
import com.track.trackxtreme.data.track.TrackRecord;

import java.util.List;

/**
 * Created by marko on 29/04/2017.
 */

public class TrackRecordAdapter extends ArrayAdapter<TrackRecord> {
    public TrackRecordAdapter(Context context,List<TrackRecord> objects) {
        super(context, R.layout.track_record_list_item,objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView count = (TextView) view.findViewById(R.id.track_record_count);
        return view;
    }
}
