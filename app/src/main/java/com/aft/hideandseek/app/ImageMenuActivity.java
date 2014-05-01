package com.aft.hideandseek.app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ImageMenuActivity extends ActionBarActivity {


    private static final String APP_DIR = "hideandseek";
    private static final String POKEMON_DIR = "http://www.serebii.net/xy/pokemon/";

    private final AsyncTask pkmnDownloader = new AsyncTask() {

        ProgressDialog progress;

        @Override
        protected Object doInBackground(Object[] params) {
            File storageDir = new File(Environment.getExternalStorageDirectory(), APP_DIR);
            File dir = new File(storageDir, "pokemon");
            dir.mkdir();
            for (int i = 1; i <= 718; i++) {
                progress.setMessage(i + "/718");
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try {
                    String pkmnUrl = POKEMON_DIR + ("000" + i).substring(Integer.toString(i).length()) + ".png";
                    Log.d("", pkmnUrl);
                    URL url = new URL(pkmnUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();
                    }

                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    int fileLength = connection.getContentLength();

                    // download the file
                    input = connection.getInputStream();
                    output = new FileOutputStream(dir.getAbsolutePath() + "/" + ("000" + i).substring(Integer.toString(i).length()) + ".png");

                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        // allow canceling with back button
                        if (isCancelled()) {
                            input.close();
                            return null;
                        }
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) // only if total length is known
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                } catch (Exception e) {
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }

                    if (connection != null)
                        connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(ImageMenuActivity.this);
            progress.setTitle("Downloading Pokemon images");
            progress.setCancelable(true);
            progress.show();
            progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    pkmnDownloader.cancel(true);
                }
            });
        }

        @Override
        protected void onPostExecute(Object v) {
            if (v != null)
                Log.d("", v.toString());
            progress.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_menu);
        String[] options = {"Pokemon"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options);
        ListView list = (ListView)findViewById(R.id.downloaderList);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //Download pokeymanz
                    if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(getApplicationContext(), "SD card is not mounted!", 2000).show();
                    } else {
                        pkmnDownloader.execute();
                    }
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_menu, menu);
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
