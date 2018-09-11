package com.hiddenshrineoffline;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class ClusterListActivity extends AppCompatActivity {

    private String[] colorArr;
    private ArrayList<ClusterEntity> clusterArrayList;
    private final int clusterNo = 10;
    private ClusterListViewAdapter clusterListViewAdapter;
    private ListView clusterListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_list);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Cluster List");

        clusterListView = (ListView) findViewById(R.id.clusterList);

        Bundle extras = getIntent().getExtras();
        if (extras != null){

            colorArr = extras.getStringArray("colorArr");
            initClusterArrayList(colorArr);
            clusterListViewAdapter = new ClusterListViewAdapter(getApplicationContext(), clusterArrayList);
            clusterListView.setAdapter(clusterListViewAdapter);
        }


    }

    public ArrayList<ClusterEntity> initClusterArrayList(String[] colorArr){
        clusterArrayList = new ArrayList<ClusterEntity>();
        for(int i=0; i<clusterNo; i++){
            ClusterEntity clusterObj = new ClusterEntity(i,colorArr[i]);
            clusterArrayList.add(clusterObj);
        }
        return clusterArrayList;
    }
}
