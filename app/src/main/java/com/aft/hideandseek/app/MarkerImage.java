package com.aft.hideandseek.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alex on 4/29/14.
 */
public class MarkerImage implements Parcelable {
    private String origin;
    private String dir;
    private String filename;

    public MarkerImage(String origin, String dir, String filename) {
        this.origin = origin;
        this.dir = dir;
        this.filename = filename;
    }

    public MarkerImage(Parcel parcel) {
        origin = parcel.readString();
        dir = parcel.readString();
        filename = parcel.readString();
    }

    public String getOrigin() { return origin; }

    public String getDir() { return dir; }

    public String getFilename() { return filename; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(origin);
        dest.writeString(dir);
        dest.writeString(filename);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public Object createFromParcel(Parcel source) {
            return new MarkerImage(source);
        }

        @Override
        public Object[] newArray(int size) {
            return new MarkerImage[size];
        }
    };
}
