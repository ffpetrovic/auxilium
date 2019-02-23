package com.filipetrovic.auxilium.TunerUtils;

import android.content.Context;
import android.databinding.ObservableBoolean;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.filipetrovic.auxilium.TunerView.Indicator;

import java.util.HashMap;
import java.util.Map;

public class Tuner {
    private int         sampleRate = 0;
    private int         bufferSize = 0;
    private int         readSize = 0;
    private int         amountRead = 0;
    private double      currentFrequency = -1.0;
    private float[]     buffer = null;
    private short[]     intermediaryBuffer = null;
    private short[]     statusBuffer = new short[20];
    private String[]    noteBuffer = new String[30];
    public boolean      isFake = false;


    public long ptr = 0;
    public long input = 0;
    public long pitch = 0;

    public boolean      isRecording = false;
    private Handler     handler = null;
    private AudioRecord audioRecord = null;

    private TunerResult currentNoteResult;
    private TunerResult prevNoteResult;
    private OnNoteFoundListener onNoteFoundListener;
    Thread audioThread;
    private SoundPlayer soundPlayer;

    private boolean isMuted = false;
    private Context mContext;

    public TunerMode tunerMode;

    private boolean playedSfx = false;

    public ObservableBoolean hasValidResult = new ObservableBoolean(false);
    public ObservableBoolean hasCorrectResult = new ObservableBoolean(false);

    static {
        System.loadLibrary("aubio");
        System.loadLibrary("pitch");
    }

//    public Tuner(Context context) {
//        init("E2 A2 D3 G3 B3 E4");
//    }

    public Tuner(Context context, TunerMode mode) {
        mContext = context;
        init(mode);
    }

    private void init(TunerMode mode) {
        tunerMode = mode;
        sampleRate = 44100;
        bufferSize = 4096;
        readSize = bufferSize / 4;
        buffer = new float[readSize];
        intermediaryBuffer = new short[readSize];
        handler = new Handler(Looper.getMainLooper());
    }

    public void start() {
        if(!isRecording) {
            isRecording = true;
//        sampleRate = AudioUtils.getSampleRate();
//        bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            initPitch(sampleRate, bufferSize);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRate, AudioFormat.CHANNEL_IN_DEFAULT,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize);
            audioRecord.startRecording();
            audioThread = new Thread(new Runnable() {
                //Runs off the UI thread
                @Override
                public void run() {
                    findNote();
                }
            }, "Tuner Thread");
            audioThread.start();
        }
        Log.d("AUX_LOG", "Tuner: STARTED");

    }

    public void stop() {
        // stops the recording activity
        isRecording = false;
        sendNullResult();
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            audioThread = null;
            onNoteFoundListener = null;
        }
        Log.d("AUX_LOG", "Tuner: STOPPED");
//        dispatcher = null;
//        dispatcher.stop();
//        dispatcher = null;
//        audioRecord = null;

    }

    private void findNote() {
        while (isRecording) {
            if(!isMuted) {
                amountRead = audioRecord.read(intermediaryBuffer, 0, readSize);
                buffer = shortArrayToFloatArray(intermediaryBuffer);
                final TunerResult result = new TunerResult(getPitch(buffer), tunerMode.getNotesObjects());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pushType(result.type);
                        Indicator.INDICATOR_TYPE tempType = result.type;
                        pushNote(result.note + result.octave);
                        result.type = getType();

                        hasValidResult.set(result.frequency > -1);
                        hasCorrectResult.set(getType() == Indicator.INDICATOR_TYPE.CORRECT);

                        Log.d("AUX_LOG", "valid: " + (getType() != Indicator.INDICATOR_TYPE.INACTIVE));
                        Log.d("AUX_LOG", "correct: " + hasCorrectResult.get());

                        if(getType() == Indicator.INDICATOR_TYPE.CORRECT && !tunerMode.isChromatic()) {
                            tunerMode.setInTune(result.getNoteLabelWithAugAndOctave());
                        }

                        if(
                                !(tempType == Indicator.INDICATOR_TYPE.INACTIVE &&
                                        result.type != Indicator.INDICATOR_TYPE.INACTIVE) &&
                                        onNoteFoundListener != null &&
                                        (result.note + result.octave).equals(getNote())) {
                            currentNoteResult = result;
                            onNoteFoundListener.onEvent(result);
                            if(getType() == Indicator.INDICATOR_TYPE.CORRECT) {
                                if(!playedSfx && soundPlayer != null) {
                                    soundPlayer.playSfxCorrectResult();
                                    playedSfx = true;
                                    new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    playedSfx = false;
                                                }
                                            },
                                            3000);
                                }
                            }
                            if(result.type != Indicator.INDICATOR_TYPE.INACTIVE)
                                prevNoteResult = result;
                        }

                        // Passing the previous successful result, as long as it's not -1
                        // because otherwise, the tuner would pick up very little sound
