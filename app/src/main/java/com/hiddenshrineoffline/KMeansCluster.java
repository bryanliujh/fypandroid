package com.hiddenshrineoffline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private String SOURCE_ID;
    private String LAYER_ID;
    private String K_SOURCE_ID;
    private String K_LAYER_ID;
    private CircleLayer circleLayer;
    private Expression.Stop[] stops;
    private int KMEANS_CLUSTER_NUM;

    public void initKMeans(Context context, MapboxMap mapboxMap, String[] colorArr, String jsonStr){
        this.jsonStr = jsonStr;
        this.context = context;
        this.mapboxMap = mapboxMap;

        getStringResource();

        stops = new Expression.Stop[KMEANS_CLUSTER_NUM];
        for (int i=0; i<KMEANS_CLUSTER_NUM; i++){
            stops[i] = stop(String.valueOf(i),color(Color.parseColor(colorArr[i])));
        }

        mapLayerSource = new MapLayerSource();
        calCluster();
        //mapLayerSource.addMapSource(mapboxMap, jsonStr, K_SOURCE_ID);
        //mapLayerSource.addMapLayer(mapboxMap, K_LAYER_ID, K_SOURCE_ID, stops);


    }

    public void getStringResource(){
        Resources res = context.getResources();
        SOURCE_ID = res.getString(R.string.SOURCE_ID);
        LAYER_ID = res.getString(R.string.LAYER_ID);
        K_SOURCE_ID = res.getString(R.string.K_SOURCE_ID);
        K_LAYER_ID = res.getString(R.string.K_LAYER_ID);
        KMEANS_CLUSTER_NUM = Integer.parseInt(res.getString(R.string.KMEANS_CLUSTER_NUM));
    }


    //Calculate the convex hull
    public void calCluster(){
        HashMap<Integer, ArrayList<String>> hashMap = new HashMap<>();
        JSONObject featureCollection = new JSONObject();
        JSONArray features = new JSONArray();
        try {
            FileManager fileManager = new FileManager();
            hashMap = (HashMap<Integer, ArrayList<String>>) fileManager.readObjectFile("kmeans_lat_lon_file", context);

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
        mapLayerSource.addMapSource(mapboxMap, featureCollection_str, K_SOURCE_ID);
        mapLayerSource.addBorderLayer(mapboxMap, K_LAYER_ID, K_SOURCE_ID, stops);
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
                List<Feature> featureList = mapboxMap.queryRenderedFeatures(rectF, K_LAYER_ID);
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
