package com.hiddenshrineoffline;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DownloadCluster {

    private Context context;
    private FileManager fileManager;
    private String jsonStr;
    private ArrayList<ShrineEntity> shrineArrayList;
    private ArrayList<String> newUidList;
    private String cluster_id;
    private String jsonFileName;
    private String[] regionCoord;

    public void downloadCluster(Context context, String jsonFileName, String featureJSON){
        try {
            this.context = context;
            this.jsonFileName = jsonFileName;
            JSONObject featureObj = new JSONObject(featureJSON);
            JSONObject properties = featureObj.getJSONObject("properties");
            cluster_id = properties.getString("circleID");

            new ImageSaveInBackground().execute();

        }
        catch (Exception e) {

        }
    }

    public void downloadClusterList(Context context, String jsonFileName, String cluster_id){
        try {
            this.context = context;
            this.jsonFileName = jsonFileName;
            this.cluster_id = cluster_id;

            new ImageSaveInBackground().execute();

        }
        catch (Exception e) {

        }
    }

    public void downloadRegion(Context context, String jsonStr, String[] regionCoord){
        try {
            this.context = context;
            this.jsonStr = jsonStr;
            this.regionCoord = regionCoord;

            new RegionImageSaveInBackground().execute();

        }catch(Exception e){

        }
    }


    private class RegionImageSaveInBackground extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected Void doInBackground(Void... arg0) {

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            NetworkConnection networkConnection = new NetworkConnection();
            if (networkConnection.isNetworkConnected(context)) {
                try {
                    String shrineUUID;
                    String name;
                    String status;
                    String size;
                    String materials;
                    String deity;
                    String religion;
                    String offerings;
                    String imageURL;

                    //use array difference to find the difference in array (for update only)
                    shrineArrayList = new ArrayList<ShrineEntity>();
                    newUidList = new ArrayList<String>();
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray features = jsonObj.getJSONArray("features");

                    for (int i = 0; i < features.length(); i++) {
                        JSONObject feature = features.getJSONObject(i);

                        //extract coords
                        JSONObject geometry = feature.getJSONObject("geometry");
                        JSONArray coord_arr = geometry.getJSONArray("coordinates");
                        double lon = (double) coord_arr.get(0);
                        double lat = (double) coord_arr.get(1);
                        LatLng coord = new LatLng();
                        coord.setLongitude(lon);
                        coord.setLatitude(lat);
                        if(coordinateInRegion(regionCoord, coord)) {

                            JSONObject properties = feature.getJSONObject("properties");
                            ShrineEntity shrineEntity = new ShrineEntity();
                            //get value from the json string
                            shrineUUID = properties.getString("shrineUUID");
                            name = properties.getString("name");
                            status = properties.getString("status");
                            size = properties.getString("size");
                            materials = properties.getString("materials");
                            deity = properties.getString("deity");
                            religion = properties.getString("religion");
                            offerings = properties.getString("offerings");
                            imageURL = properties.getString("imageURL");


                            //download image from imgur
                            ImageDownload imageDownload = new ImageDownload();
                            imageDownload.initPicasso(context, imageURL, shrineUUID);

                            //create new shrine entity and add in list
                            shrineEntity.setShrine_uid(shrineUUID);
                            shrineEntity.setShrine_name(name);
                            shrineEntity.setShrine_status(status);
                            shrineEntity.setShrine_size(size);
                            shrineEntity.setShrine_materials(materials);
                            shrineEntity.setShrine_deity(deity);
                            shrineEntity.setShrine_religion(religion);
                            shrineEntity.setShrine_offerings(offerings);
                            shrineEntity.setShrine_imageURL(imageURL);
                            shrineArrayList.add(shrineEntity);
                            newUidList.add(shrineUUID);
                        }

                    }
                }
                catch(Exception e){
                }
                Toast.makeText(context, "Images downloaded", Toast.LENGTH_SHORT).show();
                new SaveDBInBackground(AppDatabase.getDatabase(context)).execute();
            }
            else{
                Toast.makeText(context, "Network connection lost, please retry download", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private class ImageSaveInBackground extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected Void doInBackground(Void... arg0) {
            fileManager = new FileManager();
            jsonStr = (String) fileManager.readObjectFile(jsonFileName,context);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            NetworkConnection networkConnection = new NetworkConnection();
            if (networkConnection.isNetworkConnected(context)) {
                try {
                        String circleID;
                        String shrineUUID;
                        String name;
                        String status;
                        String size;
                        String materials;
                        String deity;
                        String religion;
                        String offerings;
                        String imageURL;

                        //use array difference to find the difference in array (for update only)
                        shrineArrayList = new ArrayList<ShrineEntity>();
                        newUidList = new ArrayList<String>();
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        JSONArray features = jsonObj.getJSONArray("features");
                        for (int i = 0; i < features.length(); i++) {
                            JSONObject feature = features.getJSONObject(i);
                            JSONObject properties = feature.getJSONObject("properties");
                            circleID = properties.getString("circleID");
                            if (cluster_id.equals(circleID)) {
                                ShrineEntity shrineEntity = new ShrineEntity();
                                //get value from the json string
                                shrineUUID = properties.getString("shrineUUID");
                                name = properties.getString("name");
                                status = properties.getString("status");
                                size = properties.getString("size");
                                materials = properties.getString("materials");
                                deity = properties.getString("deity");
                                religion = properties.getString("religion");
                                offerings = properties.getString("offerings");
                                imageURL = properties.getString("imageURL");


                                //download image from imgur
                                ImageDownload imageDownload = new ImageDownload();
                                imageDownload.initPicasso(context, imageURL, shrineUUID);

                                //create new shrine entity and add in list
                                shrineEntity.setShrine_uid(shrineUUID);
                                shrineEntity.setShrine_name(name);
                                shrineEntity.setShrine_status(status);
                                shrineEntity.setShrine_size(size);
                                shrineEntity.setShrine_materials(materials);
                                shrineEntity.setShrine_deity(deity);
                                shrineEntity.setShrine_religion(religion);
                                shrineEntity.setShrine_offerings(offerings);
                                shrineEntity.setShrine_imageURL(imageURL);
                                shrineEntity.setCluster_uid(Integer.parseInt(cluster_id));
                                shrineArrayList.add(shrineEntity);
                                newUidList.add(shrineUUID);

                            }
                        }
                }
                catch(Exception e){
                }
                Toast.makeText(context, "Images downloaded", Toast.LENGTH_SHORT).show();
                new SaveDBInBackground(AppDatabase.getDatabase(context)).execute();
            }
            else{
                Toast.makeText(context, "Network connection lost, please retry download", Toast.LENGTH_SHORT).show();
            }

        }
    }


   //TODO: change the DB To accomodate both kmeans and normal cluster

    private class SaveDBInBackground extends AsyncTask<Void, Void, Void>{
        private final AppDatabase mDB;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        SaveDBInBackground(AppDatabase db){
            mDB = db;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            List<ShrineEntity> shrineList = mDB.shrineDao().getAll();
            ArrayList<String> oldUidList = new ArrayList<String>();

            for (ShrineEntity shrine:shrineList){
                oldUidList.add(shrine.getShrine_uid());
            }

            //ensure only new uid gets added
            ArrayList<String> add = new ArrayList<String>(newUidList);
            add.removeAll(oldUidList);

            for (int i = 0; i<shrineArrayList.size(); i++) {
                if (add.contains(shrineArrayList.get(i).getShrine_uid())) {
                    mDB.shrineDao().insertAll(shrineArrayList.get(i));
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }


    public boolean coordinateInRegion(String[] regionCoord, LatLng coord) {
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


        //Toast.makeText(context, String.valueOf(isInside),Toast.LENGTH_SHORT).show();

        return isInside;
    }



}
