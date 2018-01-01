package net.alexandroid.servicepermisssionbroadcastfirststeps;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
            return;
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(mUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                mDownloadCallBack.onError("Server returned HTTP response code: "
                        + connection.getResponseCode() + " - " + connection.getResponseMessage());
            }
            int fileLength = connection.getContentLength();
            Log.d("TAG", "File size: " + fileLength / 1024 + " KB");

            // Input stream (Downloading file)
            inputStream = new BufferedInputStream(url.openStream(), 8192);

            // Output stream (Saving file)
            fos = new FileOutputStream(file.getPath());

            int progress = 0;
            int next;
            byte[] data = new byte[1024];
            while ((next = inputStream.read(data)) != -1) {
                fos.write(data, 0, next);

                int count = ((int) fos.getChannel().size()) * 100 / fileLength;
                if (count > progress) {
                    progress = count;
                    mDownloadCallBack.onProgressUpdate(progress);
                }
            }

            mDownloadCallBack.onDownloadFinished(file.getPath());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            mDownloadCallBack.onError("MalformedURLException: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            mDownloadCallBack.onError("IOException: " + e.getMessage());
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (connection != null) {
                connection.disconnect();
            }
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
