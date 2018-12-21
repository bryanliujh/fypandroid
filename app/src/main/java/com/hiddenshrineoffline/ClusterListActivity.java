package com.hiddenshrineoffline;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class ClusterListActivity extends AppCompatActivity {

    private String[] colorArr;
    private ArrayList<ClusterEntity> clusterArrayList;
    private int clusterNo;
    private ClusterListViewAdapter clusterListViewAdapter;
    private ListView clusterListView;
    private SharedPreferences sharedPreferences;
    private Context context;
    private Boolean region_bool;
    private String jsonFileName;
    HashMap<Integer, String> hashMapCentroid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_list);

        context = getApplicationContext();
        hashMapCentroid = new HashMap<>();
        sharedPreferences = context.getSharedPreferences("cluster_settings", context.MODE_PRIVATE);
        region_bool = sharedPreferences.getBoolean("region_bool", false);

        if (region_bool) {
            clusterNo = Integer.parseInt(getString(R.string.KMEANS_CLUSTER_NUM));
            jsonFileName = "kmeansjson";
            FileManager fileManager = new FileManager();
            hashMapCentroid = (HashMap<Integer, String>) fileManager.readObjectFile("centroid_file", context);
        }else{
            clusterNo = Integer.parseInt(getString(R.string.CLUSTER_NUM));
            jsonFileName = "mapjson";
            hashMapCentroid = null;
        }

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Cluster List");

        clusterListView = (ListView) findViewById(R.id.clusterList);

        Bundle extras = getIntent().getExtras();

        if (extras != null){
            colorArr = extras.getStringArray("colorArr");
            clusterArrayList = initClusterArrayList(colorArr);
            clusterListViewAdapter = new ClusterListViewAdapter(this, jsonFileName, region_bool, hashMapCentroid, clusterArrayList);
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
