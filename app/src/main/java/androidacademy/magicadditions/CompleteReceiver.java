package androidacademy.magicadditions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class CompleteReceiver extends BroadcastReceiver {

    public static final String ACTION_DOWNLOAD_COMPLETE = "androidacademy.magicadditions.DOWNLOAD_COMPLETE";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG", "CompleteReceiver # onReceive");
        Toast.makeText(context, "File downloaded", Toast.LENGTH_LONG).show();

        String filePath = intent.getStringExtra(DownloadService.FILE_PATH);
        Log.d("TAG", "CompleteReceiver # onReceive, filePath: " + filePath);

        MainActivity.startActivity(context, intent.getExtras());
    }
}
