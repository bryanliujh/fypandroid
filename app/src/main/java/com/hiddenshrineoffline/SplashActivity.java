package com.hiddenshrineoffline;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static String url = "https://ntuhiddenshrine.herokuapp.com/map_geojson";
    //private static String coord_url = "https://ntuhiddenshrine.herokuapp.com/map_coord";
    private static AppDatabase INSTANCE;
    private FileManager fileManager;
    private FileManager coordFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new GetGeoJsonMap().execute();
    }



    private class GetGeoJsonMap extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(SplashActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }


        @Override
        protected Void doInBackground(Void... arg0) {

            NetworkConnection networkConnection = new NetworkConnection();
            //check if network is connected, if not connected do not download json string
            if(networkConnection.isNetworkConnected(getApplicationContext())) {
                HttpHandler sh = new HttpHandler();
                // Making a request to url and getting response

                String jsonStr = sh.makeServiceCall(url);
                fileManager = new FileManager();
                fileManager.saveFile("mapjson", jsonStr,getApplicationContext());


                //request for coordinates of all points sorted by cluster
                //HttpHandler sh2 = new HttpHandler();
                //String coordStr = sh2.makeServiceCall(coord_url);
                //coordFileManager = new FileManager();
                //coordFileManager.saveFile("coord_pts", coordStr, getApplicationContext());


                Log.e("test", "Response from url: " + jsonStr);
                if (jsonStr != null) {
                    try {


                    } catch (Exception e) {
                        Log.e("test2", "Json parsing error: " + e.getMessage());
                    }
                } else {
                    Log.e("test3", "Couldn't get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            // Start home activity
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            // close splash activity
            finish();
        }



    }


    public static AppDatabase getDatabase(final Context context){
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "shrine_database").build();
                }
            }
        }
        return INSTANCE;
    }




}
