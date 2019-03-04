package com.filipetrovic.auxilium.TunerUtils;


import android.databinding.ObservableBoolean;
import android.util.Log;

public class Note {
    String translatedNote;
    String realNote;
    int octave;
    double frequency;
    double offset;
    double actualFrequency;
    public ObservableBoolean isInTune = new ObservableBoolean(false);

    private static String[] NOTES_DEFAULT = new String[] {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};

    private TunerOptions tunerOptions;

    public Note(String translatedNote, int octave, double frequency, double actualFrequency, double offset, TunerOptions tunerOptions) {
        this.translatedNote = translatedNote;
        this.realNote = NOTES_DEFAULT[indexOfNote(tunerOptions.getNotes(), translatedNote)];
        this.octave = octave;
        this.frequency = frequency;
        this.offset = offset;
        this.actualFrequency = actualFrequency;
        this.tunerOptions = tunerOptions;
    }

    public static Note parse(String s, TunerOptions tunerOptions) {
        String note = s.substring(0, s.length() - 1);
        int octave = Integer.valueOf(s.substring(s.length() - 1, s.length()));
        double semitones = (octave - 4) * 12 + indexOfNote(NOTES_DEFAULT, note) - indexOfNote(NOTES_DEFAULT, "A");
        double BASE = tunerOptions.tunerBase;
        double frequency = BASE * Math.pow(Math.pow(2, (1.00/12.00)), semitones);

        String translatedNote = tunerOptions.getNotes()[indexOfNote(NOTES_DEFAULT, note)];
        return new Note(translatedNote, octave, frequency, frequency, 0, tunerOptions);
    }

    public static Note parse(double frequency, TunerOptions tunerOptions) {
        double aboveA =
                Math.log(
                        (frequency / tunerOptions.tunerBase)
                )
                        /
                        Math.log(Math.pow(2.00, 1/12.00
                        ))
                ;
        double closest = tunerOptions.tunerBase * Math.pow(Math.pow(2, 1.00/12.00), Math.round(aboveA));

        // NOTE ( 57 is the number of semitones of A4 )
        int index = (int) ((57 + Math.round(aboveA)) % 12);
        String note = tunerOptions.getNotes()[index];
        String localizedNote = NOTES_DEFAULT[index];

        // OCTAVE
        int octave = (int) (Math.floor(( Math.log ( frequency ) - Math.log ( tunerOptions.tunerBase ) )
                / Math.log ( 2 ) + 4.0));

        double offset = 100 * (aboveA - Math.round(aboveA));
        return new Note(note, octave, closest, frequency, offset, tunerOptions);
    }

    public double offsetFrom(Note n) {
        double nAboveA =
                Math.log(
                    (n.actualFrequency / tunerOptions.tunerBase)
                )
                /
                Math.log(Math.pow(2, 1.00/12.00));

        double thisAboveA =
                Math.log(
                    (this.actualFrequency / tunerOptions.tunerBase)
                )
                /
                Math.log(Math.pow(2, 1.00/12.00));
        ;
        return 100.00 * (thisAboveA - nAboveA);
    }

    private static int indexOfNote(String[] a, String s) {
        int index = -1;
        for (int i=0;i<a.length;i++) {
            Log.d("AUX_LOG", a[i] + " " + s);
            if (a[i].equals(s)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public boolean isAccidental() {
        // Accidental positions: 1 3 6 8 10

        int index = indexOfNote(tunerOptions.getNotes(), getTranslatedNote());
        return (index == 1 || index == 3 || index == 6 || index == 8 || index == 10);
    }

    public String getTranslatedNote() {
        return translatedNote;
    }

    public void setTranslatedNote(String translatedNote) {
        this.translatedNote = translatedNote;
    }

    public int getOctave() {
        return octave;
    }

    public void setOctave(int octave) {
        this.octave = octave;
    }

    public String getRealNote() {
        return realNote;
    }

    public void setRealNote(String realNote) {
        this.realNote = realNote;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public int semiTonesFromBase() {
        return (int) Math.round(Math.log(this.frequency / tunerOptions.tunerBase) / Math.log(Math.pow(2, 2.00/12)));
    }

    public boolean getIsInTune() {
        return isInTune.get();
    }

    public static String getNotes(String s, TunerOptions tunerOptions) {
        String[] notes = s.split(" ");
        StringBuilder notesString = new StringBuilder();
        for(int i = 0; i < notes.length; i++) {
            notesString.append(Note.parse(notes[i], tunerOptions).getTranslatedNote());
            notesString.append(Note.parse(notes[i], tunerOptions).getOctave());
            notesString.append(" ");
        }
        return notesString.toString();
    }

    public String toString() {
        return getTranslatedNote() + octave;
    }
}