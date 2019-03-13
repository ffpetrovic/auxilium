package com.filipetrovic.auxilium;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.filipetrovic.auxilium.Dialog.AlertDialog;

public class SettingsActivity extends AppCompatActivity {

    /*
        Used as a view wrapper for SettingsFragment
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.buttonClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
