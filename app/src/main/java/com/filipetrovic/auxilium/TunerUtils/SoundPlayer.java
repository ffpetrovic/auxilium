package com.filipetrovic.auxilium.TunerUtils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.databinding.ObservableField;
import android.media.MediaPlayer;
import android.util.Log;

import com.filipetrovic.auxilium.Interface.INotePlayerFinished;


public class SoundPlayer {
    public ObservableField<String> currentNote = new ObservableField<>("");
    Context mContext;
    MediaPlayer mPlayer;
    private boolean isPlaying = false;
    private boolean isPlayingNote = false;

    INotePlayerFinished onNotePlayerFinished;

    public SoundPlayer(Context c) {
        mContext = c;
    }

    public void playSfxOnStart() {
        try {
            if(mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
            }
            AssetFileDescriptor afd = mContext.getAssets()
                    .openFd("sounds/aux_app_start.wav");
            mPlayer = new MediaPlayer();
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

    public void playSfxCorrectResult() {
        try {
            if(mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
                isPlaying = false;
            }
            AssetFileDescriptor afd = mContext.getAssets()
                    .openFd("sounds/aux_result_correct.wav");
            mPlayer = new MediaPlayer();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    isPlaying = false;
                }
            });
            mPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            afd.close();
            mPlayer.prepare();
            mPlayer.setVolume(1f, 1f);
//            mPlayer.setLooping(true);
            mPlayer.start();
            isPlaying = true;
        } catch(Exception e) {
            Log.e("AUX_LOG", e.toString());
        }
    }


    public void playNote(String note) {
        try {
            if(mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();
                mPlayer = null;
                isPlaying = false;
                isPlayingNote = false;
            }
            if(note.equals(currentNote.get()) || note.equals("")) {
                currentNote.set("");
                isPlaying = false;
                isPlayingNote = false;
                return;
            }
            currentNote.set(note);
            isPlayingNote = true;
            isPlaying = true;

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
    public boolean isPlayingNote() {
        return isPlayingNote;
    }

    public void setOnNotePlayerFinished(INotePlayerFinished onNotePlayerFinished) {
        this.onNotePlayerFinished = onNotePlayerFinished;
    }
}
