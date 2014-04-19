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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SaveGameActivity extends ActionBarActivity {

    private Button button;
    private EditText et;

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
                    File file = new File(Environment.getExternalStorageDirectory(), et.getText() + ".json");
                    try {
                        OutputStream out = new FileOutputStream(file);
                        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
                        writer.beginArray();
                        ArrayList<HideAndSeekMarker> markers = SaveGameActivity.this.getIntent().getParcelableArrayListExtra("Markers");
                        for (HideAndSeekMarker marker : markers) {
                            writer.beginObject();
                            writer.name("Name").value(marker.getName());
                            writer.name("Latlng").value(marker.getPosition().toString());
                            writer.endObject();
                        }
                        writer.endArray();
                        writer.close();
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

}
