package com.filipetrovic.auxilium.TunerUtils;


import android.databinding.Bindable;
import android.databinding.ObservableBoolean;
import android.util.Log;

public class Note {
    String note;
    int octave;
    double frequency;
    double offset;
    double actualFrequency;
    public ObservableBoolean isInTune = new ObservableBoolean(false);
    private static String[] NOTES = new String[] {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};

    public Note(String note, int octave, double frequency, double actualFrequency, double offset) {
        this.note = note;
        this.octave = octave;
        this.frequency = frequency;
        this.offset = offset;
        this.actualFrequency = actualFrequency;
    }

    public static Note parse(String s) {
        String note = s.substring(0, s.length() - 1);
        int octave = Integer.valueOf(s.substring(s.length() - 1, s.length()));
        double semitones = (octave - 4) * 12 + indexOfNote(NOTES, note) - indexOfNote(NOTES, "A");
        double BASE = 440.00;
        double frequency = BASE * Math.pow(Math.pow(2, (1.00/12.00)), semitones);
        return new Note(note, octave, frequency, frequency, 0);
    }

    public static Note parse(double frequency) {
        double aboveA =
                Math.log(
                        (frequency / 440.00)
                )
                        /
                        Math.log(Math.pow(2.00, 1/12.00
                        ))
                ;
        double closest = 440 * Math.pow(Math.pow(2, 1.00/12.00), Math.round(aboveA));

        // NOTE ( 57 is the number of semitones of A4 )
        int index = (int) ((57 + Math.round(aboveA)) % 12);
        String note = NOTES[index];

        // OCTAVE
        int octave = (int) (Math.floor(( Math.log ( frequency ) - Math.log ( 440 ) )
                / Math.log ( 2 ) + 4.0));

        double offset = 100 * (aboveA - Math.round(aboveA));
        return new Note(note, octave, closest, frequency, offset);
    }

    public double offsetFrom(Note n) {
        double nAboveA =
                Math.log(
                    (n.actualFrequency / 440.00)
                )
                /
                Math.log(Math.pow(2, 1.00/12.00));

        double thisAboveA =
                Math.log(
                    (this.actualFrequency / 440.00)
                )
                /
                Math.log(Math.pow(2, 1.00/12.00));
        ;
        return 100.00 * (thisAboveA - nAboveA);
    }

    private static int indexOfNote(String[] a, String s) {
        int index = -1;
        for (int i=0;i<a.length;i++) {
            if (a[i].equals(s)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getOctave() {
        return octave;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public int semiTonesFromBase() {
        return (int) Math.round(Math.log(this.frequency / 440) / Math.log(Math.pow(2, 2/12)));
    }

    public boolean getIsInTune() {
        return isInTune.get();
    }

    public static Note getPreviousNote(Note n) {
        String note  = n.note;
        int index = indexOfNote(NOTES, note);
        int octave = n.octave;

        if(index == 0) {
            octave--;
            note = NOTES[NOTES.length - 1];
        } else {
            note = NOTES[index - 1];
        }
        return parse(note + String.valueOf(octave));
    }

    public static Note getNextNote(Note n) {
        String note  = n.note;
        int index = indexOfNote(NOTES, note);
        int octave = n.octave;

        if(index == NOTES.length - 1) {
            octave++;
            note = NOTES[0];
        } else {
            note = NOTES[index + 1];
        }
        return parse(note + String.valueOf(octave));
    }

    public String toString() {
        return note + octave;
    }
}