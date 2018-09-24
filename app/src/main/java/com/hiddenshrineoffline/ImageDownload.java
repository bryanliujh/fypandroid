package com.hiddenshrineoffline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

public class ImageDownload {
    public void initPicasso(Context context, String imageURL, String shrineUUID){
        Picasso.get()
                .load(imageURL)
                .into(new Target(){
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        try{
                            if (isExternalStorageWritable()) {
                                String name = shrineUUID + ".jpg";
                                File myDir = new File(getPrivateAlbumStorageDir(context, "ShrineAlbum"), name);
                                //   "/storage/emulated/0/Android/data/com.hiddenshrineoffline/files/Pictures/ShrineAlbum"

                                String dirstr = myDir.getParent();

                                FileOutputStream out = new FileOutputStream(myDir);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                out.flush();
                                out.close();


                            }
                        }
                        catch(Exception e){

                        }
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                  }
              );
    }



    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
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

}
