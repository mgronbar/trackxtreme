package com.track.trackxtreme.data.track;

/**
 * Created by marko on 25/04/2017.
 */

public class GeoHelper {
    public static double distance(TrackPoint p1, TrackPoint p2){
        return distance(p1.getLocation().getLatitude(),p1.getLocation().getLongitude(),p2.getLocation().getLatitude(),p2.getLocation().getLongitude());
    }
    /**
     * Calculates the distance in km between two lat/long points
     * using the haversine formula
     */
    public static double distance(
            double lat1, double lng1, double lat2, double lng2) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = r * c;
        return d;
    }
}
