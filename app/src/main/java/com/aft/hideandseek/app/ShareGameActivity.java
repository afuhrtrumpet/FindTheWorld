package com.aft.hideandseek.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class ShareGameActivity extends ActionBarActivity {

    private static final String APP_DIR = "hideandseek";
    private static final File DIR = new File(Environment.getExternalStorageDirectory(), APP_DIR);
    ArrayList<String> matchingFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_game);

        matchingFiles = new ArrayList<String>();
        ListView list = (ListView) findViewById(R.id.shareList);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File[] files = DIR.listFiles();
            for (File file : files) {
                if (!file.isDirectory() && file.getName().endsWith(".zip")) {
                    matchingFiles.add(file.getName().replace(".zip", ""));
                }
            }
            //String[] filesArray = new String[matchingFiles.size()];
            final String[] filesArray = matchingFiles.toArray(new String[matchingFiles.size()]);
            ArrayAdapter<String> fileNames = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filesArray);
            list.setAdapter(fileNames);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    File selectedFile = new File(DIR, matchingFiles.get(position) + ".zip");
                    Log.d(selectedFile.getAbsolutePath(), "File path");
                    shareIntent.setType("*/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(selectedFile));
                    startActivity(Intent.createChooser(shareIntent, "Send to"));
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
        getMenuInflater().inflate(R.menu.share_game, menu);
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
