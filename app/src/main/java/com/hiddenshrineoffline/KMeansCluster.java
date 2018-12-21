package com.hiddenshrineoffline;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.expressions.Expression;

import static com.mapbox.mapboxsdk.style.expressions.Expression.color;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;


public class KMeansCluster {
    private Context context;
    private Expression.Stop[] stops;
    private int KMEANS_CLUSTER_NUM;
    private String SOURCE_ID;
    private String LAYER_ID;
    private String K_SOURCE_ID;
    private String K_LAYER_ID;

    public MapboxMap.OnMapClickListener initKMeans(Context context, MapboxMap mapboxMap, String[] colorArr){
        this.context = context;

        getStringResource();

        stops = new Expression.Stop[KMEANS_CLUSTER_NUM];
        for (int i=0; i<KMEANS_CLUSTER_NUM; i++){
            stops[i] = stop(String.valueOf(i),color(Color.parseColor(colorArr[i])));
        }

        ClusterCreation clusterCreation = new ClusterCreation();
        return clusterCreation.initClusterCreation(context, mapboxMap, stops, "kmeans_lat_lon_file", "kmeansjson", SOURCE_ID, LAYER_ID, K_SOURCE_ID, K_LAYER_ID);
        //mapLayerSource.addMapSource(mapboxMap, jsonStr, K_SOURCE_ID);
        //mapLayerSource.addMapLayer(mapboxMap, K_LAYER_ID, K_SOURCE_ID, stops);


    }

    public void getStringResource(){
        Resources res = context.getResources();
        KMEANS_CLUSTER_NUM = Integer.parseInt(res.getString(R.string.KMEANS_CLUSTER_NUM));
        SOURCE_ID = res.getString(R.string.SOURCE_ID);
        LAYER_ID = res.getString(R.string.LAYER_ID);
        K_SOURCE_ID = res.getString(R.string.K_SOURCE_ID);
        K_LAYER_ID = res.getString(R.string.K_LAYER_ID);
    }



}
