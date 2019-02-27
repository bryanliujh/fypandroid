package com.hiddenshrineoffline;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class VideoDownload {
    private Context context;
    private String youtubeLink;
    private String downloadURLStr;
    private String shrineUID;

    //API used
    //https://github.com/HaarigerHarald/android-youtubeExtractor
    //youtube link must be in this format "://youtu.be/" or "youtube.com/watch?v="
    public VideoDownload(Context context, String youtubeLink, String shrineUID){
        this.context = context;
        this.youtubeLink = formatYoutubeLink(youtubeLink);
        this.shrineUID = shrineUID;
        getYoutubeDownloadUrl();

    }


    private String formatYoutubeLink(String youtubeLink){
        //https://www.youtube.com/embed/nxfqVCLXa9k
        String youtubeID;
        youtubeID = youtubeLink.split("/")[4];
        youtubeLink = "https://www.youtube.com/watch?v=" + youtubeID;
        return youtubeLink;
    }

    //Comprehensive list of YouTube format code itags
    //https://gist.github.com/sidneys/7095afe4da4ae58694d128b1034e01e2
    private void getYoutubeDownloadUrl() {
        new YouTubeExtractor(context) {
            @Override
            public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                if (ytFiles != null) {
                    int itag = 18;
                    downloadURLStr = ytFiles.get(itag).getUrl();
                    downloadFromUrl(downloadURLStr);
                    Log.d("youtube url download:", downloadURLStr);
                }
            }
        }.extract(youtubeLink, true, true);
    }


    private void downloadFromUrl(String downloadURLStr) {
        Uri uri = Uri.parse(downloadURLStr);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(shrineUID);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MOVIES, shrineUID + ".mp4");
        //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, shrineUID);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }



}
