package com.filipetrovic.auxilium.TunerView;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.graphics.Typeface;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.filipetrovic.auxilium.BR;
import com.filipetrovic.auxilium.Interface.INoteClickToPlayEvent;
import com.filipetrovic.auxilium.R;
import com.filipetrovic.auxilium.TunerUtils.Note;
import com.filipetrovic.auxilium.Utils.CustomFontHelper;
import com.filipetrovic.auxilium.databinding.ViewNoteSingleBinding;

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
        ((TextView) view.findViewById(R.id.tvNote)).setText(note.get());
    }
}
