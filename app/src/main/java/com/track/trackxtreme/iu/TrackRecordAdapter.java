package com.track.trackxtreme.iu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
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
    public TrackRecordAdapter(Context context, List<TrackRecord> objects) {
        super(context, R.layout.track_record_list_item, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.track_record_list_item, parent, false);
        }
        //View view = super.getView(position, convertView, parent);
        TextView count = (TextView) convertView.findViewById(R.id.track_record_count);
        count.setText(Integer.toString(position));
        TrackRecord tr = getItem(position);
        TextView distance = (TextView) convertView.findViewById(R.id.track_record_distance);

        TextView max = (TextView) convertView.findViewById(R.id.track_record_max_speed);
        max.setText("Max: "+tr.getMaxspeed() + " km/h");

        float dist = tr.getDistance();
        distance.setText("Dist: "+UiTools.getDistance(dist));
        Long time = tr.getTime();
        TextView timeView = (TextView) convertView.findViewById(R.id.track_record_time);
        timeView.setText(UiTools.getTime(time));

        TextView avgView = (TextView) convertView.findViewById(R.id.track_record_avg_speed);
        avgView.setText("Avg: "+UiTools.getAvg(dist, time));

        TextView dateView = (TextView) convertView.findViewById(R.id.track_record_date);
        if(tr.getStarttime()!=null) {
            dateView.setText(UiTools.getDate(tr.getStarttime()));
            TextView dateHourView = (TextView) convertView.findViewById(R.id.track_record_date_time);
            dateHourView.setText(UiTools.getHours(tr.getStarttime()) + "-" + UiTools.getHours(tr.getStarttime() + time));
        }


        return convertView;
    }
}
