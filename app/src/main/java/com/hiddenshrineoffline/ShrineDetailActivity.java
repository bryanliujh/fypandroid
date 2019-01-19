package com.hiddenshrineoffline;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class ShrineDetailActivity extends AppCompatActivity {

    private ToggleButton favouriteBtn;
    private TextView name_val;
    private TextView status_val;
    private TextView size_val;
    private TextView materials_val;
    private TextView deity_val;
    private TextView religion_val;
    private TextView offerings_val;
    private RequestOptions imgOptions;
    private ProgressDialog pDialog;
    private Context context;


    private String name;
    private String status;
    private String size;
    private String materials;
    private String deity;
    private String religion;
    private String offerings;
    private String imageURL;
    private String shrineUID;
    private AppDatabase mDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shrine_detail);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Shrine Details");

        context = getApplicationContext();

        mDB = AppDatabase.getDatabase(context);

        name_val = (TextView) (findViewById(R.id.name_val));
        status_val = (TextView) (findViewById(R.id.status_val));
        size_val = (TextView) (findViewById(R.id.size_val));
        materials_val = (TextView) (findViewById(R.id.materials_val));
        deity_val = (TextView) (findViewById(R.id.deity_val));
        religion_val = (TextView) (findViewById(R.id.religion_val));
        offerings_val = (TextView) (findViewById(R.id.offerings_val));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            ShrineEntity shrineEntity = mDB.shrineDao().findByShrineUid(extras.getString("shrineUUID"));

            //if cluster that contains this shrine is downloaded
            if (shrineEntity != null) {

                name = shrineEntity.getShrine_name();
                status = shrineEntity.getShrine_status();
                size = shrineEntity.getShrine_size();
                materials = shrineEntity.getShrine_materials();
                deity = shrineEntity.getShrine_deity();
                religion = shrineEntity.getShrine_religion();
                offerings = shrineEntity.getShrine_offerings();
                imageURL = shrineEntity.getShrine_imageURL();
                shrineUID = shrineEntity.getShrine_uid();

            }
            else{
                name = extras.getString("name");
                status = extras.getString("status");
                size = extras.getString("size");
                materials = extras.getString("materials");
                deity = extras.getString("deity");
                religion = extras.getString("religion");
                offerings = extras.getString("offerings");
                imageURL = extras.getString("imageURL");
                shrineUID = extras.getString("shrineUUID");
            }


            updateUI();

        }



        //favouriteBtn = (ToggleButton) (findViewById(R.id.favouriteButton));

        //initFavourites();
       //attachedOnClickListener();



    }


    public void updateUI(){

        name_val.setText(name);
        status_val.setText(status);
        size_val.setText(size);
        materials_val.setText(materials);
        deity_val.setText(deity);
        religion_val.setText(religion);
        offerings_val.setText(offerings);


        NetworkConnection networkConnection = new NetworkConnection();
        //check if network is connected, if not connected do not download image
        if(networkConnection.isNetworkConnected(getApplicationContext())) {
            imgOptions = new RequestOptions()
                    .override(800, 800)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            Glide.with(this).load(imageURL)
                    .apply(imgOptions)
                    .into((ImageView) findViewById(R.id.shrine_img));
        }
    }

    public ArrayList<String> retrieveFavourites(){
        ArrayList<String> favouriteList = null;

        try {
            FileManager fileManager = new FileManager();
            favouriteList = (ArrayList<String>) fileManager.readObjectFile("Favourite_File", context);
        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(), "Cannot read favourite file", Toast.LENGTH_SHORT).show();
        }

        return favouriteList;
    }

    public void initFavourites(){
        ArrayList<String> favouriteList = retrieveFavourites();
        favouriteBtn.setChecked(false);

        if (favouriteList != null){
            for (String favourite:favouriteList){
                if (favourite == shrineUID){
                    favouriteBtn.setChecked(true);
                }
            }
        }else{
            favouriteBtn.setChecked(false);
        }

    }


    public void attachedOnClickListener(){
        ArrayList<String> favouriteList = retrieveFavourites();

        FileManager fileManager = new FileManager();

        favouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (favouriteBtn.isChecked()){
                    favouriteList.add(shrineUID);
                    Toast.makeText(getApplicationContext(), "Shrine Favourited", Toast.LENGTH_SHORT).show();
                }else{
                    favouriteList.remove(shrineUID);
                    Toast.makeText(getApplicationContext(), "Shrine Unfavourited", Toast.LENGTH_SHORT).show();
                }

                fileManager.saveObjectFile("Favourite_File",context, favouriteList);
            }
        });
    }







}