//                    if(!(result.note + result.octave).equals(getNote())
//                            && prevNoteResult != null && onNoteFoundListener != null && !getNote().equals("00")) {
//                        onNoteFoundListener.onEvent(prevNoteResult);
//                    }
//                    if(onNoteFoundListener != null)
//                        onNoteFoundListener.onEvent(result);
                    }
                });
            }
        }
    }

    private void pushType(Indicator.INDICATOR_TYPE v) {
        short s = 0;
        switch(v) {
            case ACTIVE:
                s = 1;
                break;
            case CORRECT:
                s = 2;
                break;
            case INCORRECT:
                s = 3;
        }
        for(int i = 0; i < statusBuffer.length; i++) {
            if(i != statusBuffer.length - 1) {
                statusBuffer[i] = statusBuffer[i + 1];
            } else {
                statusBuffer[i] = s;
            }
        }
    }

    private void pushNote(String v) {
        for(int i = 0; i < noteBuffer.length; i++) {
            if(i != noteBuffer.length - 1) {
                noteBuffer[i] = noteBuffer[i + 1];
            } else {
                noteBuffer[i] = v;
            }
        }
    }

    private Indicator.INDICATOR_TYPE getType() {
        short type = statusBuffer[0];
        if(type == 0) {
            for(int i = 0; i < statusBuffer.length; i++) {
                if(type != statusBuffer[i]) {
                    type = 1;
//                    Log.d("AUX_TEST",  type + " " + statusBuffer[i]);
                    break;
                }
            }
        } else {
            for(int i = 0; i < statusBuffer.length; i++) {
                if(type != statusBuffer[i] && statusBuffer[i] != 0) {
                    type = 1;
                    break;
                }
            }
        }
        Indicator.INDICATOR_TYPE i = null;
        switch(type) {
            case 0:
                i = Indicator.INDICATOR_TYPE.INACTIVE;
                break;
            case 1:
                i = Indicator.INDICATOR_TYPE.ACTIVE;
                break;
            case 2:
                i = Indicator.INDICATOR_TYPE.CORRECT;
                break;
            case 3:
                i = Indicator.INDICATOR_TYPE.INCORRECT;
                break;
        }
        return i;
    }

    String getNote() {
        String[] ary = noteBuffer;
        Map<String, Integer> m = new HashMap<>();

        for (String a : ary) {
            Integer occ = m.get(a);
            m.put(a, (occ == null) ? 1 : occ + 1);
        }

        int max = -1;
        String mostFrequent = null;

        for (Map.Entry<String, Integer> e : m.entrySet()) {
            if (e.getValue() > max) {
                mostFrequent = e.getKey();
                max = e.getValue();
            }
        }

        return mostFrequent;
    }

    public String getCurrentNoteResultLabel() {
            return currentNoteResult.getNoteLabelWithAug();
    }



    private float[] shortArrayToFloatArray(short[] array) {
        float[] fArray = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            fArray[i] = (float) array[i];
        }
        return fArray;
    }

    public void sendNullResult() {
        if(onNoteFoundListener != null) {
            TunerResult nullTunerResult = new TunerResult(0, tunerMode.getNotesObjects());
            nullTunerResult.type = Indicator.INDICATOR_TYPE.INACTIVE;
            nullTunerResult.percentage = 50f;
            onNoteFoundListener.onEvent(nullTunerResult);
        }
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public static boolean checkIfMicrophoneIsAvailable(Context ctx){
        AudioRecord audio = null;
        boolean ready = true;
        try{
            int baseSampleRate = 44100;
            int channel = AudioFormat.CHANNEL_IN_MONO;
            int format = AudioFormat.ENCODING_PCM_16BIT;
            int buffSize = AudioRecord.getMinBufferSize(baseSampleRate, channel, format );
            audio = new AudioRecord(MediaRecorder.AudioSource.MIC, baseSampleRate, channel, format, buffSize );
            audio.startRecording();
            short buffer[] = new short[buffSize];
            int audioStatus = audio.read(buffer, 0, buffSize);

            if(audioStatus == AudioRecord.ERROR_INVALID_OPERATION || audioStatus == AudioRecord.STATE_UNINITIALIZED /* For Android 6.0 */)
                ready = false;
        }
        catch(Exception e){
            ready = false;
        }
        finally {
            try{
                audio.release();
            }
            catch(Exception e){}
        }

        return ready;
    }

    public void setSoundPlayer(SoundPlayer soundPlayer) {
        this.soundPlayer = soundPlayer;
    }

    public void setOnNoteFoundListener(Tuner.OnNoteFoundListener eventListener) {
        onNoteFoundListener = eventListener;
    }

    public void removeOnNoteFoundListener() {
        onNoteFoundListener = null;
    }

    public void destroy() {
        cleanupPitch();
    }


    public interface OnNoteFoundListener {
        void onEvent(TunerResult note);
    }

    private native float    getPitch(float[] input);
    private native void     initPitch(int sampleRate, int B);
    private native void     cleanupPitch();


    //    private Double getAverageNote() {
//        double sum = 0;
//        for(int i = 0; i < notes.length; i++) {
//            sum += notes[i];
//        }
//        return sum / notes.length;
//    }
//
//
//    private void addNote(Double v) {
//        for(int i = 0; i < notes.length; i++) {
//            if(i != notes.length - 1) {
//                notes[i] = notes[i + 1];
//            } else {
//                notes[i] = v;
//            }
//            Log.d("AUX_TEST", notes[i] + "");
//        }
//    }

}
