package com.hiddenshrineoffline;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ClusterListViewAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<ClusterEntity> clusterArrayList;
    private TextView clusterColorTv;

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

            /*clusterColorTv = (TextView) view.findViewById(R.id.cluster_color);
            GradientDrawable clusterBg = (GradientDrawable) clusterColorTv.getBackground();
            clusterBg.setColor(Color.parseColor());*/
            view.setTag(clusterViewHolder);
        }
        else{
            clusterViewHolder = (ClusterViewHolder) view.getTag();
        }

        ClusterEntity clusterEntity = (ClusterEntity) getItem(i);
        clusterViewHolder.number.setText(String.valueOf(clusterEntity.getCluster_uid()));
        //clusterViewHolder.color.setText(clusterEntity.getCluster_color());
        GradientDrawable clusterBg = (GradientDrawable) clusterViewHolder.color.getBackground();
        clusterBg.setColor(Color.parseColor(clusterEntity.getCluster_color()));

        return view;
    }


    public class ClusterViewHolder{
        TextView number;
        TextView color;
    }
}
