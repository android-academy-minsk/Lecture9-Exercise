package net.alexandroid.servicepermisssionbroadcastfirststeps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static final String PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int PERMISSIONS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setFab();
    }

    private void setFab() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabClick();
            }
        });
    }

    private void onFabClick() {
        if (isPermissionGranted()) {
            startDownloadService();
        } else {
            requestPermission();
        }
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }

    private void startDownloadService() {
        Log.d("TAG", "startDownloadService");
    }

    private void requestPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, PERMISSION)) {
            // Show an explanation to the user.
            // After the user sees the explanation, try again to request the permission.
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this, new String[]{PERMISSION}, PERMISSIONS_REQUEST_CODE);
            // PERMISSIONS_REQUEST_CODE is an app-defined int constant.
            // The callback method gets the result of the request.
        }
    }
}
