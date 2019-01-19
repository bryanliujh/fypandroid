package com.hiddenshrineoffline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.expressions.Expression;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManualClusterDownload {

    private MapboxMap mapboxMap;
    private Context context;
    private MapLayerSource mapLayerSource;
    private String CHANGE_SOURCE_ID;
    private String CHANGE_LAYER_ID;
    private String jsonFileName;
    private Expression.Stop[] stops;
    private MapboxMap.OnMapClickListener clusterListener;

    private String[] northSG = {"1.42896, 103.73795", "1.40618, 103.72653", "1.37563, 103.73477", "1.34612, 103.74163", "1.31419, 103.74678", "1.33698, 103.77533", "1.35826, 103.80417", "1.37851, 103.82648",
            "1.41283, 103.84571", "1.43548, 103.86391", "1.46191, 103.83884", "1.46912, 103.80657", "1.45745, 103.78528", "1.43926, 103.76331"};


    private String[] southSG = {"1.31419, 103.74678",
        "1.28661, 103.74254",
        "1.26326, 103.76347",
        "1.25297, 103.80673",
        "1.23813, 103.83129",
        "1.26387, 103.86116",
        "1.29854, 103.8948",
        "1.32805, 103.86597",
        "1.35036, 103.84434",
        "1.37851, 103.82648",
        "1.35826, 103.80417",
        "1.33698, 103.77533"};


    private String[] westSG = {"1.42896, 103.73795",
            "1.40618, 103.72653",
            "1.37563, 103.73477",
            "1.34612, 103.74163",
            "1.31419, 103.74678",
            "1.28661, 103.74254",
            "1.2691, 103.73086",
            "1.25434, 103.70958",
            "1.22826, 103.67936",
            "1.20526, 103.64091",
            "1.22105, 103.60383",
            "1.2691, 103.60692",
            "1.30012, 103.61701",
            "1.34268, 103.63417",
            "1.3746, 103.65306",
            "1.40137, 103.66164",
            "1.43295, 103.68636",
            "1.44668, 103.71073",
            "1.44874, 103.72893"};


    private String[] eastSG = {"1.43548, 103.86391",
        "1.41283, 103.84571",
        "1.37851, 103.82648",
        "1.35036, 103.84434",
        "1.32805, 103.86597",
        "1.29854, 103.8948",
        "1.30548, 103.92854",
        "1.312, 103.96287",
        "1.31406, 103.99205",
        "1.31852, 104.02089",
        "1.34255, 104.02982",
        "1.36409, 104.04313",
        "1.37988, 104.06785",
        "1.39018, 104.08588",
        "1.41764, 104.08149",
        "1.43377, 104.05591",
        "1.4293, 104.01505",
        "1.41935, 103.97317",
        "1.42725, 103.92922",
        "1.42527, 103.88768"};


    public MapboxMap.OnMapClickListener initClusterCreation(Context context, MapboxMap mapboxMap, Expression.Stop[] stops, String jsonFileName, String CHANGE_SOURCE_ID, String CHANGE_LAYER_ID){
        this.context = context;
        this.mapboxMap = mapboxMap;
        this.stops = stops;
        this.jsonFileName = jsonFileName;
        this.CHANGE_SOURCE_ID = CHANGE_SOURCE_ID;
        this.CHANGE_LAYER_ID = CHANGE_LAYER_ID;
        mapLayerSource = new MapLayerSource();
        calCluster();


        //check if coordinate is in region
        /*
        LatLng coord = new LatLng();
        coord.setLatitude(1.33784);
        coord.setLongitude(103.76232);
        coordinateInRegion(eastSG,coord);
        */

        return clusterListener;
    }

    //Calculate the convex hull
    public void calCluster(){

        JSONObject featureCollection = new JSONObject();
        JSONArray features = new JSONArray();
        try {

            //create geojson root
            featureCollection.put("type", "FeatureCollection");


        }
        catch(Exception e){
            Log.e("read_error","Error Reading Latitude and Longitude");
        }

        features = createFeatures(features, northSG, 1);
        features = createFeatures(features, southSG, 2);
        features = createFeatures(features, eastSG, 3);
        features = createFeatures(features, westSG, 4);

        try {
            featureCollection.put("features", features);
        }catch(Exception e){
            Log.e("json_error_features","Unable to add features array to collection");
        }

        drawCluster(featureCollection);


    }


    public JSONArray createFeatures(JSONArray features, String[] regionCoord, Integer color_index){


        JSONArray coord_arr_list = new JSONArray();
        JSONArray coord_arr_list2 = new JSONArray();

        //note: first and last coordinate points must be the same or else you will get malformed polygon

        try {

            for (String e : regionCoord){
                String[] coord = e.split(",");
                double lat = Double.parseDouble(coord[0]);
                double lon = Double.parseDouble(coord[1]);
                JSONArray coord_pt_json = new JSONArray();
                coord_pt_json.put(lon);
                coord_pt_json.put(lat);
                coord_arr_list.put(coord_pt_json);
            }

            //fix the malformed polygon bug by making the first element same as the last
            coord_arr_list.put(coord_arr_list.getJSONArray(0));
        }
        catch(Exception ex){ }

        //geojson format need to have another array over another array
        coord_arr_list2.put(coord_arr_list);
        //create geojson features array
        features = buildGeojson(features, coord_arr_list2, color_index);

        return features;
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



    public void coordinateInRegion(String[] regionCoord, LatLng coord) {
        int i, j;
        boolean isInside = false;
        ArrayList<LatLng> regionCoordList = new ArrayList<>();

        for (String e: regionCoord){
            String[] coordStr = e.split(",");
            double lat = Double.parseDouble(coordStr[0]);
            double lon = Double.parseDouble(coordStr[1]);
            LatLng location = new LatLng(lat,lon);
            regionCoordList.add(location);
        }
        int sides = regionCoordList.size();
        for (i = 0, j = sides - 1; i < sides; j = i++) {
            //verifying if your coordinate is inside your region

            if (
                    (
                            (
                                    (regionCoordList.get(i).getLongitude() <= coord.getLongitude()) && (coord.getLongitude() < regionCoordList.get(j).getLongitude())
                            ) || (
                                    (regionCoordList.get(j).getLongitude() <= coord.getLongitude()) && (coord.getLongitude() < regionCoordList.get(i).getLongitude())
                            )
                    ) &&
                            (coord.getLatitude() < (regionCoordList.get(j).getLatitude() - regionCoordList.get(i).getLatitude()) * (coord.getLongitude() - regionCoordList.get(i).getLongitude()) / (regionCoordList.get(j).getLongitude() - regionCoordList.get(i).getLongitude()) + regionCoordList.get(i).getLatitude())
            ) {
                isInside = !isInside;
            }
        }


        Toast.makeText(context, String.valueOf(isInside),Toast.LENGTH_SHORT).show();
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

