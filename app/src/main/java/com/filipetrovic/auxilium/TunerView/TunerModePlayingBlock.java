package com.filipetrovic.auxilium.TunerView;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.ObservableField;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.filipetrovic.auxilium.R;
import com.filipetrovic.auxilium.TunerUtils.Note;
import com.filipetrovic.auxilium.TunerUtils.TunerOptions;

public class TunerModePlayingBlock extends RelativeLayout {

    public boolean isActive = true;

    public TunerModePlayingBlock(Context context) {
        super(context);
        init(null, 0);
    }

    public TunerModePlayingBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TunerModePlayingBlock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void init(AttributeSet attrs, int defStyle) {
        inflate(getContext(), R.layout.view_tuner_mode_playing, this);
    }

    public void startAnimation() {
        Drawable d = ((ImageView) findViewById(R.id.imageViewMain)).getDrawable();
        if(d instanceof AnimatedVectorDrawable) {
            ((AnimatedVectorDrawable) d).start();
        }
    }

    public void stopAnimation() {
        Drawable d = ((ImageView) findViewById(R.id.imageViewMain)).getDrawable();
        if(d instanceof AnimatedVectorDrawable) {
            ((AnimatedVectorDrawable) d).stop();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if(isActive) {
            startAnimation();
        }
    }

    @BindingAdapter({"app:currentNotePlaying"})
    public static void setNavigationBlockText(TunerModePlayingBlock view, ObservableField<String> note) {
        if(!note.get().equals("")) {
            Note parsedNote = Note.parse(note.get(), new TunerOptions(view.getContext()));
            String n = parsedNote.getTranslatedNote() + parsedNote.getOctave();
            ((TextView) view.findViewById(R.id.tvNote)).setText(n);
        } else {
            ((TextView) view.findViewById(R.id.tvNote)).setText(note.get());
        }


    }
}
