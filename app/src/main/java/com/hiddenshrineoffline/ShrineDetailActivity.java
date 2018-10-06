package com.hiddenshrineoffline;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class ShrineDetailActivity extends AppCompatActivity {

    private TextView name_val;
    private TextView status_val;
    private TextView size_val;
    private TextView materials_val;
    private TextView deity_val;
    private TextView religion_val;
    private TextView offerings_val;
    private RequestOptions imgOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shrine_detail);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Shrine Details");

        name_val = (TextView) (findViewById(R.id.name_val));
        status_val = (TextView) (findViewById(R.id.status_val));
        size_val = (TextView) (findViewById(R.id.size_val));
        materials_val = (TextView) (findViewById(R.id.materials_val));
        deity_val = (TextView) (findViewById(R.id.deity_val));
        religion_val = (TextView) (findViewById(R.id.religion_val));
        offerings_val = (TextView) (findViewById(R.id.offerings_val));

        Bundle extras = getIntent().getExtras();
        if (extras != null){

            name_val.setText(extras.getString("name"));
            status_val.setText(extras.getString("status"));
            size_val.setText(extras.getString("size"));
            materials_val.setText(extras.getString("materials"));
            deity_val.setText(extras.getString("deity"));
            religion_val.setText(extras.getString("religion"));
            offerings_val.setText(extras.getString("offerings"));

            NetworkConnection networkConnection = new NetworkConnection();
            //check if network is connected, if not connected do not download image
            if(networkConnection.isNetworkConnected(getApplicationContext())) {
                imgOptions = new RequestOptions()
                        .override(800, 800)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL);

                Glide.with(this).load(extras.getString("imageURL"))
                        .apply(imgOptions)
                        .into((ImageView) findViewById(R.id.shrine_img));
            }




        }


    }
}
