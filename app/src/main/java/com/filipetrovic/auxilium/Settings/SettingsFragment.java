package com.filipetrovic.auxilium.Settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.filipetrovic.auxilium.Dialog.AlertDialog;
import com.filipetrovic.auxilium.R;
import com.filipetrovic.auxilium.Utils.SharedPreferencesHelper;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);

        ((PreferenceReset) findPreference("tuner_reset")).onPreferencesResetListener =
                new PreferenceReset.OnPreferencesResetListener() {
            @Override
            public void onEvent() {
                final com.filipetrovic.auxilium.Dialog.AlertDialog dialog = new AlertDialog();

                dialog.show(getFragmentManager(), "dialog_reset");

                dialog.setDialogCreatedListener(new AlertDialog.OnDialogCreatedListener() {
                    @Override
                    public void onCreate() {
                        dialog.getDialogContent().setVisibility(View.GONE);
                        dialog.getDialogTitle().setText("Reset to default?");

                        dialog.getPositiveButton().setText("Reset");
                        dialog.getPositiveButton().setButtonVariant(getString(R.string.button_variant_danger));
                        dialog.getPositiveButton().setButtonGhost(true);
                        dialog.getPositiveButton().updateStyle();

                        dialog.getNegativeButton().setText("Cancel");
                        dialog.getNegativeButton().setButtonVariant(getString(R.string.button_variant_fade));
                        dialog.getNegativeButton().setButtonGhost(true);
                        dialog.getNegativeButton().updateStyle();
                    }
                });

                dialog.setDialogResultListener(new AlertDialog.OnDialogResultListener() {
                    @Override
                    public void onPositiveResult() {
                        SharedPreferencesHelper.clear(getContext());
                        updatePreferences();
                    }

                    @Override
                    public void onNegativeResult() {

                    }
                });
            }
        };
    }

    public void updatePreference(String key) {
        ((CustomPreference) findPreference(key)).updatePreferenceView();
    }

    public void updatePreferences() {
        PreferenceScreen prefScreen = getPreferenceScreen();
        int prefCount = prefScreen.getPreferenceCount();

        for(int i=0; i < prefCount; i++) {
            CustomPreference pref = (CustomPreference) prefScreen.getPreference(i);
            pref.updatePreferenceView();
        }
    }
}
