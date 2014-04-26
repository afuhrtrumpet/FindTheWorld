package com.aft.hideandseek.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Environment;
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
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MarkerSettingsActivity extends ActionBarActivity {

    private final String[] IMAGE_EXTENSIONS = {"jpg", "png", "bmp", "gif", "jpeg"};
    private int selectedIndex = 0;

    private CheckBox cCustom;
    private GridView iconGrid;
    private ArrayList<String> files = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_settings);

        cCustom = (CheckBox) findViewById(R.id.customBox);
        Button b = (Button) findViewById(R.id.bSetMarker);
        iconGrid = (GridView) findViewById(R.id.iconGrid);
        getImageFiles();
        final ImageAdapter adapter = new ImageAdapter(this);
        iconGrid.setAdapter(adapter);
        iconGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedIndex = position;
                adapter.notifyDataSetChanged();
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.markerText);

                Intent intent = new Intent();
                intent.putExtra("Name", et.getText().toString());
                String imageFile = "";
                if (cCustom.isChecked()) {
                    imageFile = files.get(selectedIndex);
                }
                intent.putExtra("File", imageFile);
                setResult(1, intent);
                finish();
            }
        });
    }

    public void getImageFiles() {
        File dir = new File(Environment.getExternalStorageDirectory(), "pkmn");
        if (dir.isDirectory())
            for (File f : dir.listFiles())
                for (String ext : IMAGE_EXTENSIONS)
                    if (f.getName().toLowerCase().endsWith(ext)) {
                        Log.d(f.toString(), "Adding file");
                        files.add(f.getAbsolutePath());
                    }
        Collections.sort(files, new StringComparator());
    }

    public class StringComparator implements Comparator<String> {
        public int compare(String s1, String s2) {
            return s1.compareTo(s2);
        }
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
            Log.d("", "Getting position " + position);
            ImageView c;
            if (convertView == null) {
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
            Bitmap myBitmap = BitmapFactory.decodeFile(files.get(position));
            c.setImageBitmap(myBitmap);

            return c;
        }
    }

}
