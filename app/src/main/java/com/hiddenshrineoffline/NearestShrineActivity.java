package com.hiddenshrineoffline;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONObject;

public class NearestShrineActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {



    private MapView mapView;
    private Context context;

    private TextView name_val;
    private TextView status_val;
    private TextView size_val;
    private TextView materials_val;
    private TextView deity_val;
    private TextView religion_val;
    private TextView offerings_val;
    private TextView videoURL_val;


    private Location currentLocation;
    private Location closestLocation;
    private String closestName;
    private String closestStatus;
    private String closestSize;
    private String closestMaterials;
    private String closestDeity;
    private String closestReligion;
    private String closestOfferings;
    private String closestVideoURL;
    private String closestImageURL;
    private String closestShrineUID;
    private float smallestDistance;
    private String jsonStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_nearest_shrine);
        mapView = (MapView) findViewById(R.id.mapView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        name_val = (TextView) (findViewById(R.id.name_val));
        status_val = (TextView) (findViewById(R.id.status_val));
        size_val = (TextView) (findViewById(R.id.size_val));
        materials_val = (TextView) (findViewById(R.id.materials_val));
        deity_val = (TextView) (findViewById(R.id.deity_val));
        religion_val = (TextView) (findViewById(R.id.religion_val));
        offerings_val = (TextView) (findViewById(R.id.offerings_val));
        videoURL_val = (TextView) (findViewById(R.id.videoURL_val));


        getNearestShrine();


    }


    public void getNearestShrine(){
        GPSTracker gpsTracker = new GPSTracker(context);
        currentLocation = new Location("");
        closestLocation = new Location("");

        closestShrineUID = null;
        smallestDistance = -1;
        jsonStr = null;

        FileManager fileManager = new FileManager();

        currentLocation.setLatitude(gpsTracker.getLatitude());
        currentLocation.setLongitude(gpsTracker.getLongitude());
        jsonStr = (String) fileManager.readObjectFile("mapjson", context);

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray features = jsonObject.getJSONArray("features");
            for (int i=0; i<features.length(); i++){
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");
                String name = properties.getString("name");
                String status = properties.getString("status");
                String size = properties.getString("size");
                String materials = properties.getString("materials");
                String deity = properties.getString("deity");
                String religion = properties.getString("religion");
                String offerings = properties.getString("offerings");
                String imageURL = properties.getString("imageURL");
                String videoURL = properties.getString("videoURL");
                String shrineUID = properties.getString("shrineUUID");


                JSONObject geometry = feature.getJSONObject("geometry");
                JSONArray coord_arr = geometry.getJSONArray("coordinates");
                double lon = (double) coord_arr.get(0);
                double lat = (double) coord_arr.get(1);
                Location location = new Location("");
                location.setLongitude(lon);
                location.setLatitude(lat);
                float distance = currentLocation.distanceTo(location);
                if (smallestDistance == -1 || distance < smallestDistance) {
                    closestLocation = location;
                    closestName = name;
                    closestStatus = status;
                    closestSize = size;
                    closestMaterials = materials;
                    closestDeity = deity;
                    closestReligion = religion;
                    closestOfferings = offerings;
                    closestImageURL = imageURL;
                    closestVideoURL = videoURL;
                    closestShrineUID = shrineUID;
                    smallestDistance = distance;
                }

            }


        }catch(Exception e){
            Log.e("json_error","Error Calculating Closest Location");
        }



        //Log.i("hi", String.valueOf(closestLocation.getLatitude()) + ":" + String.valueOf(closestLocation.getLongitude()) + ":" + closestShrineUID);


        name_val.setText(closestName);
        status_val.setText(closestStatus);
        size_val.setText(closestSize);
        materials_val.setText(closestMaterials);
        deity_val.setText(closestDeity);
        religion_val.setText(closestReligion);
        offerings_val.setText(closestOfferings);
        videoURL_val.setText(closestVideoURL);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(closestLocation.getLatitude(),closestLocation.getLongitude()), 13.0));;
                // One way to add a marker view
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(closestLocation.getLatitude(),closestLocation.getLongitude()))
                        .title(closestName)
                );
            }
        });

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
                Intent main = new Intent(NearestShrineActivity.this, MainActivity.class);
                startActivity(main);
                break;
            case R.id.nav_shrine_ar:
                Intent ar = new Intent(NearestShrineActivity.this, shrine_ar.class);
                startActivity(ar);
                break;
            case R.id.nav_game_ar:
                Intent game = new Intent(NearestShrineActivity.this, GameARActivity.class);
                startActivity(game);
                break;
            case R.id.nav_video_ar:
                Intent video = new Intent(NearestShrineActivity.this, VideoARActivity.class);
                startActivity(video);
                break;
            case R.id.nav_settings:
                Intent settings = new Intent(NearestShrineActivity.this, settings.class);
                startActivity(settings);
                break;
            case R.id.nav_favourite:
                Intent favourites = new Intent(NearestShrineActivity.this, FavouriteActivity.class);
                startActivity(favourites);
                break;
            case R.id.nav_nearest_shrine:
                Intent nearest_shrine = new Intent(NearestShrineActivity.this, NearestShrineActivity.class);
                startActivity(nearest_shrine);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


}
