package com.hiddenshrineoffline;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class NearestShrine {
    private Location currentLocation;
    private Location closestLocation;
    private String closestName;
    private String closestStatus;
    private String closestSize;
    private String closestMaterials;
    private String closestDeity;
    private String closestReligion;
    private String closestOfferings;
    private String closestImageURL;
    private String closestShrineUID;
    private float smallestDistance;
    private String jsonStr;

    public NearestShrine(Context context) {
        GPSTracker gpsTracker = new GPSTracker(context);
        currentLocation = new Location("");
        closestLocation = new Location("");

        closestShrineUID = null;
        smallestDistance = -1;
        jsonStr = null;

        FileManager fileManager = new FileManager();

        currentLocation.setLatitude(gpsTracker.getLatitude());
        currentLocation.setLongitude(gpsTracker.getLongitude());
        jsonStr = (String) fileManager.readObjectFile("mapjson", context);

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray features = jsonObject.getJSONArray("features");
            for (int i=0; i<features.length(); i++){
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                String name = properties.getString("name");
                String status = properties.getString("status");
                String size = properties.getString("size");
                String materials = properties.getString("materials");
                String deity = properties.getString("deity");
                String religion = properties.getString("religion");
                String offerings = properties.getString("offerings");
                String imageURL = properties.getString("imageURL");
                String shrineUID = properties.getString("shrineUUID");


                JSONObject geometry = feature.getJSONObject("geometry");
                JSONArray coord_arr = geometry.getJSONArray("coordinates");
                double lon = (double) coord_arr.get(0);
                double lat = (double) coord_arr.get(1);
                Location location = new Location("");
                location.setLongitude(lon);
                location.setLatitude(lat);
                float distance = currentLocation.distanceTo(location);
                if (smallestDistance == -1 || distance < smallestDistance) {
                    closestLocation = location;
                    closestName = name;
                    closestStatus = status;
                    closestSize = size;
                    closestMaterials = materials;
                    closestDeity = deity;
                    closestReligion = religion;
                    closestOfferings = offerings;
                    closestImageURL = imageURL;
                    closestShrineUID = shrineUID;
                    smallestDistance = distance;
                }

            }


        }catch(Exception e){
            Log.e("json_error","Error Calculating Closest Location");
        }

        Intent shrine_detail = new Intent(context, ShrineDetailActivity.class);
        shrine_detail.putExtra("name",closestName);
        shrine_detail.putExtra("status",closestStatus);
        shrine_detail.putExtra("size",closestSize);
        shrine_detail.putExtra("materials",closestMaterials);
        shrine_detail.putExtra("deity",closestDeity);
        shrine_detail.putExtra("religion", closestReligion);
        shrine_detail.putExtra("offerings",closestOfferings);
        shrine_detail.putExtra("imageURL", closestImageURL);
        shrine_detail.putExtra("shrineUUID", closestShrineUID);

        context.startActivity(shrine_detail);


        //Log.i("hi", String.valueOf(closestLocation.getLatitude()) + ":" + String.valueOf(closestLocation.getLongitude()) + ":" + closestShrineUID);




    }
}
