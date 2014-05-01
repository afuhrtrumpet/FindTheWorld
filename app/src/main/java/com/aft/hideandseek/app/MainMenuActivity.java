package com.aft.hideandseek.app;

import android.content.Intent;
import android.net.Uri;
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


public class MainMenuActivity extends ActionBarActivity {

    private static final String APP_DIR = "hideandseek";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getApplicationContext(), "External storage is not mounted!", 2000).show();
            finish();
        }
        new File(Environment.getExternalStorageDirectory(), APP_DIR).mkdir();

        String[] options = {"Create new game", "Play existing game", "Share game", "Download images", "About app"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);
        ListView list = (ListView)findViewById(R.id.mainList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent createIntent = new Intent(MainMenuActivity.this, CreateGameActivity.class);
                    startActivity(createIntent);
                } else if (position == 1) {
                    Intent selectIntent = new Intent(MainMenuActivity.this, SelectGameActivity.class);
                    startActivity(selectIntent);
                } else if (position == 2) {
                    Intent shareIntent = new Intent(MainMenuActivity.this, ShareGameActivity.class);
                    startActivity(shareIntent);
                } else if (position == 3) {
                    Intent downloadIntent = new Intent(MainMenuActivity.this, ImageMenuActivity.class);
                    startActivity(downloadIntent);
                } else if (position == 4) {
                    Intent aboutIntent = new Intent(Intent.ACTION_VIEW);
                    aboutIntent.setData(Uri.parse("http://afuhrtrumpet.github.io/findtheworld.html"));
                    startActivity(aboutIntent);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
