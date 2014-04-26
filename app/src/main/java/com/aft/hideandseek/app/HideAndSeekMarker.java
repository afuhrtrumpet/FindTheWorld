package com.aft.hideandseek.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
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
    private String filename = "";

    public HideAndSeekMarker(GoogleMap map, String name, LatLng position, float _zoomLevel) {
        marker = map.addMarker(new MarkerOptions().title(name).position(position));
        zoomLevel = _zoomLevel;
        this.name = name;
        this.position = position;
    }

    public HideAndSeekMarker(GoogleMap map, String name, LatLng position, float _zoomLevel, String filename) {
        this.filename = filename;
        File file = new File(filename);
        Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());

        marker = map.addMarker(new MarkerOptions().title(name).position(position).icon(BitmapDescriptorFactory.fromBitmap(image)));
        zoomLevel = _zoomLevel;
        this.name = name;
        this.position = position;
        this.filename = filename;
    }

    public HideAndSeekMarker(Parcel in) {
        name = in.readString();
        position = in.readParcelable(LatLng.class.getClassLoader());
        zoomLevel = in.readFloat();
        filename = in.readString();
    }

    public Marker getMarker() { return marker; }

    public String getName() { return name; }

    public LatLng getPosition() { return position; }

    public float getZoomLevel() { return zoomLevel; }

    public String getFilename() { return filename; }

    public void adjustVisibilityFromZoom(float zoom) {
        marker.setVisible(zoomLevel <= zoom);
    }

    public void find() {
        found = true;
        marker.setAlpha(0.5F);
    }

    public void reset() {
        found = false;
        marker.setAlpha(1.0F);
    }

    public boolean isFound() { return found; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(position, 0);
        dest.writeFloat(zoomLevel);
        dest.writeString(filename);
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
