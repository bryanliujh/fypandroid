package com.hiddenshrineoffline;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private String jsonStr;
    private final int CLUSTER_NUM = 20;
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
    private Context context;



    private static final String SOURCE_ID = "shrine.data";
    private static final String LAYER_ID = "shrine.layer";

    public final int REQUEST_CODE_ASKLOCATION = 500;
    public final int REQUEST_CODE_WRITESTORAGE = 501;
    private ProgressDialog pDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
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
        jsonStr = fileManager.readFile("mapjson",context);
        new extractLatLng().execute();
        FeatureCollection featureCollection = FeatureCollection.fromJson(jsonStr);
        Source source = new GeoJsonSource(SOURCE_ID, featureCollection);
        mapboxMap.addSource(source);



        Expression.Stop[] stops = new Expression.Stop[CLUSTER_NUM];
        for (int i=0; i<CLUSTER_NUM; i++){
            stops[i] = stop(String.valueOf(i),color(Color.parseColor(colorArr[i])));
        }


        CircleLayer circleLayer = new CircleLayer(LAYER_ID, SOURCE_ID);
        circleLayer.withProperties(
                circleRadius(
                        interpolate(
                                exponential(1.75f),
                                zoom(),
                                stop(12, 2f),
                                stop(22, 180f)
                        ))
        );

        circleLayer.setProperties(
                circleColor(
                        match(get("circleID"), color(Color.parseColor("#000000")),stops)
                )
        );

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

        else if (id == R.id.cluster_layer){
            calCluster();
        }

        else if (id == R.id.shrine_status){

        }

        else if (id == R.id.singapore_map){
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(1.3521, 103.8198)) // Sets the new camera position
                    .zoom(10) // Sets the zoom
                    .bearing(180) // Rotate the camera
                    .tilt(30) // Set the camera tilt
                    .build(); // Creates a CameraPosition from the builder

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
        }
        else if (id == R.id.sri_lanka_map){
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(9.6523, 80.0417)) // Sets the new camera position
                    .zoom(10) // Sets the zoom
                    .bearing(180) // Rotate the camera
                    .tilt(30) // Set the camera tilt
                    .build(); // Creates a CameraPosition from the builder

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
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

    private class extractLatLng extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            HashMap<Integer, ArrayList<String>> hashMap = new HashMap<>();
            try {

                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray features = jsonObject.getJSONArray("features");
                for (int i=0; i<features.length(); i++){
                    JSONObject feature = features.getJSONObject(i);
                    //get cluster id
                    JSONObject properties = feature.getJSONObject("properties");
                    Integer circleID = Integer.parseInt(properties.getString("circleID"));
                    //get coordinates of pts
                    JSONObject geometry = feature.getJSONObject("geometry");
                    JSONArray coord_arr = geometry.getJSONArray("coordinates");
                    double lon = (double) coord_arr.get(0);
                    double lat = (double) coord_arr.get(1);
                    String coord = String.valueOf(lon) + ',' + String.valueOf(lat);
                    hashMap.computeIfAbsent(circleID, k -> new ArrayList<>()).add(coord);
                }


            }
            catch(Exception e){
                Log.e("json_error","Error Extracting Latitude and Longitude");
            }


            try{
                File file = new File(context.getFilesDir(), "lat_lon_file");
                ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
                outputStream.writeObject(hashMap);
                outputStream.flush();
                outputStream.close();
            }
            catch(Exception e){
                Log.e("save_error","Error Saving Latitude and Longitude");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Toast.makeText(getApplicationContext(), "Extracting of Latitude and Longitude Finished", Toast.LENGTH_SHORT).show();

        }
    }



    public void calCluster(){
        HashMap<Integer, ArrayList<String>> hashMap = new HashMap<>();
        try {
            File file = new File(context.getFilesDir(), "lat_lon_file");
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            hashMap = (HashMap<Integer, ArrayList<String>>) objectInputStream.readObject();
        }
        catch(Exception e){
            Log.e("read_error","Error Reading Latitude and Longitude");
        }

        for (Map.Entry<Integer, ArrayList<String>> entry : hashMap.entrySet()){
            List<LatLng> points = new ArrayList<>();
            for (String e : entry.getValue()){
                String[] coord = e.split(",");
                double lon = Double.parseDouble(coord[0]);
                double lat = Double.parseDouble(coord[1]);
                LatLng location = new LatLng(lat,lon);
                points.add(location);
            }

            QuickHullLatLng quickHullLatLng = new QuickHullLatLng();
            ArrayList<LatLng> hullLatLng = new ArrayList<>(quickHullLatLng.quickHull(points));
            drawCluster(hullLatLng, entry.getKey());
        }

    }

    public void drawCluster(ArrayList<LatLng> points, Integer color_index){
        mapboxMap.addPolygon(new PolygonOptions()
                .addAll(points)
                .fillColor(Color.parseColor(colorArr[color_index])));
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
