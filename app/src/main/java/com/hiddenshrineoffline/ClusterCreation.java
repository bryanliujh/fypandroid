package com.hiddenshrineoffline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.expressions.Expression;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClusterCreation {
    private MapboxMap mapboxMap;
    private Context context;
    private MapLayerSource mapLayerSource;
    private String SOURCE_ID;
    private String LAYER_ID;
    private String CHANGE_SOURCE_ID;
    private String CHANGE_LAYER_ID;
    private String lat_lon_filename;
    private String jsonFileName;
    private Expression.Stop[] stops;
    private MapboxMap.OnMapClickListener clusterListener;


    public MapboxMap.OnMapClickListener initClusterCreation(Context context, MapboxMap mapboxMap, Expression.Stop[] stops, String lat_lon_filename, String jsonFileName, String SOURCE_ID, String LAYER_ID, String CHANGE_SOURCE_ID, String CHANGE_LAYER_ID){
        this.context = context;
        this.mapboxMap = mapboxMap;
        this.stops = stops;
        this.lat_lon_filename = lat_lon_filename;
        this.jsonFileName = jsonFileName;
        this.SOURCE_ID = SOURCE_ID;
        this.LAYER_ID = LAYER_ID;
        this.CHANGE_SOURCE_ID = CHANGE_SOURCE_ID;
        this.CHANGE_LAYER_ID = CHANGE_LAYER_ID;
        mapLayerSource = new MapLayerSource();
        calCluster();

        return clusterListener;
    }

    //Calculate the convex hull
    public void calCluster(){
        HashMap<Integer, ArrayList<String>> hashMap = new HashMap<>();
        JSONObject featureCollection = new JSONObject();
        JSONArray features = new JSONArray();
        try {
            FileManager fileManager = new FileManager();
            hashMap = (HashMap<Integer, ArrayList<String>>) fileManager.readObjectFile(lat_lon_filename, context);

            //create geojson root
            featureCollection.put("type", "FeatureCollection");


        }
        catch(Exception e){
            Log.e("read_error","Error Reading Latitude and Longitude");
        }

        for (Map.Entry<Integer, ArrayList<String>> entry : hashMap.entrySet()){

            List<LatLng> points = new ArrayList<>();
            //each entry corresponds to a cluster of points
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
        mapLayerSource.addMapSource(mapboxMap, featureCollection_str, CHANGE_SOURCE_ID);
        mapLayerSource.addBorderLayer(mapboxMap, CHANGE_LAYER_ID, CHANGE_SOURCE_ID, stops);
        setClusterClickListener();
    }

    public JSONArray buildGeojson(JSONArray features, JSONArray coord_arr_list, Integer color_index){

        //color index refers to cluster index as well
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
                List<Feature> featureList = mapboxMap.queryRenderedFeatures(rectF, CHANGE_LAYER_ID);
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
                                downloadCluster.downloadCluster(context, jsonFileName, feature.toJson());
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
