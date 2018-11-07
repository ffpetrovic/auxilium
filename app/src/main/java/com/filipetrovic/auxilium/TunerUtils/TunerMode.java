package com.filipetrovic.auxilium.TunerUtils;
import android.content.Context;
import android.view.Gravity;
import com.filipetrovic.auxilium.R;

public class TunerMode {
    public String name;
    public String group;
    public Note[] notesObjects;
    public String notes;

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
                notesMatch[i] = Note.parse(notesArr[i]);
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

    public int getTunerModeDrawable() {
        if(isChromatic()) {
            return R.drawable.ic_mode_pick;
        } else if(group.equals("Guitar")) {
            return R.drawable.ic_mode_acoustic;
        } else if(group.equals("Bass")) {
            return R.drawable.ic_mode_bass;
        } else if(group.equals("Ukulele")) {
            return R.drawable.ic_mode_ukulele;
        } else if(group.equals("Fiddle")) {
            return R.drawable.ic_mode_fiddle;
        } else {
            return R.drawable.ic_mode_pick;
        }
    }

    public class TunerModeGroup {
        String name;
        TunerMode[] tunerModes;

        public TunerModeGroup() {

        }
    }
}
