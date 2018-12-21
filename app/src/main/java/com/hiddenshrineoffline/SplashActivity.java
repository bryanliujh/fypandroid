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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private static String url = "https://ntuhiddenshrine.herokuapp.com/map_geojson";
    private static String url2 = "https://ntuhiddenshrine.herokuapp.com/kmeans_geojson";
    private static AppDatabase INSTANCE;
    private FileManager fileManager;
    private FileManager fileManager2;
    private String jsonStr;
    private String jsonStrKmeans;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        jsonStr = null;
        jsonStrKmeans = null;

        NetworkConnection networkConnection = new NetworkConnection();
        //check if network is connected, if not connected do not download json string
        if(networkConnection.isNetworkConnected(getApplicationContext())) {
            new GetGeoJsonMap().execute();
        }
        else{
            fileManager = new FileManager();
            fileManager2 = new FileManager();
            jsonStr = (String) fileManager.readObjectFile("mapjson", context);
            jsonStrKmeans = (String) fileManager2.readObjectFile("kmeansjson", context);

            new extractLatLng().execute();
            new extractKmeansLatLng().execute();
            startHomeActivity();
        }

    }

    public void startHomeActivity(){
        // Start home activity
        Intent MainActivity = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(MainActivity);
        // close splash activity
        finish();
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
            HttpHandler sh = new HttpHandler();
            HttpHandler sh2 = new HttpHandler();
            // Making a request to url and getting response

            jsonStr = sh.makeServiceCall(url);
            fileManager = new FileManager();
            fileManager.saveObjectFile("mapjson", context, jsonStr);

            jsonStrKmeans = sh2.makeServiceCall(url2);
            fileManager2 = new FileManager();
            fileManager2.saveObjectFile("kmeansjson", context, jsonStrKmeans);


            Log.e("test", "Response from url: " + jsonStr);
            if (jsonStr != null && jsonStrKmeans != null) {
                try {


                } catch (Exception e) {
                    Log.e("jsonStr: ", "Json parsing error: " + e.getMessage());
                }
            }
            else {
                Log.e("no json", "Couldn't get json from server.");
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

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            new extractLatLng().execute();
            new extractKmeansLatLng().execute();

            if (jsonStr != null && jsonStrKmeans != null) {
               startHomeActivity();
            } else if (jsonStr == null){
                Log.e("null string", "null json string re-extraction");
                new extractLatLng().execute();
            }else{
                Log.e("null string2", "null kmeans json string re-extraction");
                new extractKmeansLatLng().execute();
            }
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


    private class extractLatLng extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            HashMap<Integer, ArrayList<String>> hashMap = new HashMap<>();
            try {

                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray features = jsonObject.getJSONArray("features");
                for (int i=0; i<features.length(); i++){
                    JSONObject feature = features.getJSONObject(i);
                    //get cluster id
                    JSONObject properties = feature.getJSONObject("properties");
                    Integer circleID = Integer.parseInt(properties.getString("circleID"));
                    //get coordinates of pts
                    JSONObject geometry = feature.getJSONObject("geometry");
                    JSONArray coord_arr = geometry.getJSONArray("coordinates");
                    double lon = (double) coord_arr.get(0);
                    double lat = (double) coord_arr.get(1);
                    String coord = String.valueOf(lon) + ',' + String.valueOf(lat);
                    hashMap.computeIfAbsent(circleID, k -> new ArrayList<>()).add(coord);
                }


            }
            catch(Exception e){
                Log.e("json_error","Error Extracting Latitude and Longitude");
            }


            try{
                File file = new File(context.getFilesDir(), "lat_lon_file");
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
                outputStream.writeObject(hashMap);
                outputStream.flush();
                outputStream.close();
            }
            catch(Exception e){
                Log.e("save_error","Error Saving Latitude and Longitude");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Toast.makeText(getApplicationContext(), "Extracting of Latitude and Longitude Finished", Toast.LENGTH_SHORT).show();

        }
    }

    private class extractKmeansLatLng extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            HashMap<Integer, ArrayList<String>> hashMap = new HashMap<>();
            HashMap<Integer, String> hashMapCentroid = new HashMap<>();
            try {

                JSONObject jsonObject = new JSONObject(jsonStrKmeans);
                JSONArray features = jsonObject.getJSONArray("features");
                for (int i=0; i<features.length(); i++){
                    JSONObject feature = features.getJSONObject(i);
                    //get cluster id
                    JSONObject properties = feature.getJSONObject("properties");
                    //check if approved is centroid, if it is ignore it
                    if (!properties.getString("approved").equals("centroid")) {
                        Integer circleID = Integer.parseInt(properties.getString("circleID"));
                        //get coordinates of pts
                        JSONObject geometry = feature.getJSONObject("geometry");
                        JSONArray coord_arr = geometry.getJSONArray("coordinates");
                        double lon = (double) coord_arr.get(0);
                        double lat = (double) coord_arr.get(1);
                        String coord = String.valueOf(lon) + ',' + String.valueOf(lat);
                        hashMap.computeIfAbsent(circleID, k -> new ArrayList<>()).add(coord);
                    } else {
                        hashMapCentroid.put(Integer.parseInt(properties.getString("circleID")), properties.getString("name"));
                    }
                }


            }
            catch(Exception e){
                Log.e("json_error","Error Extracting Latitude and Longitude");
            }


            try{

                //save kmeans lat lon file
                FileManager fileManager = new FileManager();
                fileManager.saveObjectFile("kmeans_lat_lon_file", context, hashMap);

                //save centroid cluster id
                FileManager fileManager2 = new FileManager();
                fileManager2.saveObjectFile("centroid_file", context, hashMapCentroid);
            }
            catch(Exception e){
                Log.e("save_error","Error Saving Latitude and Longitude");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Toast.makeText(context, "Extracting of Latitude and Longitude Finished", Toast.LENGTH_SHORT).show();

        }
    }






}
