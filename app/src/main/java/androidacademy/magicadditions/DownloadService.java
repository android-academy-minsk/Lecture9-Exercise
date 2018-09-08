package androidacademy.magicadditions;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.util.Log;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class DownloadService extends Service {

    public static final String URL = "URL";
    public static final int ONGOING_NOTIFICATION_ID = 987;
    public static final int ERROR_NOTIFICATION_ID = 1024;
    public static final String CHANNEL_DEFAULT_IMPORTANCE = "Channel";
    public static final String FILE_PATH = "FILE_PATH";

    private NotificationManagerCompat notificationManager;

    public static void startService(Activity activity, String url) {
        Intent intent = new Intent(activity, DownloadService.class);
        intent.putExtra(URL, url);
        activity.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        Log.d("TAG", "DownloadService # onStartCommand");
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        startForeground();

        String url = intent.getStringExtra(URL);
        Log.d("TAG", "DownloadService # URL: " + url);

        startDownloadThread(url);

        return START_STICKY;
    }

    private void startDownloadThread(String url) {
        new DownloadThread(url, new DownloadThread.DownloadCallBack() {
            @Override
            public void onProgressUpdate(int progress) {
                Log.d("TAG", "DownloadService, DownloadThread, onProgressUpdate: " + progress + "%");
                updateNotification(progress);
            }

            @Override
            public void onDownloadFinished(String filePath) {
                Log.d("TAG", "DownloadService, DownloadThread, onDownloadFinished: " + filePath);
                sendBroadcastMsgDownloadComplete(filePath);
                stopSelf();
            }

            @Override
            public void onError(String error) {
                Log.e("TAG", "DownloadService, DownloadThread, Error: " + error);
                notificationManager.notify(ERROR_NOTIFICATION_ID, createErrorNotification());
                notificationManager.cancel(DownloadService.ONGOING_NOTIFICATION_ID);
                stopSelf();
            }
        }).start();
    }

    private void startForeground() {
        createNotificationChannel();

        Notification notification = createNotification(0);

        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    private Notification createNotification(int progress) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return new NotificationCompat.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
                .setContentTitle(String.format(Locale.getDefault(), "Downloading... %d%%", progress))
                .setContentText(getText(R.string.notification_message))
                .setSmallIcon(R.drawable.ic_stat_download)
                .setContentIntent(pendingIntent)
                .build();
    }

    private Notification createErrorNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_DEFAULT_IMPORTANCE)
                .setContentTitle(getText(R.string.notification_error_title))
                .setContentText(getText(R.string.notification_error_message))
                .setSmallIcon(R.drawable.ic_stat_download)
                .build();
    }

    private void updateNotification(int progress) {
        Notification notification = createNotification(progress);
        notificationManager.notify(ONGOING_NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // The user-visible name of the channel.
            CharSequence name = getString(R.string.channel_name);
            // The user-visible description of the channel.
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_DEFAULT_IMPORTANCE, name, importance);

            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);

            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }
    }

    private void sendBroadcastMsgDownloadComplete(String filePath) {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            intent = new Intent(this, CompleteReceiver.class);
        } else {
            intent = new Intent(CompleteReceiver.ACTION_DOWNLOAD_COMPLETE);
        }
        intent.putExtra(FILE_PATH, filePath);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TAG", "DownloadService # onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
