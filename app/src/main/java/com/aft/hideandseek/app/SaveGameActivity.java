package com.aft.hideandseek.app;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SaveGameActivity extends ActionBarActivity {

    private Button button;
    private EditText et;

    private static final String APP_DIR = "hideandseek";
    private static final int BUFFER = 2048;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_game);

        button = (Button)findViewById(R.id.bSave);
        et = (EditText)findViewById(R.id.filename);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    File file = new File(Environment.getExternalStorageDirectory(), APP_DIR + "/" + et.getText() + ".json");
                    try {
                        OutputStream out = new FileOutputStream(file);
                        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
                        writer.beginArray();
                        ArrayList<HideAndSeekMarker> markers = SaveGameActivity.this.getIntent().getParcelableArrayListExtra("Markers");
                        ArrayList<String> filenames = new ArrayList<String>();
                        filenames.add(file.getAbsolutePath());
                        for (HideAndSeekMarker marker : markers) {
                            writer.beginObject();
                            writer.name("Name").value(marker.getName());
                            writer.name("Lat").value(marker.getPosition().latitude);
                            writer.name("Long").value(marker.getPosition().longitude);
                            writer.name("Zoom").value(marker.getZoomLevel());
                            writer.name("File").value(marker.getFilename().replace(Environment.getExternalStorageDirectory().getAbsolutePath(), ""));
                            if (!marker.getFilename().equals("")) {
                                filenames.add(marker.getFilename());
                            }
                            writer.endObject();
                        }
                        writer.endArray();
                        writer.close();
                        out.close();
                        zip(filenames.toArray(new String[filenames.size()]), new File(Environment.getExternalStorageDirectory(), APP_DIR + "/" + et.getText() + ".zip").getAbsolutePath());
                        Intent startIntent = new Intent(SaveGameActivity.this, MainMenuActivity.class);
                        Toast toast = Toast.makeText(getApplicationContext(), "File written successfully.", 2000);
                        toast.setDuration(2000);
                        toast.show();
                        startActivity(startIntent);
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
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_game, menu);
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

    public void zip(String[] _files, String zipFileName)
    {
        //_files is the path of the files which you want to make it zip

        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
