package com.filipetrovic.auxilium.TunerUtils;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.filipetrovic.auxilium.R;

public class TunerMode {
    public String name;
    public String group;
    public Note[] notesObjects;
    public String notes;
    public TunerOptions tunerOptions;

    public TunerMode(TunerOptions tunerOptions) {
        this.tunerOptions = tunerOptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Note[] getNotesObjects() {
        return notesObjects;
    }

    public Note[] getNotesObjectsForGroup() {
        if(isChromatic()) {
            return new Note[] {notesObjects[0]};
        } else {
            return notesObjects;
        }

    }

    public void setNotesObjects(Note[] notesObjects) {
        this.notesObjects = notesObjects;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        String[] notesArr = notes.split(" ");
        Note[] notesMatch = new Note[notesArr.length];
        for(int i = 0; i < notesArr.length; i++) {
            if(!notesArr[i].trim().equals("") && !notesArr[i].isEmpty()) {
                notesMatch[i] = Note.parse(notesArr[i], tunerOptions);
            }
        }
        this.setNotesObjects(notesMatch);
    }

    public boolean isChromatic() {
        return this.getName().equals("Chromatic");
    }

    public static void getAllTuningModes(Context c) {

    }

    public void setInTune(String noteName) {
        for(Note n : notesObjects) {
            if(n.toString().equals(noteName)) {
                n.isInTune.set(true);
            }
        }
    }

    public String getNameLabel() {
        if(name.equals("Chromatic")) {
            return "Automatic";
        } else {
            return getName();
        }
    }

    public String getNotesLabel() {
        if(name.equals("Chromatic")) {
            return "Any Notes";
        } else {
            return Note.getNotes(getNotes(), tunerOptions);
        }
    }

    public static TunerMode getChromaticMode(Context context) {
        TunerOptions tunerOptions = new TunerOptions(context);
        TunerMode tunerMode = new TunerMode(tunerOptions);
        tunerMode.setName("Chromatic");
        tunerMode.setGroup("All Notes");
        tunerMode.setNotes("A1 A#1 B1 C1 C#1 D1 D#1 E1 F1 F#1 G1 G#1 A2 A#2 B2 C2 C#2 D2 D#2 E2 F2 F#2 G2 G#2 A3 A#3 B3 C3 C#3 D3 D#3 E3 F3 F#3 G3 G#3 A4 A#4 B4 C4 C#4 D4 D#4 E4 F4 F#4 G4 G#4 A5 A#5 B5 C5 C#5 D5 D#5 E5 F5 F#5 G5 G#5 A6 A#6 B6 C6 C#6 D6 D#6 E6 F6 F#6 G6 G#6 A7 A#7 B7 C7 C#7 D7 D#7 E7 F7 F#7 G7 G#7 A8 A#8 B8 C8 C#8 D8 D#8 E8 F8 F#8 G8 G#8 ");
        return tunerMode;
    }

    public String toString() {
        if(getName().equals("Chromatic")) {
            return "ChromaticMode";
        } else {
            return getGroup() + "," + getName() + "," + getNotes();
        }
    }

    public static TunerMode valueOf(String s, Context context) {
        if(s.equals("ChromaticMode")) {
            return getChromaticMode(context);
        } else {
            TunerMode tunerMode = new TunerMode(new TunerOptions(context));
            tunerMode.setName(s.split(",")[1]);
            tunerMode.setGroup(s.split(",")[0]);
            tunerMode.setNotes(s.split(",")[2]);
            return tunerMode;
        }
    }
}
