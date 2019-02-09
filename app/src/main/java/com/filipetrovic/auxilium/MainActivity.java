package com.filipetrovic.auxilium;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.filipetrovic.auxilium.TunerView.TunerFragment;
import com.filipetrovic.auxilium.TunerView.TunerModesDialog;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TunerFragment tunerFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    11);
        }
        displayTunerFragment();
//        tunerFragment = (TunerFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.tunerFragment);
//        tunerFragment.start();
    }

    public void displayTunerFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.activity_main_fragment_placeholder, new TunerFragment());
        ft.commit();
    }

    public void displayTunerModesFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        ft.replace(R.id.activity_main_fragment_placeholder, new TunerModesDialog());
        ft.addToBackStack("tunerModes");
        ft.commit();
    }

    public void popBackStack() {
        getSupportFragmentManager().popBackStack("tunerModes", FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
