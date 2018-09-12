package com.hiddenshrineoffline;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClusterListViewAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<ClusterEntity> clusterArrayList;
    private Button downloadBtn;
    private Button updateBtn;
    private FileManager fileManager;

    private String shrineUUID;
    private String imageURL;

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
                getShrineByCluster(downloadClusterEntity.getCluster_uid());

            }
        });


        return view;
    }

    public void getShrineByCluster(int cluster_id){

        fileManager = new FileManager();
        String jsonStr = fileManager.readFile("mapjson",context);
        try {
            //use array difference to find the difference in array
            JSONObject jsonObj = new JSONObject(jsonStr);
            JSONArray features = jsonObj.getJSONArray("features");
            for (int i=0; i<features.length(); i++){
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                shrineUUID = properties.getString("shrineUUID");
                imageURL = properties.getString("imageURL");
                Log.e("1",imageURL);
                /*
                String properties = feature.getString("properties").replace("\"","").replace("}","");
                String propertyArr[] = properties.split(",");
                String shrineUUIDArr[] = propertyArr[1].split(":");
                String imageURLArr[] = propertyArr[10].split("/*");
                shrineUUID = shrineUUIDArr[1];
                imageURL = imageURLArr[2];

                Log.e("1",imageURL);*/

            }
        }
        catch(Exception e){

        }

        //Toast toast = Toast.makeText(context, jsonStr, Toast.LENGTH_SHORT);
        //toast.show();
    }


    public class ClusterViewHolder{
        TextView number;
        TextView color;
    }
}
