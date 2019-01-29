package com.hiddenshrineoffline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.mapbox.geojson.Feature;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.style.expressions.Expression;

import org.json.JSONArray;
import org.json.JSONObject;

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
    private String[] colorArr;


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

    private String[] northernSR = {"8.55919, 79.91959",
            "8.52099, 80.04954",
            "8.59721, 80.0631",
            "8.63659, 80.13177",
            "8.62301, 80.24163",
            "8.55376, 80.31441",
            "8.61486, 80.39406",
            "8.64066, 80.51423",
            "8.71773, 80.59358",
            "8.79475, 80.64679",
            "8.87482, 80.68662",
            "8.91688, 80.75254",
            "8.95079, 80.8475",
            "8.97724, 80.90175",
            "8.97928, 80.96938",
            "9.08426, 80.9096",
            "9.18348, 80.85724",
            "9.26683, 80.82318",
            "9.32915, 80.76806",
            "9.3925, 80.69596",
            "9.459, 80.6228",
            "9.5008, 80.56451",
            "9.56219, 80.49302",
            "9.62252, 80.41368",
            "9.67397, 80.35326",
            "9.74571, 80.30382",
            "9.79917, 80.26674",
            "9.83502, 80.22005",
            "9.82758, 80.17061",
            "9.81452, 80.10577",
            "9.82061, 80.03436",
            "9.8108, 79.97291",
            "9.77866, 79.91266",
            "9.76241, 79.8627",
            "9.70908, 79.79052",
            "9.65561, 79.74486",
            "9.55139, 79.65038",
            "9.48164, 79.65313",
            "9.46674, 79.72591",
            "9.5006, 79.82342",
            "9.50901, 79.91101",
            "9.47041, 79.99821",
            "9.38709, 80.04818",
            "9.33862, 79.99157",
            "9.27628, 79.96823",
            "9.23812, 80.04412",
            "9.20617, 80.1029",
            "9.13278, 80.07182",
            "9.06401, 80.05694",
            "9.02226, 80.01082",
            "9.0148, 79.94855",
            "9.06566, 79.884",
            "9.09888, 79.80847",
            "9.11322, 79.72446",
            "9.08542, 79.68327",
            "9.04948, 79.77596",
            "9.00264, 79.84222",
            "8.93494, 79.89453",
            "8.86065, 79.92166",
            "8.78805, 79.9195",
            "8.70716, 79.94673",
            "8.62515, 79.9299"};


    private String[] northCentralSR = {"8.52099, 80.04954",
            "8.59721, 80.0631",
            "8.63659, 80.13177",
            "8.62301, 80.24163",
            "8.55376, 80.31441",
            "8.61486, 80.39406",
            "8.64066, 80.51423",
            "8.71773, 80.59358",
            "8.79475, 80.64679",
            "8.87482, 80.68662",
            "8.91688, 80.75254",
            "8.88374, 80.86116",
            "8.78332, 80.88863",
            "8.66523, 80.90785",
            "8.55244, 80.94622",
            "8.44583, 80.97026",
            "8.34937, 80.93592",
            "8.27191, 81.02244",
            "8.25244, 81.12528",
            "8.19805, 81.21785",
            "8.1396, 81.26866",
            "8.06482, 81.30986",
            "7.93539, 81.34973",
            "7.86738, 81.23849",
            "7.73949, 81.24398",
            "7.68647, 81.12384",
            "7.71369, 81.00573",
            "7.69132, 80.88763",
            "7.67339, 80.76501",
            "7.78906, 80.78836",
            "7.87347, 80.8148",
            "7.9678, 80.81303",
            "8.02084, 80.69218",
            "7.92564, 80.61527",
            "7.83392, 80.57391",
            "7.80185, 80.50294",
            "7.90576, 80.47874",
            "7.97946, 80.43069",
            "8.04881, 80.38263",
            "8.11487, 80.29018",
            "8.15326, 80.19346",
            "8.19608, 80.1066",
            "8.23021, 80.0139",
            "8.32684, 79.98266",
            "8.39842, 80.00223",
            "8.46112, 80.04857"};

    private String[] northWesternSR = {"7.80185, 80.50294",
        "7.90576, 80.47874",
        "7.97946, 80.43069",
        "8.04881, 80.38263",
        "8.11487, 80.29018",
        "8.15326, 80.19346",
        "8.19608, 80.1066",
        "8.23021, 80.0139",
        "8.32684, 79.98266",
        "8.39842, 80.00223",
        "8.46112, 80.04857",
        "8.52099, 80.04954",
        "8.55919, 79.91959",
        "8.54129, 79.87511",
        "8.53568, 79.78643",
        "8.48504, 79.77313",
        "8.37463, 79.76972",
        "8.29362, 79.75101",
        "8.24288, 79.73451",
        "8.21437, 79.69525",
        "8.16247, 79.70249",
        "8.09882, 79.69956",
        "8.02846, 79.71329",
        "7.9041, 79.74963",
        "7.78506, 79.77984",
        "7.60753, 79.78476",
        "7.50981, 79.79139",
        "7.42212, 79.81065",
        "7.35811, 79.82438",
        "7.27719, 79.84116",
        "7.27108, 79.9147",
        "7.28632, 79.98027",
        "7.28379, 80.07285",
        "7.3221, 80.13379",
        "7.28224, 80.17278",
        "7.25078, 80.20813",
        "7.3, 80.27645",
        "7.31348, 80.35292",
        "7.34527, 80.42974",
        "7.36433, 80.47257",
        "7.37897, 80.51754",
        "7.41745, 80.55359",
        "7.47396, 80.59152",
        "7.54047, 80.5673",
        "7.62566, 80.56515",
        "7.72536, 80.53119"};


    private String[] easternSR = {"8.88374, 80.86116",
        "8.78332, 80.88863",
        "8.66523, 80.90785",
        "8.55244, 80.94622",
        "8.44583, 80.97026",
        "8.34937, 80.93592",
        "8.27191, 81.02244",
        "8.25244, 81.12528",
        "8.19805, 81.21785",
        "8.1396, 81.26866",
        "8.06482, 81.30986",
        "7.93539, 81.34973",
        "7.86738, 81.23849",
        "7.73949, 81.24398",
        "7.68647, 81.12384",
        "7.71369, 81.00573",
        "7.60319, 80.98648",
        "7.51062, 81.09154",
        "7.40489, 81.20578",
        "7.24961, 81.29161",
        "7.35722, 81.37813",
        "7.32181, 81.48524",
        "7.16787, 81.51133",
        "7.08474, 81.52507",
        "7.04658, 81.61158",
        "6.94299, 81.62394",
        "6.57205, 81.60609",
        "6.50383, 81.71115",
        "6.59934, 81.76951",
        "6.76028, 81.83131",
        "7.02229, 81.88809",
        "7.33567, 81.87024",
        "7.49364, 81.81943",
        "7.61616, 81.7851",
        "7.74409, 81.71369",
        "7.83253, 81.61893",
        "7.99032, 81.55576",
        "8.1059, 81.461",
        "8.19374, 81.42504",
        "8.33644, 81.38934",
        "8.47605, 81.35797",
        "8.55527, 81.30016",
        "8.62996, 81.2363",
        "8.72779, 81.1854",
        "8.79803, 81.11742",
        "8.86588, 81.05356",
        "8.92309, 81.01706",
        "8.97928, 80.96938",
        "8.97724, 80.90175",
        "8.95079, 80.8475",
        "8.91688, 80.75254"};

    private String[] uvaSR = {"7.60319, 80.98648",
            "7.51062, 81.09154",
            "7.40489, 81.20578",
            "7.24961, 81.29161",
            "7.35722, 81.37813",
            "7.32181, 81.48524",
            "7.16787, 81.51133",
            "7.08474, 81.52507",
            "7.04658, 81.61158",
            "6.94299, 81.62394",
            "6.57205, 81.60609",
            "6.50401, 81.49326",
            "6.40735, 81.47283",
            "6.37664, 81.3458",
            "6.3473, 81.23525",
            "6.38833, 81.16101",
            "6.41, 81.06388",
            "6.35063, 81.01582",
            "6.29433, 80.92621",
            "6.37554, 80.85823",
            "6.49017, 80.83626",
            "6.57476, 80.87059",
            "6.69003, 80.94475",
            "6.77869, 80.80726",
            "6.85676, 80.83541",
            "6.91383, 80.77368",
            "6.96701, 80.86505",
            "6.98779, 80.92444",
            "7.07639, 80.96633",
            "7.19289, 80.95191",
            "7.22287, 81.02057",
            "7.31716, 80.99039",
            "7.43701, 80.97872"};

    private String[] southernSR = {"6.50383, 81.71115",
        "6.57205, 81.60609",
        "6.50401, 81.49326",
        "6.40735, 81.47283",
        "6.37664, 81.3458",
        "6.3473, 81.23525",
        "6.38833, 81.16101",
        "6.41, 81.06388",
        "6.35063, 81.01582",
        "6.29433, 80.92621",
        "6.27463, 80.85216",
        "6.27076, 80.78342",
        "6.31199, 80.68921",
        "6.39183, 80.62535",
        "6.37273, 80.52648",
        "6.38466, 80.44586",
        "6.42816, 80.37942",
        "6.41874, 80.35329",
        "6.40406, 80.32212",
        "6.38564, 80.29757",
        "6.38019, 80.24704",
        "6.35955, 80.21477",
        "6.36143, 80.16928",
        "6.38036, 80.12413",
        "6.40732, 80.06868",
        "6.43359, 80.03607",
        "6.42185, 79.99401",
        "6.27082, 80.02683",
        "6.18481, 80.0646",
        "6.09279, 80.12928",
        "6.00319, 80.23774",
        "5.96802, 80.36649",
        "5.92952, 80.493",
        "5.91494, 80.58683",
        "5.95916, 80.7223",
        "6.04046, 80.8627",
        "6.1066, 81.09408",
        "6.17999, 81.26351",
        "6.25849, 81.41783",
        "6.36974, 81.56666",
        "6.44428, 81.65592"};


    private String[] westernSR = {"7.27719, 79.84116",
            "7.27108, 79.9147",
            "7.28632, 79.98027",
            "7.28379, 80.07285",
            "7.3221, 80.13379",
            "7.28224, 80.17278",
            "7.25078, 80.20813",
            "7.18956, 80.18447",
            "7.13165, 80.17589",
            "7.06317, 80.18241",
            "6.97923, 80.19539",
            "6.8504, 80.19127",
            "6.68198, 80.23659",
            "6.52305, 80.3183",
            "6.42816, 80.37942",
            "6.41874, 80.35329",
            "6.40406, 80.32212",
            "6.38564, 80.29757",
            "6.38019, 80.24704",
            "6.35955, 80.21477",
            "6.36143, 80.16928",
            "6.38036, 80.12413",
            "6.40732, 80.06868",
            "6.43359, 80.03607",
            "6.42185, 79.99401",
            "6.50441, 79.97032",
            "6.59245, 79.94992",
            "6.73573, 79.89159",
            "6.8334, 79.86006",
            "6.86657, 79.85745",
            "6.90139, 79.84953",
            "6.9218, 79.83415",
            "6.94322, 79.82128",
            "6.96596, 79.82016",
            "6.96647, 79.85088",
            "6.98164, 79.86874",
            "7.03582, 79.85706",
            "7.09525, 79.84471",
            "7.14806, 79.82926",
            "7.20488, 79.81465"};


    private String[] sabaragamuwaSR = {"7.34527, 80.42974",
            "7.31348, 80.35292",
            "7.3, 80.27645",
            "7.25078, 80.20813",
            "7.18956, 80.18447",
            "7.13165, 80.17589",
            "7.06317, 80.18241",
            "6.97923, 80.19539",
            "6.8504, 80.19127",
            "6.68198, 80.23659",
            "6.52305, 80.3183",
            "6.42816, 80.37942",
            "6.38466, 80.44586",
            "6.37273, 80.52648",
            "6.39183, 80.62535",
            "6.31199, 80.68921",
            "6.27076, 80.78342",
            "6.27463, 80.85216",
            "6.29433, 80.92621",
            "6.37554, 80.85823",
            "6.49017, 80.83626",
            "6.57476, 80.87059",
            "6.69003, 80.94475",
            "6.77869, 80.80726",
            "6.76274, 80.76187",
            "6.752, 80.70745",
            "6.75183, 80.64463",
            "6.76138, 80.57064",
            "6.77314, 80.49923",
            "6.84302, 80.50867",
            "6.90438, 80.45293",
            "6.99224, 80.44636",
            "7.03918, 80.45846",
            "7.1079, 80.45354",
            "7.13311, 80.5023",
            "7.16394, 80.55568",
            "7.20917, 80.51929",
            "7.25199, 80.49905",
            "7.29896, 80.45852"};

    private String[] centralSR = {"6.76274, 80.76187",
            "6.752, 80.70745",
            "6.75183, 80.64463",
            "6.76138, 80.57064",
            "6.77314, 80.49923",
            "6.84302, 80.50867",
            "6.90438, 80.45293",
            "6.99224, 80.44636",
            "7.03918, 80.45846",
            "7.1079, 80.45354",
            "7.13311, 80.5023",
            "7.16394, 80.55568",
            "7.20917, 80.51929",
            "7.25199, 80.49905",
            "7.29896, 80.45852",
            "7.34527, 80.42974",
            "7.36433, 80.47257",
            "7.37897, 80.51754",
            "7.41745, 80.55359",
            "7.47396, 80.59152",
            "7.54047, 80.5673",
            "7.62566, 80.56515",
            "7.72536, 80.53119",
            "7.80185, 80.50294",
            "7.83392, 80.57391",
            "7.92564, 80.61527",
            "8.02084, 80.69218",
            "7.9678, 80.81303",
            "7.87347, 80.8148",
            "7.78906, 80.78836",
            "7.67339, 80.76501",
            "7.69132, 80.88763",
            "7.71369, 81.00573",
            "7.60319, 80.98648",
            "7.43701, 80.97872",
            "7.31716, 80.99039",
            "7.22287, 81.02057",
            "7.19289, 80.95191",
            "7.07639, 80.96633",
            "6.98779, 80.92444",
            "6.96701, 80.86505",
            "6.91383, 80.77368",
            "6.85676, 80.83541",
            "6.77869, 80.80726"};


    private String[][] regionArray = {northSG, southSG, westSG, eastSG, northernSR, northCentralSR,
            northWesternSR, easternSR, uvaSR, southernSR, westernSR, sabaragamuwaSR, centralSR};



    public MapboxMap.OnMapClickListener initClusterCreation(Context context, MapboxMap mapboxMap, Expression.Stop[] stops, String jsonFileName, String CHANGE_SOURCE_ID, String CHANGE_LAYER_ID){
        this.context = context;
        this.mapboxMap = mapboxMap;
        this.stops = stops;
        this.jsonFileName = jsonFileName;
        this.CHANGE_SOURCE_ID = CHANGE_SOURCE_ID;
        this.CHANGE_LAYER_ID = CHANGE_LAYER_ID;
        mapLayerSource = new MapLayerSource();
        this.colorArr = context.getResources().getStringArray(R.array.colorArr);

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


        for (int i = 0; i<regionArray.length; i++){
            features = createFeatures(features, regionArray[i], i);
        }

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








    public void setClusterClickListener(){
        String jsonStr;

        FileManager fileManager = new FileManager();
        jsonStr = (String) fileManager.readObjectFile("mapjson", context);


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

                        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View v = layoutInflater.inflate(R.layout.download_dialog, null);
                        GradientDrawable gd = (GradientDrawable) v.findViewById(R.id.cluster_color).getBackground();
                        gd.setColor(Color.parseColor(colorArr[Integer.parseInt(feature.getStringProperty("circleID"))]));

                        builder.setView(v);
                        builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DownloadCluster downloadCluster = new DownloadCluster();
                                downloadCluster.downloadRegion(context, jsonStr, regionArray[Integer.parseInt(feature.getStringProperty("circleID"))]);

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

