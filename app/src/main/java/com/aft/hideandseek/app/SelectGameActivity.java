package com.aft.hideandseek.app;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;


public class SelectGameActivity extends ActionBarActivity {

    private static final String APP_DIR = "hideandseek";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);

        ArrayList<String> matchingFiles = new ArrayList<String>();
        ListView list = (ListView) findViewById(R.id.fileList);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(Environment.getExternalStorageDirectory(), APP_DIR);
            File[] files = dir.listFiles();
            for (File file : files) {
                if (!file.isDirectory() && file.getName().endsWith(".json")) {
                    matchingFiles.add(file.getName().replace(".json", ""));
                }
            }
            //String[] filesArray = new String[matchingFiles.size()];
            final String[] filesArray = matchingFiles.toArray(new String[matchingFiles.size()]);
            ArrayAdapter<String> fileNames = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filesArray);
            list.setAdapter(fileNames);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent playIntent = new Intent(SelectGameActivity.this, PlayGameActivity.class);
                    playIntent.putExtra("File", filesArray[position] + ".json");
                    startActivity(playIntent);
                }
            });
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "External storage is not mounted!", 2000);
            toast.setDuration(2000);
            toast.show();
            finish();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_game, menu);
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
