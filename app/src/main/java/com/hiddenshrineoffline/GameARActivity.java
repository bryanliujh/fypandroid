package com.hiddenshrineoffline;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class GameARActivity  extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;

    //ARCORE
    private Session mSession;
    private Config config;
    // Set to true ensures requestInstall() triggers installation if necessary.
    private boolean mUserRequestedInstall = true;
    private ArFragment arFragment;
    private ArSceneView arSceneView;
    private boolean modelAdded = false; // add model once
    private boolean sessionConfigured = false;

    private MediaPlayer mp;
    float speed = 2.5f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();

        setContentView(R.layout.activity_game_ar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mp = new MediaPlayer();
        mp = MediaPlayer.create(context, R.raw.sound);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mp.release();
            }
        });


        initArFragment();

    }

    public void initArFragment(){
        arFragment= (ArFragment)  getSupportFragmentManager().findFragmentById(R.id.ar_fragment);

        // hiding the plane discovery
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getPlaneDiscoveryController().setInstructionView(null);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

        arSceneView= arFragment.getArSceneView();
    }



    private class setupAugmentedImageDb extends AsyncTask<Void, Void, Void> {
        private final AppDatabase mDB;

        setupAugmentedImageDb(AppDatabase db){
            mDB = db;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0){


            List<ShrineEntity> shrineList = mDB.shrineDao().getAll();
            AugmentedImageDatabase imageDatabase = new AugmentedImageDatabase(mSession);
            for (ShrineEntity shrine: shrineList){
                Bitmap augmentedImageBitmap = loadAugmentedImageBitmap(shrine.getShrine_uid());
                if (augmentedImageBitmap != null) {
                    imageDatabase.addImage(shrine.getShrine_uid(), augmentedImageBitmap);
                }
            }
            config.setAugmentedImageDatabase(imageDatabase);
            config.setFocusMode(Config.FocusMode.AUTO);
            //config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            mSession.configure(config);

            return null;

        }
        @Override
        protected void onPostExecute(Void result){
        }

    }


    private Bitmap loadAugmentedImageBitmap(String shrineUUID) {

        File myDir = new File(getPrivateAlbumStorageDir(context, "ShrineAlbum"), shrineUUID + ".jpg");

        if (myDir != null) {
            try (FileInputStream is = new FileInputStream(myDir)) {
                return BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                Log.e("cannot load bitmap", "IO exception loading augmented image bitmap.", e);
            }
        }
        return null;

    }

    public File getPrivateAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("Warning1", "Directory not created");
        }
        return file;
    }

    private void onUpdateFrame(FrameTime frameTime){
        Frame frame = arFragment.getArSceneView().getArFrame();

        Collection<AugmentedImage> augmentedImages = frame.getUpdatedTrackables(AugmentedImage.class);

        for (AugmentedImage augmentedImage : augmentedImages){
            if (augmentedImage.getTrackingState() == TrackingState.TRACKING){
                // get the name of the tracked image and navigate to open the page
                Log.d("hello", augmentedImage.getName());

                if (!modelAdded){
                    renderObject(arFragment,
                            augmentedImage.createAnchor(augmentedImage.getCenterPose()),
                            R.raw.car);
                    modelAdded = true;




                }



            }
        }

    }


    private void renderObject(ArFragment fragment, Anchor anchor, int model){
        ModelRenderable.builder()
                .setSource(this, model)
                .build()
                .thenAccept(renderable -> addNodeToScene(fragment, anchor, renderable))
                .exceptionally((throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage())
                            .setTitle("Error!");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return null;
                }));

    }

    private void addNodeToScene(ArFragment fragment, Anchor anchor, Renderable renderable){
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
        //node.getScaleController().setMaxScale(0.4f);
        //node.getScaleController().setMinScale(0.3f);
        node.setRenderable(renderable);

        // Set the min and max scales of the ScaleController.
        // Default min is 0.75, default max is 1.75.
        node.getScaleController().setMinScale(0.3f);
        node.getScaleController().setMaxScale(0.8f);

        // Set the local scale of the node BEFORE setting its parent
        node.setLocalScale(new Vector3(0.5f, 0.5f, 0.5f));



        node.setParent(anchorNode);
        node.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(speed));
                mp.start();

                AlertDialog.Builder builder = new AlertDialog.Builder(GameARActivity.this);
                builder.setMessage("You Found a Treasure").setTitle("Congratulations");
                AlertDialog dialog = builder.create();
                dialog.show();
                anchor.detach();

                //Toast.makeText(context, "Congratulations, You Found a Treasure", Toast.LENGTH_SHORT).show();
            }
        });
        fragment.getArSceneView().getScene().addChild(anchorNode);

        node.select();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSession != null) {

            arSceneView.pause();
            mSession.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSession == null) {
            String message = null;
            Exception exception = null;
            try {
                mSession = new Session(this);
            } catch (UnavailableArcoreNotInstalledException
                    e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update android";
                exception = e;
            } catch (Exception e) {
                message = "AR is not supported";
                exception = e;
            }

            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                Log.e("Error", "Exception creating session", exception);
                return;
            }
            sessionConfigured = true;

        }
        if (sessionConfigured) {
            configureSession();
            sessionConfigured = false;

            arSceneView.setupSession(mSession);
        }


    }
    private void configureSession() {
        config = new Config(mSession);
        new GameARActivity.setupAugmentedImageDb(AppDatabase.getDatabase(context)).execute();
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        mSession.configure(config);
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
                Intent main = new Intent(GameARActivity.this, MainActivity.class);
                startActivity(main);
                break;
            case R.id.nav_shrine_ar:
                Intent ar = new Intent(GameARActivity.this, shrine_ar.class);
                startActivity(ar);
                break;
            /*
            case R.id.nav_game_ar:
                Intent game = new Intent(GameARActivity.this, GameARActivity.class);
                startActivity(game);
                break;*/
            case R.id.nav_video_ar:
                Intent video = new Intent(GameARActivity.this, VideoARActivity.class);
                startActivity(video);
                break;
            case R.id.nav_settings:
                Intent settings = new Intent(GameARActivity.this, settings.class);
                startActivity(settings);
                break;
            case R.id.nav_favourite:
                Intent favourites = new Intent(GameARActivity.this, FavouriteActivity.class);
                startActivity(favourites);
                break;
            case R.id.nav_nearest_shrine:
                Intent nearest_shrine = new Intent(GameARActivity.this, NearestShrineActivity.class);
                startActivity(nearest_shrine);
                break;
            case R.id.nav_beta_ar:
                Intent beta = new Intent(GameARActivity.this, BetaVideoARActivity.class);
                startActivity(beta);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
