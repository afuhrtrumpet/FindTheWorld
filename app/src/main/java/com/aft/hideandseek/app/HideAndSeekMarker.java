package com.aft.hideandseek.app;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;

/**
 * Created by alex on 4/18/14.
 */
public class HideAndSeekMarker implements Parcelable {
    private Marker marker;
    private String name;
    private LatLng position;
    private float zoomLevel;
    private boolean found;

    public HideAndSeekMarker(GoogleMap map, String name, LatLng position, float _zoomLevel) {
        marker = map.addMarker(new MarkerOptions().title(name).position(position));
        zoomLevel = _zoomLevel;
        this.name = name;
        this.position = position;
    }

    public HideAndSeekMarker(Parcel in) {
        name = in.readString();
        position = in.readParcelable(LatLng.class.getClassLoader());
        zoomLevel = in.readFloat();
    }

    public String getName() { return name; }

    public LatLng getPosition() { return position; }

    public float getZoomLevel() { return zoomLevel; }

    public void adjustVisibilityFromZoom(float zoom) {
        marker.setVisible(zoomLevel <= zoom);
    }

    public void find() {
        found = true;
    }

    public void reset() {
        found = false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(position, 0);
        dest.writeFloat(zoomLevel);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new HideAndSeekMarker(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new HideAndSeekMarker[size];
        }
    };
}
