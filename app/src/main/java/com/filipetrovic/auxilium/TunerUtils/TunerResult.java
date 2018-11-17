package com.filipetrovic.auxilium.TunerUtils;

import android.util.Log;

import com.filipetrovic.auxilium.TunerView.Indicator;

public class TunerResult {

    public double percentage;
    public double percentageActual;
    public String note;

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

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public String getNoteLabel() {
        String noteName = this.note;
        if(noteName.contains("#")) {
            noteName = noteName.substring(0, 1);
        }
        return noteName;
    }

    public String getNoteLabelWithAug() {
        String noteName = this.note;
        return noteName;
    }

    public String getNoteLabelWithAugAndOctave() {
        String noteName = this.note + this.octave;
        return noteName;
    }

    public String getFrequencyLabel() {
        return String.valueOf(Math.round(frequency)) + " Hz";
    }


    public String getOctaveLabel() {
        return this.octave != 0 ?  String.valueOf(this.octave) : "";
    }

    public Double getPercentage() {
        if(this.type != Indicator.INDICATOR_TYPE.INACTIVE)
            return this.percentage;
        else
            return -1.00;
    }


    public String getPercentageLabel() {
        if(this.type != Indicator.INDICATOR_TYPE.INACTIVE)
            return String.valueOf((Math.round(this.percentageActual) - 50) / 10) + "%";
        else
            return "";
    }

    public boolean isNull() {
        return this.type == Indicator.INDICATOR_TYPE.INACTIVE;
    }

    public Double getPercentageActual() {
        if(this.type != Indicator.INDICATOR_TYPE.INACTIVE)
            return Math.floor(this.percentageActual);
        else
            return -1.00;
    }

    public Indicator.INDICATOR_TYPE getType() {
        return this.type;
    }


    public String getNoteAug() {
        if(this.note.contains("#")) {
            return "#";
        }
        return "";
    }

    public int octave;
    public float frequency;
    private float tolerance = .3f;
    public Indicator.INDICATOR_TYPE type;

    public TunerResult(double freq, Note[] notesMatch) {
        int index = -1;
        Double dist = java.lang.Double.MAX_VALUE;
        for (int i = 0; i < notesMatch.length; i++) {
            Double d = Math.abs(freq - notesMatch[i].frequency);
            if (d < dist) {
                index = i;
                dist = d;
            }
//            Log.d("AUX_LOG", notesMatch[i].frequency + "");
        }
        Note mNote = notesMatch[index];
        this.octave = mNote.octave;
        this.note =  mNote.note;

        double curNote = mNote.frequency;
        if(freq != 0) {
//            Double prevNote = Note.getPreviousNote(mNote).frequency;
//            Double nextNote = Note.getNextNote(mNote).frequency;
//            Double percentage = 0.0;
//
//            if(freq > prevNote && freq < curNote) {
//                //( cur - min ) / ( max - min )
//                percentage = ((freq - prevNote)) / (curNote - prevNote) * 50;
//                percentage = Math.round(percentage * 100.0) / 100.0;
//            } else if(freq < nextNote && freq > curNote) {
//                // ( cur - min ) / ( max - min )
//                percentage = ((freq - curNote)) / (nextNote - curNote) * 50;
//                percentage = Math.round(percentage * 100.0) / 100.0;
//                percentage += 50;
//            }



            this.percentage = Note.parse(freq).offsetFrom(mNote) + 50;
            this.percentageActual = this.percentage;

            if(this.percentage > 50 - 50 * tolerance && this.percentage < 50 + 50 * tolerance) {
                this.percentage = 50;
                this.type = Indicator.INDICATOR_TYPE.CORRECT;
            } else if(this.percentage < 0) {
                this.percentage = 5;
                this.type = Indicator.INDICATOR_TYPE.INCORRECT
                ;
            } else if(this.percentage > 100) {
                this.percentage = 95;
                this.type = Indicator.INDICATOR_TYPE.INCORRECT;
            } else {
                this.type = Indicator.INDICATOR_TYPE.ACTIVE;
            }


            this.frequency = (float) Math.round( freq * 100.00) / 100.00f;
        } else {
            this.note = "-";
            this.octave = 0;
            this.frequency = 0.0f;
//            this.percentage = 50.0;
            this.type = Indicator.INDICATOR_TYPE.INACTIVE;
        }
    }

    public boolean isEmpty() {
        return this.type == Indicator.INDICATOR_TYPE.INACTIVE;
    }
}
