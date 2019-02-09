package com.filipetrovic.auxilium.TunerUtils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.databinding.ObservableField;
import android.media.MediaPlayer;
import android.media.midi.MidiManager;
import android.net.Uri;
import android.util.Log;

import com.filipetrovic.auxilium.Interface.INotePlayerFinished;

import jm.JMC;
import jm.music.data.Phrase;
import jm.util.Play;
import jm.music.data.Note;




public class SoundPlayer {
    public ObservableField<String> currentNote = new ObservableField<>("");
    Context mContext;
    MediaPlayer mPlayer;

    INotePlayerFinished onNotePlayerFinished;

    public SoundPlayer(Context c) {
        mContext = c;
    }

    public void playNote(String note) {
        try {
            if(mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            }
            if(note.equals(currentNote.get()) || note.equals("")) {
                currentNote.set("");
                return;
            }
            currentNote.set(note);


            AssetFileDescriptor afd = mContext.getAssets()
                    .openFd("sounds/" + currentNote.get() + ".mp3");
            mPlayer = new MediaPlayer();
            if(onNotePlayerFinished != null) {
                onNotePlayerFinished.onEvent();
            }
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if(onNotePlayerFinished != null) {
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                        onNotePlayerFinished.onEvent();
                    }
                }
            });
            mPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            afd.close();
            mPlayer.prepare();
            mPlayer.setVolume(1f, 1f);
//            mPlayer.setLooping(true);
            mPlayer.start();
        } catch(Exception e) {
            Log.e("AUX_LOG", e.toString());
        }
    }

    public boolean isPlaying() {
        return !currentNote.get().equals("");
    }

    public void setOnNotePlayerFinished(INotePlayerFinished onNotePlayerFinished) {
        this.onNotePlayerFinished = onNotePlayerFinished;
    }
}
