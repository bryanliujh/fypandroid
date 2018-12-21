package com.hiddenshrineoffline;

import android.app.ProgressDialog;
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

public class ClusterListViewAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<ClusterEntity> clusterArrayList;
    private Button downloadBtn;
    private Button updateBtn;
    private FileManager fileManager;

    private String cluster_id;
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
        //updateBtn = (Button) (view.findViewById(R.id.update_btn));

        downloadBtn.setTag(i);

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer)view.getTag();
                ClusterEntity downloadClusterEntity = (ClusterEntity) getItem(position);
                cluster_id = String.valueOf(downloadClusterEntity.getCluster_uid());
                DownloadCluster downloadCluster = new DownloadCluster();
                downloadCluster.downloadClusterList(context, cluster_id);
            }
        });


        return view;
    }



    public class ClusterViewHolder{
        TextView number;
        TextView color;
    }




}
