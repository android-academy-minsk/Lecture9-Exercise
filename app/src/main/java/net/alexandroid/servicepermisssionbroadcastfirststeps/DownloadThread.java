package net.alexandroid.servicepermisssionbroadcastfirststeps;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DownloadThread extends Thread {

    private final String mUrl;
    private final DownloadCallBack mDownloadCallBack;

    public DownloadThread(String url, DownloadCallBack downloadCallBack) {
        mUrl = url;
        mDownloadCallBack = downloadCallBack;
    }

    @Override
    public void run() {
        Log.d("TAG", "DownloadThread # run");

        File file = createFile();
        if (file == null) {
            mDownloadCallBack.onError("Can't create file");
            //return;
        }
    }

    private File createFile() {
        File mediaStorageDirectory = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        + File.separator);
        // Create the storage directory if it does not exist
        if (!mediaStorageDirectory.exists()) {
            if (!mediaStorageDirectory.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName = createImageFileName() + ".jpg";
        mediaFile = new File(mediaStorageDirectory.getPath() + File.separator + mImageName);
        return mediaFile;
    }


    @NonNull
    private String createImageFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        return "FILE_" + timeStamp;
    }


    public interface DownloadCallBack {
        void onProgressUpdate(int percent);

        void onDownloadFinished(String filePath);

        void onError(String error);
    }
}
