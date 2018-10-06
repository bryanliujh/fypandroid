package com.hiddenshrineoffline;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;

import java.util.List;

import static com.mapbox.mapboxsdk.style.expressions.Expression.color;
import static com.mapbox.mapboxsdk.style.expressions.Expression.exponential;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.interpolate;
import static com.mapbox.mapboxsdk.style.expressions.Expression.match;
import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.circleRadius;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {



    private FileManager fileManager;
    private String[] colorArr = {"#FF8C00", "#FFFF00", "#1CE6FF", "#FF34FF", "#FF4A46", "#008941", "#006FA6", "#A30059",
            "#FFDBE5", "#7A4900", "#0000A6", "#63FFAC", "#B79762", "#004D43", "#8FB0FF", "#997D87",
            "#5A0007", "#809693", "#FEFFE6", "#1B4400", "#4FC601", "#3B5DFF", "#4A3B53", "#FF2F80",
            "#61615A", "#BA0900", "#6B7900", "#00C2A0", "#FFAA92", "#FF90C9", "#B903AA", "#D16100",
            "#DDEFFF", "#000035", "#7B4F4B", "#A1C299", "#300018", "#0AA6D8", "#013349", "#00846F",
            "#372101", "#FFB500", "#C2FFED", "#A079BF", "#CC0744", "#C0B9B2", "#C2FF99", "#001E09",
            "#00489C", "#6F0062", "#0CBD66", "#EEC3FF", "#456D75", "#B77B68", "#7A87A1", "#788D66",
            "#885578", "#FAD09F", "#FF8A9A", "#D157A0", "#BEC459", "#456648", "#0086ED", "#886F4C",
            "#34362D", "#B4A8BD", "#00A6AA", "#452C2C", "#636375", "#A3C8C9", "#FF913F", "#938A81",
            "#575329", "#00FECF", "#B05B6F", "#8CD0FF", "#3B9700", "#04F757", "#C8A1A1", "#1E6E00",
            "#7900D7", "#A77500", "#6367A9", "#A05837", "#6B002C", "#772600", "#D790FF", "#9B9700",
            "#549E79", "#FFF69F", "#201625", "#72418F", "#BC23FF", "#99ADC0", "#3A2465", "#922329",
            "#5B4534", "#FDE8DC", "#404E55", "#0089A3", "#CB7E98", "#A4E804", "#324E72", "#6A3A4C",
            "#83AB58", "#001C1E", "#D1F7CE", "#004B28", "#C8D0F6", "#A3A489", "#806C66", "#222800",
            "#BF5650", "#E83000", "#66796D", "#DA007C", "#FF1A59", "#8ADBB4", "#1E0200", "#5B4E51",
            "#C895C5", "#320033", "#FF6832", "#66E1D3", "#CFCDAC", "#D0AC94", "#7ED379", "#012C58", "#000000"};

    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar = null;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private GPSTracker gpsTracker;


    private static final String SOURCE_ID = "shrine.data";
    private static final String LAYER_ID = "shrine.layer";

    public final int REQUEST_CODE_ASKLOCATION = 500;
    public final int REQUEST_CODE_WRITESTORAGE = 501;
    private ProgressDialog pDialog;
    private static String url = "https://ntuhiddenshrine.herokuapp.com/map_geojson";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiaGlkZGVuc2hyaW5lbnR1IiwiYSI6ImNqaW15cHNveTA5ZW0za28ybDhhenUwOXAifQ.vDG7MECkwj4PnwW_iqZCNQ");
        setContentView(R.layout.activity_main);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASKLOCATION);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITESTORAGE);

        // Showing progress dialog
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();

        //mapbox
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);






    }





    public void setCircle(){
        gpsTracker = new GPSTracker(MainActivity.this);
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsTracker.getLatitude(),gpsTracker.getLongitude()), 13.0));

        fileManager = new FileManager();
        String jsonStr = fileManager.readFile("mapjson",getApplicationContext());
        FeatureCollection featureCollection = FeatureCollection.fromJson(jsonStr);
        Source source = new GeoJsonSource(SOURCE_ID, featureCollection);
        mapboxMap.addSource(source);


        CircleLayer circleLayer = new CircleLayer(LAYER_ID, SOURCE_ID);
        circleLayer.withProperties(
                circleRadius(
                        interpolate(
                                exponential(1.75f),
                                zoom(),
                                stop(12, 2f),
                                stop(22, 180f)
                        )),
                circleColor(
                        match(get("circleID"), color(Color.parseColor("#000000")),
                                stop("0", color(Color.parseColor(colorArr[0]))),
                                stop("1", color(Color.parseColor(colorArr[1]))),
                                stop("2", color(Color.parseColor(colorArr[2]))),
                                stop("3", color(Color.parseColor(colorArr[3]))),
                                stop("4", color(Color.parseColor(colorArr[4]))),
                                stop("5", color(Color.parseColor(colorArr[5]))),
                                stop("6", color(Color.parseColor(colorArr[6]))),
                                stop("7", color(Color.parseColor(colorArr[7]))),
                                stop("8", color(Color.parseColor(colorArr[8]))),
                                stop("9", color(Color.parseColor(colorArr[9]))),
                                stop("10", color(Color.parseColor(colorArr[10]))),
                                stop("11", color(Color.parseColor(colorArr[11]))),
                                stop("12", color(Color.parseColor(colorArr[12]))),
                                stop("13", color(Color.parseColor(colorArr[13]))),
                                stop("14", color(Color.parseColor(colorArr[14]))),
                                stop("15", color(Color.parseColor(colorArr[15]))),
                                stop("16", color(Color.parseColor(colorArr[16]))),
                                stop("17", color(Color.parseColor(colorArr[17]))),
                                stop("18", color(Color.parseColor(colorArr[18]))),
                                stop("19", color(Color.parseColor(colorArr[19]))),
                                stop("20", color(Color.parseColor(colorArr[20])))
                        )));

        mapboxMap.addLayer(circleLayer);

    }

    public void setClickListener(){
        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng point) {
                PointF screenPoint = mapboxMap.getProjection().toScreenLocation(point);
                List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, LAYER_ID);
                if (!features.isEmpty()) {
                    Feature selectedFeature = features.get(0);
                    String name = selectedFeature.getStringProperty("name");
                    String status = selectedFeature.getStringProperty("status");
                    String size = selectedFeature.getStringProperty("size");
                    String materials = selectedFeature.getStringProperty("materials");
                    String deity = selectedFeature.getStringProperty("deity");
                    String religion = selectedFeature.getStringProperty("religion");
                    String offerings = selectedFeature.getStringProperty("offerings");
                    String imageURL = selectedFeature.getStringProperty("imageURL");
                    String shrineUID = selectedFeature.getStringProperty("shrineUUID");

                    Intent shrine_detail = new Intent(MainActivity.this, ShrineDetailActivity.class);
                    shrine_detail.putExtra("name",name);
                    shrine_detail.putExtra("status",status);
                    shrine_detail.putExtra("size",size);
                    shrine_detail.putExtra("materials",materials);
                    shrine_detail.putExtra("deity",deity);
                    shrine_detail.putExtra("religion", religion);
                    shrine_detail.putExtra("offerings",offerings);
                    shrine_detail.putExtra("imageURL", imageURL);
                    shrine_detail.putExtra("shrineUUID", shrineUID);

                    startActivity(shrine_detail);

                }
            }
        });

        // Dismiss the progress dialog
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        setCircle();
        setClickListener();

        /*
        SymbolLayer myLayer = new SymbolLayer(LAYER_ID, SOURCE_ID);
        myLayer.setProperties(iconImage("place-of-worship-15"));
        mapboxMap.addLayer(myLayer);*/



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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent cluster = new Intent(MainActivity.this, ClusterListActivity.class);
            cluster.putExtra("colorArr",colorArr);
            startActivity(cluster);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        switch (id){
            case R.id.nav_shrine_map:
                Intent main = new Intent(MainActivity.this, MainActivity.class);
                startActivity(main);
                break;
            case R.id.nav_shrine_ar:
                Intent ar = new Intent(MainActivity.this, shrine_ar.class);
                startActivity(ar);
                break;
            case R.id.nav_settings:
                Intent settings = new Intent(MainActivity.this, settings.class);
                startActivity(settings);
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



        // ARCore requires camera permission to operate.
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this);
            return;
        }



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
