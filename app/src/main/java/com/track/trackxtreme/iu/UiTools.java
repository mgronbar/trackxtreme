package com.track.trackxtreme.iu;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by marko on 30/04/2017.
 */

public class UiTools {
    public static String getTime(long time) {
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time),
                TimeUnit.MILLISECONDS.toMinutes(time) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(time) % TimeUnit.MINUTES.toSeconds(1));
        return hms;
    }

    public static String getAvg(double dist, long time) {
        double hours = (double) time / 1000 / 3600;

        return round((dist / 1000) / hours,2) + " km/h";
    }

    public static String getDistance(double distance) {
        if (distance < 1000) {
            return round(distance,0) + " m";
        }

        return round(distance/1000f,2) + " km";
    }

    public static double round(Number num,int decimals){
        double dec=Math.pow(10,decimals);
        return ((int) (num.doubleValue() * dec)) / dec;
    }

    public static String getDate(long date){
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");// dd/MM/yyyy
        Date start = new Date(date);
        return sdfDate.format(start);

    }
    public static String getHours(long date){
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");// dd/MM/yyyy
        Date start = new Date(date);
        return sdfDate.format(start);

    }

    public static View findViewAt(ViewGroup viewGroup, int x, int y) {
        for(int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ViewGroup) {
                View foundView = findViewAt((ViewGroup) child, x, y);
                if (foundView != null && foundView.isShown()) {
                    return foundView;
                }
            } else {
                int[] location = new int[2];
                child.getLocationOnScreen(location);
                Rect rect = new Rect(location[0], location[1], location[0] + child.getWidth(), location[1] + child.getHeight());
                if (rect.contains(x, y)) {
                    return child;
                }
            }
        }

        return null;
    }

}
