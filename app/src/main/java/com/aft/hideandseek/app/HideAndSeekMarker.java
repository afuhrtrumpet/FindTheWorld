package com.aft.hideandseek.app;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by alex on 4/18/14.
 */
public class HideAndSeekMarker {
    private Marker marker;
    private float zoomLevel;
    private boolean found;

    public HideAndSeekMarker(Marker _marker, float _zoomLevel) {
        marker = _marker;
        zoomLevel = _zoomLevel;
    }

    public Marker getMarker() { return marker; }

    public void adjustVisibilityFromZoom(float zoom) {
        marker.setVisible(zoomLevel <= zoom);
    }

    public void find() {
        found = true;
    }

    public void reset() {
        found = false;
    }
}
