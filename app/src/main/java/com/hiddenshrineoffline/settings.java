package com.hiddenshrineoffline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class settings extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        context = getApplicationContext();

        SharedPreferences sharedPreferences = context.getSharedPreferences("cluster_settings", context.MODE_PRIVATE);

        ToggleButton regionToggle = (ToggleButton) findViewById(R.id.region_toggle_button);
        regionToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (b){
                    editor.putBoolean("region_bool", true);
                }
                else{
                    editor.putBoolean("region_bool", false);
                }
                editor.commit();
            }
        });

        //get boolean of region_bool else default is false
        boolean region_bool = sharedPreferences.getBoolean("region_bool", false);
        if (region_bool){
            regionToggle.setChecked(true);
        }
        else{
            regionToggle.setChecked(false);
        }


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
                Intent main = new Intent(settings.this, MainActivity.class);
                startActivity(main);
                break;
            case R.id.nav_shrine_ar:
                Intent ar = new Intent(settings.this, shrine_ar.class);
                startActivity(ar);
                break;
            case R.id.nav_settings:
                Intent settings = new Intent(settings.this, settings.class);
                startActivity(settings);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
