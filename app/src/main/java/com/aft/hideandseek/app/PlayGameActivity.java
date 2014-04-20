package com.aft.hideandseek.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PlayGameActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private List<HideAndSeekMarker> markers;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);
        setUpMapIfNeeded();

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("", "Camera change event occurred");
                Log.d("", markers.toString());
                for (HideAndSeekMarker m : markers) {
                    Log.d("", "Adjusting zoom of " + m.getName());
                    m.adjustVisibilityFromZoom(cameraPosition.zoom);
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (HideAndSeekMarker m : markers)
                    if (m.getMarker().equals(marker)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PlayGameActivity.this);
                        if (!m.isFound()) {
                            m.find();
                            score++;
                            if (score == markers.size()) {
                                builder.setMessage("Congrats!").setTitle("You won!");
                                builder.setPositiveButton("Play Again", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        score = 0;
                                        for (HideAndSeekMarker m : markers)
                                            m.reset();
                                        dialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                            } else {
                                builder.setMessage(score + "/" + markers.size() + " found. Keep going!").setTitle("You found " + marker.getTitle() + "!");
                                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        } else {
                            builder.setMessage("Try to find another!").setTitle("Already found!");
                            builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }

                        AlertDialog dialog = builder.create();

                        dialog.show();
                    }

                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory(), getIntent().getStringExtra("File"));
            try {
                markers = new ArrayList<HideAndSeekMarker>();
                InputStream in = new FileInputStream(file);
                JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();
                    String name = "";
                    float latitude = 0, longitude = 0, zoom = 0;
                    while (reader.hasNext()) {
                        String key = reader.nextName();
                        if (key.equals("Name"))
                            name = reader.nextString();
                        else if (key.equals("Lat"))
                            latitude = (float)reader.nextDouble();
                        else if (key.equals("Long"))
                            longitude = (float)reader.nextDouble();
                        else if (key.equals("Zoom"))
                            zoom = (float)reader.nextDouble();
                    }
                    reader.endObject();
                    HideAndSeekMarker m = new HideAndSeekMarker(mMap, name, new LatLng(latitude, longitude), zoom);
                    m.adjustVisibilityFromZoom(mMap.getCameraPosition().zoom);
                    Log.d("", "marker " + m.getName() + " added.");
                    markers.add(m);
                    Log.d("", markers.toString());
                }
                reader.endArray();
                reader.close();
                score = 0;
                Toast toast = Toast.makeText(getApplicationContext(), "Game loaded successfully", 2000);
                toast.setDuration(2000);
                toast.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "External storage is not mounted!", 2000);
            toast.setDuration(2000);
            toast.show();
            finish();
        }
    }
}
