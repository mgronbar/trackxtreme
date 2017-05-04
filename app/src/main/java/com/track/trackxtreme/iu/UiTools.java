package com.track.trackxtreme.iu;

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
            return distance + " m";
        }

        return round(distance/1000f,2) + " km";
    }

    public static double round(Number num,int decimals){
        double dec=Math.pow(10,decimals);
        return ((int) (num.doubleValue() * dec)) / dec;
    }
}
