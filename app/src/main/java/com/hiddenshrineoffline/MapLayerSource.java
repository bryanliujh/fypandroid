package com.hiddenshrineoffline;

import android.graphics.Color;

import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;

import static com.mapbox.mapboxsdk.style.expressions.Expression.color;
import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;

public class MapLayerSource {


    //exponential - rate of increase in size
    //first stop - smallest size
    //second stop - largest size
    public void addMapLayer(MapboxMap mapboxMap, String LAYER_ID, String SOURCE_ID, Expression.Stop[] stops){
        if (mapLayerExist(mapboxMap, LAYER_ID) == false) {
            CircleLayer circleLayer = new CircleLayer(LAYER_ID, SOURCE_ID);
            circleLayer.withProperties(
                    circleRadius(
                            interpolate(
                                    exponential(1.75f),
                                    zoom(),
                                    stop(12, 5f),
                                    stop(22, 180f)
                            ))
            );

            circleLayer.setProperties(
                    circleColor(
                            match(get("circleID"), color(Color.parseColor("#000000")), stops)
                    )
            );

            mapboxMap.addLayer(circleLayer);
        }
    }

    public void addBorderLayer(MapboxMap mapboxMap, String CLUSTER_LAYER_ID, String CLUSTER_SOURCE_ID, Expression.Stop[] stops){
        if (mapLayerExist(mapboxMap, CLUSTER_LAYER_ID) == false) {
            // Create and style a FillLayer that uses the Polygon Feature's coordinates in the GeoJSON data
            FillLayer borderOutlineLayer = new FillLayer(CLUSTER_LAYER_ID, CLUSTER_SOURCE_ID);
            borderOutlineLayer.setProperties(
                    PropertyFactory.fillColor(match(get("circleID"), color(Color.parseColor("#000000")), stops)),
                    PropertyFactory.fillOpacity(.6f)
            );

            mapboxMap.addLayer(borderOutlineLayer);
        }
    }

    public void removeMapLayer(MapboxMap mapboxMap, String LAYER_ID){
        if (mapLayerExist(mapboxMap, LAYER_ID) == true) {
            mapboxMap.removeLayer(LAYER_ID);
        }

    }

    public boolean mapLayerExist(MapboxMap mapboxMap, String LAYER_ID){
        if (mapboxMap.getLayer(LAYER_ID) != null){
            return true;
        }
        else{
            return false;
        }
    }


    public void addMapSource(MapboxMap mapboxMap, String jsonStr, String SOURCE_ID){
        if (mapSourceExist(mapboxMap, SOURCE_ID) == false) {
            FeatureCollection featureCollection_obj = FeatureCollection.fromJson(jsonStr);
            Source source = new GeoJsonSource(SOURCE_ID, featureCollection_obj);
            mapboxMap.addSource(source);
        }
    }


    public void removeMapSource(MapboxMap mapboxMap, String SOURCE_ID){
        if (mapSourceExist(mapboxMap, SOURCE_ID) == true) {
            mapboxMap.removeSource(SOURCE_ID);
        }
    }

    public boolean mapSourceExist(MapboxMap mapboxMap, String SOURCE_ID){
        if (mapboxMap.getSource(SOURCE_ID) != null) {
            return true;
        }
        else{
            return false;
        }
    }


}
