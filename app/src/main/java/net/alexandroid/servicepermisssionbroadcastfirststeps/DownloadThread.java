package net.alexandroid.servicepermisssionbroadcastfirststeps;

import android.util.Log;

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
    }

    public interface DownloadCallBack {
        void onProgressUpdate(int percent);

        void onDownloadFinished(String filePath);

        void onError(String error);
    }
}
