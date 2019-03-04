package com.filipetrovic.auxilium.TunerUtils;

import android.content.Context;
import android.util.Log;

import com.filipetrovic.auxilium.R;
import com.filipetrovic.auxilium.Utils.SharedPreferencesHelper;

public class TunerOptions {
    public int tunerBase;
    public boolean sharps;
    public String naming;

    private String[] NOTES_ENGLISH_SHARPS = new String[] {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};
    private String[] NOTES_ENGLISH_FLATS = new String[] {"C","Db","D","Eb","E","F","Gb","G","Ab","A","Bb","B"};
    private String[] NOTES_SOLFEGE_SHARPS = new String[] {"Do","Di","Re","Ri","Mi","Fa","Fi","Sol","Si","La","Li","Si"};
    private String[] NOTES_SOLFEGE_FLATS = new String[] {"Do","Ra","Re","Me","Mi","Fa","Se","Sol","Le","La","Se","Si"};

    public TunerOptions(Context context) {
        tunerBase = SharedPreferencesHelper
                .getSharedPreferenceInt(context, "tuner_calibration",
                        context.getResources().getInteger(R.integer.pref_calibration_default));
        sharps = SharedPreferencesHelper
                .getSharedPreferenceString(context, "tuner_accidentals",
                        context.getResources().getString(R.string.pref_accidentals_default)).equals("sharp");
        naming = SharedPreferencesHelper
                .getSharedPreferenceString(context, "tuner_naming",
                        context.getResources().getString(R.string.pref_naming_default));

        Log.d("AUX_LOG", naming +"");
    }

    public String[] getNotes() {
        if(this.naming.equals("english")) {
            if(this.sharps) return NOTES_ENGLISH_SHARPS;
            else            return NOTES_ENGLISH_FLATS;
        } else if(this.naming.equals("solfege")) {
            if(this.sharps) return NOTES_SOLFEGE_SHARPS;
            else            return NOTES_SOLFEGE_FLATS;
        }

        else return NOTES_SOLFEGE_SHARPS;
    }

    public String getChromaticNotes() {
        String[] notes = getNotes();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 1; i < 9; i++) {
            for(int j = 0; j < notes.length; j++) {
                stringBuilder.append(notes[j]);
                stringBuilder.append(i);
                stringBuilder.append(" ");
            }
        }
        return stringBuilder.toString();
    }
}
