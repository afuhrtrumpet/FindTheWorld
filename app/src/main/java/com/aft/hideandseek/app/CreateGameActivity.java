package com.aft.hideandseek.app;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

public class CreateGameActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng selectedLoc;
    private ArrayList<HideAndSeekMarker> markers;
    private HideAndSeekMarker selectedMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        registerForContextMenu(findViewById(R.id.map));

        markers = new ArrayList<HideAndSeekMarker>();

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                selectedMarker = null;
                selectedLoc = latLng;

                Log.d("", "Map long click occurred");
                openContextMenu(findViewById(R.id.map));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (HideAndSeekMarker m : markers) {
                    if (m.getMarker().equals(marker)) {
                        selectedMarker = m;
                    }
                }
                openContextMenu(findViewById(R.id.map));
                return true;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        if (selectedMarker != null)
            inflater.inflate(R.menu.edit_context_menu, menu);
        else
            inflater.inflate(R.menu.create_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.add_marker:
                Intent settingsIntent = new Intent(CreateGameActivity.this, MarkerSettingsActivity.class);
                settingsIntent.putExtra("New", true);
                settingsIntent.putExtra("Zoom", (int)mMap.getCameraPosition().zoom);
                startActivityForResult(settingsIntent, 1);
                return true;
            case R.id.save:
                Intent saveIntent = new Intent(CreateGameActivity.this, SaveGameActivity.class);
                saveIntent.putParcelableArrayListExtra("Markers", markers);
                startActivity(saveIntent);
                return true;
            case R.id.edit_marker:
                Intent editIntent = new Intent(CreateGameActivity.this, MarkerSettingsActivity.class);
                editIntent.putExtra("New", false);
                editIntent.putExtra("Marker", selectedMarker);
                startActivityForResult(editIntent, 1);
                return true;
            case R.id.delete_marker:
                selectedMarker.getMarker().remove();
                markers.remove(selectedMarker);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putParcelableArrayList("Markers", markers);
        bundle.putParcelable("Selected Location", selectedLoc);
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);

        markers = bundle.getParcelableArrayList("Markers");
        for (HideAndSeekMarker m : markers) {
            m.displayOnMap(mMap);
        }
        selectedLoc = bundle.getParcelable("Selected Location");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(resultCode + "", RESULT_OK + "");
        if (resultCode == RESULT_OK) {
            if (selectedMarker != null) {
                Log.d("", "Changing marker info");
                selectedMarker.setName(data.getStringExtra("Name"));
                selectedMarker.setZoomLevel(data.getIntExtra("Zoom", (int)selectedMarker.getZoomLevel()));
                selectedMarker.setImage(data.getStringExtra("File"));
            }
            else {
                Log.d("", "Adding new marker");
                HideAndSeekMarker marker;
                if (data.getStringExtra("File").length() > 0)
                    marker= new HideAndSeekMarker(mMap, data.getStringExtra("Name"), selectedLoc, data.getIntExtra("Zoom", 10), data.getStringExtra("File"));
                else
                    marker= new HideAndSeekMarker(mMap, data.getStringExtra("Name"), selectedLoc, data.getIntExtra("Zoom", 10));
                markers.add(marker);
            }
        }
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

    private void setUpMap() {
    }
}
