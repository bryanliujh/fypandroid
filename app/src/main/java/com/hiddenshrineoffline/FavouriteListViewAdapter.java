package com.hiddenshrineoffline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class FavouriteListViewAdapter extends BaseAdapter {

    private Context context;
    private List<FavouriteEntity> favouriteEntityArrayList;
    private RequestOptions imgOptions;
    private ToggleButton favouriteBtn;
    private FavouriteEntity favouriteEntity;
    private AppDatabase mDB;

    public FavouriteListViewAdapter(Context context, List<FavouriteEntity> favouriteEntityArrayList){
        this.context = context;
        this.favouriteEntityArrayList = favouriteEntityArrayList;
    }

    @Override
    public int getCount() {
        return favouriteEntityArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return favouriteEntityArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        FavouriteViewHolder favouriteViewHolder;

        mDB = AppDatabase.getDatabase(context);

        if (view == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.favourite_listview, viewGroup, false);

            favouriteViewHolder = new FavouriteViewHolder();
            favouriteViewHolder.image = (ImageView) view.findViewById(R.id.image);
            favouriteViewHolder.name = (TextView) view.findViewById(R.id.name);
            favouriteViewHolder.favouriteBtn = (ToggleButton) view.findViewById(R.id.favouriteButton);

            view.setTag(favouriteViewHolder);
        } else {
            favouriteViewHolder = (FavouriteViewHolder) view.getTag();
        }

        favouriteEntity = (FavouriteEntity) getItem(i);
        imgOptions = new RequestOptions()
                .centerCrop()
                .override(800, 550)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(context).load(favouriteEntity.getShrine_imageURL())
                .apply(imgOptions)
                .into((ImageView) view.findViewById(R.id.image));
        favouriteViewHolder.name.setText(favouriteEntity.getShrine_name());
        favouriteBtn = (ToggleButton) (view.findViewById(R.id.favouriteButton));
        favouriteBtn.setTag(i);
        favouriteBtn.setChecked(true);
        favouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                FavouriteEntity removeFavouriteObj = (FavouriteEntity) getItem(position);
                mDB.favouriteDao().delete(removeFavouriteObj);
            }
        });

        return view;
    }

    private class FavouriteViewHolder{
        ImageView image;
        TextView name;
        ToggleButton favouriteBtn;
    }
}
