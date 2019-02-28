package com.hiddenshrineoffline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private Context context;
    private AppDatabase mDB;
    private FavouriteListViewAdapter favouriteListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        mDB = AppDatabase.getDatabase(context);
        setContentView(R.layout.activity_favourite);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        //swipe function
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //populate list view on swipe
                populateListView();
            }
        });


        //populate list view initial
        populateListView();




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id){
            case R.id.nav_shrine_map:
                Intent main = new Intent(FavouriteActivity.this, MainActivity.class);
                startActivity(main);
                break;
            case R.id.nav_shrine_ar:
                Intent ar = new Intent(FavouriteActivity.this, shrine_ar.class);
                startActivity(ar);
                break;
            case R.id.nav_video_ar:
                Intent video = new Intent(FavouriteActivity.this, VideoARActivity.class);
                startActivity(video);
                break;
            case R.id.nav_settings:
                Intent settings = new Intent(FavouriteActivity.this, settings.class);
                startActivity(settings);
                break;
            case R.id.nav_favourite:
                Intent favourites = new Intent(FavouriteActivity.this, FavouriteActivity.class);
                startActivity(favourites);
                break;
            case R.id.nav_nearest_shrine:
                Intent nearest_shrine = new Intent(FavouriteActivity.this, NearestShrineActivity.class);
                startActivity(nearest_shrine);
                break;
            case R.id.nav_beta_ar:
                Intent beta = new Intent(FavouriteActivity.this, BetaVideoARActivity.class);
                startActivity(beta);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void populateListView(){
        List<FavouriteEntity> favouriteEntityArrayList = new ArrayList<>();
        ListView favouriteListView = (ListView) findViewById(R.id.favouriteList);

        try {
            favouriteEntityArrayList = mDB.favouriteDao().getAll();
            favouriteListViewAdapter = new FavouriteListViewAdapter(context, favouriteEntityArrayList);
            favouriteListView.setAdapter(favouriteListViewAdapter);
            favouriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    FavouriteEntity favouriteEntity = new FavouriteEntity();
                    favouriteEntity = (FavouriteEntity) adapterView.getAdapter().getItem(position);

                    Intent shrine_detail = new Intent(FavouriteActivity.this, ShrineDetailActivity.class);
                    shrine_detail.putExtra("name",favouriteEntity.getShrine_name());
                    shrine_detail.putExtra("status",favouriteEntity.getShrine_status());
                    shrine_detail.putExtra("size",favouriteEntity.getShrine_size());
                    shrine_detail.putExtra("materials",favouriteEntity.getShrine_materials());
                    shrine_detail.putExtra("deity",favouriteEntity.getShrine_deity());
                    shrine_detail.putExtra("religion", favouriteEntity.getShrine_religion());
                    shrine_detail.putExtra("offerings",favouriteEntity.getShrine_offerings());
                    shrine_detail.putExtra("imageURL", favouriteEntity.getShrine_imageURL());
                    shrine_detail.putExtra("videoURL", favouriteEntity.getShrine_videoURL());
                    shrine_detail.putExtra("shrineUUID", favouriteEntity.getShrine_uid());

                    startActivity(shrine_detail);
                }
            });



        }catch (Exception e){

        }


        if (swipeRefreshLayout.isRefreshing()) {
            Handler mHandler = new Handler();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 700);

        }


    }


}

