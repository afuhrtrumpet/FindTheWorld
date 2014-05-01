package com.aft.hideandseek.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.Image;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MarkerSettingsActivity extends ActionBarActivity {

    private static final int IMAGE_WIDTH = 160;
    private static final int PADDING = 8;
    private static final String ORIGINAL_DIR = new File(Environment.getExternalStorageDirectory(), "hideandseek").getAbsolutePath();
    private final String[] IMAGE_EXTENSIONS = {"jpg", "png", "bmp", "gif", "jpeg"};
    private int selectedIndex = 1;
    private String currentDir = ORIGINAL_DIR;

    private CheckBox cCustom;
    private GridView iconGrid;
    private EditText nameText;
    private NumberPicker zoomPicker;
    private ArrayList<String> files = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_settings);

        cCustom = (CheckBox) findViewById(R.id.customBox);
        Button b = (Button) findViewById(R.id.bSetMarker);
        iconGrid = (GridView) findViewById(R.id.iconGrid);
        iconGrid.setNumColumns((int)(getWindowManager().getDefaultDisplay().getWidth() / (PADDING + IMAGE_WIDTH)));
        nameText = (EditText) findViewById(R.id.markerText);
        zoomPicker = (NumberPicker) findViewById(R.id.zoomLevel);
        zoomPicker.setMinValue(0);
        zoomPicker.setMaxValue(20);

        getImageFiles(currentDir);
        final ImageAdapter adapter = new ImageAdapter(this);
        iconGrid.setAdapter(adapter);

        if (getIntent().getBooleanExtra("New", true)) {
            zoomPicker.setValue(getIntent().getIntExtra("Zoom", 10));
        } else {
            HideAndSeekMarker marker = getIntent().getParcelableExtra("Marker");
            zoomPicker.setValue((int)marker.getZoomLevel());
            nameText.setText(marker.getName());
            cCustom.setChecked(marker.getFilename().equals(""));
            if (cCustom.isChecked()) {
                currentDir = marker.getFilename().substring(0, marker.getFilename().lastIndexOf('/'));
            }
            adapter.notifyDataSetChanged();
            for (int i = 0; i < files.size(); i++) {
                if (files.get(i).equals(marker.getFilename())) {
                    selectedIndex = i;
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
        }

        iconGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (files.get(position).equals("Back")) {
                    currentDir = new File(currentDir, "../").getAbsolutePath();
                    getImageFiles(currentDir);
                } else if (new File(files.get(position)).isDirectory()) {
                    currentDir = files.get(position);
                    getImageFiles(currentDir);
                } else {
                    selectedIndex = position;
                }
                adapter.notifyDataSetChanged();
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("Name", nameText.getText().toString());
                intent.putExtra("Zoom", zoomPicker.getValue());
                String imageFile = "";
                if (cCustom.isChecked()) {
                    if (selectedIndex > files.size()) {
                        Toast t = Toast.makeText(getApplicationContext(), "Please select an image!", 2000);
                        t.show();
                        return;
                    }
                    imageFile = files.get(selectedIndex);
                }
                intent.putExtra("File", imageFile.replace(Environment.getExternalStorageDirectory().getAbsolutePath(), ""));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void getImageFiles(String directory) {
        files = new ArrayList<String>();
        File dir = new File(directory);
        ArrayList<String> dirs = new ArrayList<String>();
        if (dir.isDirectory())
            for (File f : dir.listFiles()) {
                if (f.isDirectory()) {
                    dirs.add(f.getAbsolutePath());
                }
                else for (String ext : IMAGE_EXTENSIONS)
                    if (f.getName().toLowerCase().endsWith(ext)) {
                        Log.d(f.toString(), "Adding file");
                        files.add(f.getAbsolutePath());
                    }
            }
        Collections.sort(files, new StringComparator());
        files.addAll(0, dirs);
        if (!directory.equals(ORIGINAL_DIR))
            files.add(0, "Back");
        selectedIndex = 1 + dirs.size();
    }

    public class StringComparator implements Comparator<String> {
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        bundle.putString("Dir", currentDir);
        bundle.putInt("Selected Index", selectedIndex);
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);

        currentDir = bundle.getString("Dir");
        selectedIndex = bundle.getInt("Selected Index");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String filePath = data.getData().getPath();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.marker_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;

        public ImageAdapter(Context context) {
            this.context = context;
        }

        public int getCount () {
            return files.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Log.d("", "Getting file " + files.get(position));
            Bitmap myBitmap;
            if (files.get(position).equals("Back")) {
                ImageView c;
                if (convertView == null || !(convertView instanceof ImageView)) {
                    c = new ImageView(context);

                    c.setLayoutParams(new GridView.LayoutParams(IMAGE_WIDTH, IMAGE_WIDTH));
                    c.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    c.setPadding(PADDING, PADDING, PADDING, PADDING);
                } else{
                    c = (ImageView) convertView;
                }
                myBitmap = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_revert);
                c.setImageBitmap(myBitmap);
                return c;
            } else if (new File(files.get(position)).isDirectory()) {
                TextView t;
                if (convertView == null || !(convertView instanceof TextView)) {
                    t = new TextView(context);

                    t.setLayoutParams(new GridView.LayoutParams(160, 160));
                    t.setPadding(8,8,8,8);
                } else {
                    t = (TextView) convertView;
                }
                t.setText(files.get(position).substring(files.get(position).lastIndexOf('/') + 1));
                t.setBackgroundResource(android.R.drawable.ic_menu_add);
                return t;
            } else {
                ImageView c;
                if (convertView == null || !(convertView instanceof ImageView)) {
                    c = new ImageView(context);

                    c.setLayoutParams(new GridView.LayoutParams(160, 160));
                    c.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    c.setPadding(8,8,8,8);
                } else {
                    c = (ImageView) convertView;
                }
                if (position == selectedIndex) {
                    c.setBackgroundColor(Color.parseColor("#444444"));
                } else {
                    c.setBackgroundColor(Color.TRANSPARENT);
                }
                myBitmap = BitmapFactory.decodeFile(files.get(position));
                c.setImageBitmap(myBitmap);
                return c;
            }
        }
    }

}
