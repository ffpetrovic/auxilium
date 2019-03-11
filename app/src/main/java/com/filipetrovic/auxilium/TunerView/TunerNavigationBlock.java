package com.filipetrovic.auxilium.TunerView;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.filipetrovic.auxilium.R;

public class TunerNavigationBlock extends RelativeLayout {

    public Drawable inactiveDrawable;
    public Drawable activeDrawable;

    public TunerNavigationBlock(Context context) {
        super(context);
        init(null, 0);
    }

    public TunerNavigationBlock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TunerNavigationBlock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void init(AttributeSet attrs, int defStyle) {
        inflate(getContext(), R.layout.view_tuner_navigation_block, this);
    }

    @BindingAdapter({"app:nbImage"})
    public static void setNavigationBlockImage(TunerNavigationBlock view, int resource) {
        ((ImageView) view.findViewById(R.id.navigationBlockImage)).setImageResource(resource);
    }
    @BindingAdapter({"app:nbImage"})
    public static void setNavigationBlockImage(TunerNavigationBlock view, Drawable img) {
        ((ImageView) view.findViewById(R.id.navigationBlockImage)).setImageDrawable(img);
    }

    @BindingAdapter({"app:nbText"})
    public static void setNavigationBlockText(TunerNavigationBlock view, String text) {
        ((TextView) view.findViewById(R.id.navigationBlockSpecificText)).setText(text);
    }
    @BindingAdapter({"app:nbGroupText"})
    public static void setNavigationBlockGroupText(TunerNavigationBlock view, String text) {
        ((TextView) view.findViewById(R.id.navigationBlockGroupText)).setText(text);
    }
}
