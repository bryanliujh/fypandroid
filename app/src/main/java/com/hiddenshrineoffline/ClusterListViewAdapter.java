package com.hiddenshrineoffline;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ClusterListViewAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<ClusterEntity> clusterArrayList;
    private Button downloadBtn;
    private Button updateBtn;

    private String cluster_id;
    private String jsonFileName;
    private Boolean region_bool;
    private HashMap<Integer, String> hashMapCentroid;

    public ClusterListViewAdapter(Context context, String jsonFileName, Boolean region_bool, HashMap<Integer, String> hashMapCentroid, ArrayList<ClusterEntity> clusterArrayList){
        this.context = context;
        this.clusterArrayList = clusterArrayList;
        this.jsonFileName = jsonFileName;
        this.region_bool= region_bool;
        this.hashMapCentroid = hashMapCentroid;
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
            clusterViewHolder.cluster_name = (TextView) view.findViewById(R.id.cluster_id);
            clusterViewHolder.color = (TextView) view.findViewById(R.id.cluster_color);
            view.setTag(clusterViewHolder);
        }
        else{
            clusterViewHolder = (ClusterViewHolder) view.getTag();
        }

        ClusterEntity clusterEntity = (ClusterEntity) getItem(i);
        if (region_bool) {
            //TODO: format the list name
            //TODO: retrieve the cluster nearest to my location
            clusterViewHolder.cluster_name.setText(hashMapCentroid.get(clusterEntity.getCluster_uid()));
        }
        else
        {
            clusterViewHolder.cluster_name.setText("CLUSTER " + String.valueOf(clusterEntity.getCluster_uid()));
        }
        GradientDrawable clusterBg = (GradientDrawable) clusterViewHolder.color.getBackground();
        clusterBg.setColor(Color.parseColor(clusterEntity.getCluster_color()));

        downloadBtn = (Button) (view.findViewById(R.id.download_btn));
        //updateBtn = (Button) (view.findViewById(R.id.update_btn));

        downloadBtn.setTag(i);

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer)view.getTag();
                ClusterEntity downloadClusterEntity = (ClusterEntity) getItem(position);
                cluster_id = String.valueOf(downloadClusterEntity.getCluster_uid());
                DownloadCluster downloadCluster = new DownloadCluster();
                downloadCluster.downloadClusterList(context, jsonFileName, cluster_id);
            }
        });


        return view;
    }



    public class ClusterViewHolder{
        TextView cluster_name;
        TextView color;
    }




}
