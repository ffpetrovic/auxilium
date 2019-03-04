package com.filipetrovic.auxilium;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    11);
        }

        openSettings();
    }

    public void openModes() {
        Intent intent = new Intent(this, ModesActivity.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        tunerFragment.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        tunerFragment.start();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_DITHER, WindowManager.LayoutParams.FLAG_DITHER);
        window.setFormat(PixelFormat.RGBA_8888);
        window.setFormat(PixelFormat.TRANSLUCENT);
    }
}
