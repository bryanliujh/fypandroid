package com.hiddenshrineoffline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mapbox.mapboxsdk.style.expressions.Expression.color;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;


public class KMeansCluster {
    private FileManager fileManager;
    private Context context;
    private MapLayerSource mapLayerSource;
    private MapboxMap mapboxMap;
    private MapboxMap.OnMapClickListener clusterListener;
    private String jsonStr;

    private static final String SOURCE_ID = "shrine.data";
    private static final String LAYER_ID = "shrine.layer";
    private static final String CLUSTER_SOURCE_ID = "shrine.cluster";
    private static final String CLUSTER_LAYER_ID = "shrine.cluster.layer";
    private static final String K_SOURCE_ID = "kmeans.data";
    private static final String K_LAYER_ID = "kmeans.layer";
    private CircleLayer circleLayer;
    private Expression.Stop[] stops;
    private final int CLUSTER_NUM = 14;

    public void initKMeans(Context context, MapboxMap mapboxMap, String[] colorArr, String jsonStr){
        context = this.context;
        mapboxMap = this.mapboxMap;
        jsonStr = this.jsonStr;

        stops = new Expression.Stop[CLUSTER_NUM];
        for (int i=0; i<CLUSTER_NUM; i++){
            stops[i] = stop(String.valueOf(i),color(Color.parseColor(colorArr[i])));
        }

        mapLayerSource = new MapLayerSource();
        mapLayerSource.removeMapLayer(mapboxMap, LAYER_ID);
        mapLayerSource.removeMapLayer(mapboxMap, K_LAYER_ID);
        mapLayerSource.removeMapLayer(mapboxMap, CLUSTER_LAYER_ID);


        mapLayerSource.addMapSource(mapboxMap, jsonStr, K_SOURCE_ID);
        mapLayerSource.addMapLayer(mapboxMap, K_LAYER_ID, K_SOURCE_ID, stops);


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
                    if (!properties.getString("approved").equals("centroid")) {
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

            Toast.makeText(context, "Extracting of Latitude and Longitude Finished", Toast.LENGTH_SHORT).show();

        }
    }





    //Calculate the convex hull
    public void calCluster(){
        HashMap<Integer, ArrayList<String>> hashMap = new HashMap<>();
        JSONObject featureCollection = new JSONObject();
        JSONArray features = new JSONArray();
        try {
            File file = new File(context.getFilesDir(), "kmeans_lat_lon_file");
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            hashMap = (HashMap<Integer, ArrayList<String>>) objectInputStream.readObject();


            //create geojson root
            featureCollection.put("type", "FeatureCollection");


        }
        catch(Exception e){
            Log.e("read_error","Error Reading Latitude and Longitude");
        }

        for (Map.Entry<Integer, ArrayList<String>> entry : hashMap.entrySet()){

            List<LatLng> points = new ArrayList<>();
            for (String e : entry.getValue()){
                String[] coord = e.split(",");
                double lon = Double.parseDouble(coord[0]);
                double lat = Double.parseDouble(coord[1]);
                LatLng location = new LatLng(lat,lon);
                points.add(location);
            }

            QuickHullLatLng quickHullLatLng = new QuickHullLatLng();
            ArrayList<LatLng> hullLatLng = new ArrayList<>(quickHullLatLng.quickHull(points));

            JSONArray coord_arr_list = new JSONArray();
            JSONArray coord_arr_list2 = new JSONArray();

            //note: first and last coordinate points must be the same or else you will get malformed polygon

            try {
                for (LatLng coord_pt : hullLatLng) {
                    JSONArray coord_pt_json = new JSONArray();
                    coord_pt_json.put(coord_pt.getLongitude());
                    coord_pt_json.put(coord_pt.getLatitude());
                    coord_arr_list.put(coord_pt_json);
                }
                //fix the malformed polygon bug by making the first element same as the last
                coord_arr_list.put(coord_arr_list.getJSONArray(0));
            }
            catch(Exception ex){ }

            //geojson format need to have another array over another array
            coord_arr_list2.put(coord_arr_list);
            //create geojson features array
            features = buildGeojson(features, coord_arr_list2, entry.getKey());

        }

        try {
            featureCollection.put("features", features);
        }catch(Exception e){
            Log.e("json_error_features","Unable to add features array to collection");
        }

        if (mapLayerSource.mapSourceExist(mapboxMap,SOURCE_ID) == true) {
            drawCluster(featureCollection);
        }

    }

    public void drawCluster(JSONObject featureCollection){
        String featureCollection_str = featureCollection.toString();
        mapLayerSource.addMapSource(mapboxMap, featureCollection_str, CLUSTER_SOURCE_ID);
        mapLayerSource.addBorderLayer(mapboxMap, CLUSTER_LAYER_ID, CLUSTER_SOURCE_ID, stops);
        setClusterClickListener();
    }

    public JSONArray buildGeojson(JSONArray features, JSONArray coord_arr_list, Integer color_index){

        try {

            JSONObject feature = new JSONObject();
            JSONObject geometry = new JSONObject();
            JSONObject properties = new JSONObject();

            feature.put("type", "Feature");
            geometry.put("coordinates", coord_arr_list);
            geometry.put("type", "Polygon");
            feature.put("geometry", geometry);
            properties.put("circleID", color_index.toString());
            feature.put("properties", properties);
            features.put(feature);

        }
        catch (Exception e){
            Log.e("json_error_featureArr","Unable to add feature to features array");
        }

        return features;
    }


    public void setClusterClickListener(){
        clusterListener = new MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                PointF pointf = mapboxMap.getProjection().toScreenLocation(point);
                RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);
                List<Feature> featureList = mapboxMap.queryRenderedFeatures(rectF, CLUSTER_LAYER_ID);
                if (featureList.size() > 0) {
                    for (Feature feature : featureList) {
                        Log.d("Feature found with %1$s", feature.toJson());
                        //Toast.makeText(MainActivity.this, feature.toJson(), Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Do you want to download cluster?");
                        builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DownloadCluster downloadCluster = new DownloadCluster();
                                downloadCluster.downloadCluster(context, feature.toJson());
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                }
            }
        };
        mapboxMap.addOnMapClickListener(clusterListener);

    }


}
