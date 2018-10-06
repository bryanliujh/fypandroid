package com.hiddenshrineoffline;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClusterListViewAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<ClusterEntity> clusterArrayList;
    private Button downloadBtn;
    private Button updateBtn;
    private FileManager fileManager;

    private int cluster_id;
    private String jsonStr;
    private String shrineUUID;
    private String name;
    private String status;
    private String size;
    private String materials;
    private String deity;
    private String religion;
    private String offerings;
    private String imageURL;
    private String circleID;
    private ArrayList<ShrineEntity> shrineArrayList;
    private ArrayList<String> newUidList;
    private ProgressDialog pDialog;

    public ClusterListViewAdapter(Context context, ArrayList<ClusterEntity> clusterArrayList){
        this.context = context;
        this.clusterArrayList = clusterArrayList;
    }
    @Override
    public int getCount() {
        return clusterArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return clusterArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ClusterViewHolder clusterViewHolder;

        if(view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.cluster_listview, viewGroup, false);
            clusterViewHolder = new ClusterViewHolder();
            clusterViewHolder.number = (TextView) view.findViewById(R.id.cluster_id);
            clusterViewHolder.color = (TextView) view.findViewById(R.id.cluster_color);
            view.setTag(clusterViewHolder);
        }
        else{
            clusterViewHolder = (ClusterViewHolder) view.getTag();
        }

        ClusterEntity clusterEntity = (ClusterEntity) getItem(i);
        clusterViewHolder.number.setText(String.valueOf(clusterEntity.getCluster_uid()));
        GradientDrawable clusterBg = (GradientDrawable) clusterViewHolder.color.getBackground();
        clusterBg.setColor(Color.parseColor(clusterEntity.getCluster_color()));

        downloadBtn = (Button) (view.findViewById(R.id.download_btn));
        updateBtn = (Button) (view.findViewById(R.id.update_btn));

        downloadBtn.setTag(i);

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer)view.getTag();
                ClusterEntity downloadClusterEntity = (ClusterEntity) getItem(position);
                cluster_id = downloadClusterEntity.getCluster_uid();
                new ImageSaveInBackground().execute();
            }
        });


        return view;
    }



    public class ClusterViewHolder{
        TextView number;
        TextView color;
    }




    private class ImageSaveInBackground extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Image Downloading");
            pDialog.setCancelable(false);
            pDialog.show();

        }


        @Override
        protected Void doInBackground(Void... arg0) {
            fileManager = new FileManager();
            jsonStr = fileManager.readFile("mapjson",context);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            try {

                //use array difference to find the difference in array (for update only)
                shrineArrayList = new ArrayList<ShrineEntity>();
                newUidList = new ArrayList<String>();
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray features = jsonObj.getJSONArray("features");
                for (int i=0; i<features.length(); i++){
                    JSONObject feature = features.getJSONObject(i);
                    JSONObject properties = feature.getJSONObject("properties");
                    circleID = properties.getString("circleID");
                    if (cluster_id == Integer.parseInt(circleID)) {
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
                        shrineEntity.setCluster_uid(cluster_id);
                        shrineArrayList.add(shrineEntity);
                        newUidList.add(shrineUUID);

                    }
                }
            }
            catch(Exception e){

            }

            new SaveDBInBackground(AppDatabase.getDatabase(context)).execute();

            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }




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
            Toast.makeText(context, "Saved in DB", Toast.LENGTH_SHORT);
        }
    }



}
