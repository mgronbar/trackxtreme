package com.track.trackxtreme.helper;

import com.google.android.gms.maps.model.LatLngBounds;
import com.track.trackxtreme.data.track.Track;

import java.util.Collection;
import java.util.List;

/**
 * Created by marko on 15/05/2017.
 */

public interface TrackSearcher {
    Collection<Track> searchTracks(LatLngBounds bounds);
}
