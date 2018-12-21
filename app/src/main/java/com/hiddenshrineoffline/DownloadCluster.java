package com.hiddenshrineoffline;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

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


}
