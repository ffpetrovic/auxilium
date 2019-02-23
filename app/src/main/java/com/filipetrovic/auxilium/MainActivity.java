package com.filipetrovic.auxilium;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.filipetrovic.auxilium.TunerView.TunerFragment;
import com.filipetrovic.auxilium.TunerView.TunerModesDialog;

public class MainActivity extends AppCompatActivity {
    TunerFragment tunerFragment;
    TunerModesDialog tunerModesDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    11);
        }

        tunerFragment = new TunerFragment();
        tunerModesDialog = new TunerModesDialog();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.add(R.id.activity_main_fragment_placeholder, tunerFragment);
        ft.commit();
    }

    public void showTunerModesFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_down, android.R.anim.fade_out, android.R.anim.fade_in, R.anim.slide_out_down);
        if(tunerModesDialog.isAdded()) {
            ft.show(tunerModesDialog);
        } else {
            ft.add(R.id.activity_main_modes_placeholder, tunerModesDialog);
        }

        if(tunerFragment.isAdded()) {
            ft.hide(tunerFragment);
            tunerFragment.stop();
        }

        ft.addToBackStack("tunerModes");
        ft.commit();
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

    public void startAttemptTuner() {
        tunerFragment.startAttempt();
    }
}
