package com.filipetrovic.auxilium.TunerView;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.filipetrovic.auxilium.R;

/**
 * TODO: document your custom view class.
 */
public class Button extends LinearLayout {
    private String buttonText;
    private String buttonVariant;
    private Boolean buttonGhost;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private View rootView;
    private TextView textView;

    public Button(Context context) {
        super(context);
        init(null, 0);
    }

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public Button(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.Button, defStyle, 0);

        buttonText = a.getString(
                R.styleable.Button_text);

        buttonVariant = a.getString(
                R.styleable.Button_variant);

        buttonGhost = a.getBoolean(
                R.styleable.Button_ghost, false);

        a.recycle();



        rootView = inflate(getContext(), R.layout.view_button, this);
        textView = rootView.findViewById(R.id.button);
        textView.setText(buttonText);
        textView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                rootView.onTouchEvent(motionEvent);
                return false;
            }
        });

        rootView.setPadding(
                getPaddingLeft(),
                getPaddingTop(),
                getPaddingRight(),
                getPaddingBottom());

        updateStyle();
    }


    public void updateStyle() {
        if(!buttonGhost) {
            if(buttonVariant.equals(getContext().getString(R.string.button_variant_white))) {
                rootView.setBackground(getContext().getDrawable(R.drawable.button_bg_white));
                textView.setTextColor(
                        ContextCompat.getColor(getContext(), R.color.colorWhite));
            } else if(buttonVariant.equals(getContext().getString(R.string.button_variant_accent))) {
                rootView.setBackground(getContext().getDrawable(R.drawable.button_bg_accent));
                textView.setTextColor(
                        ContextCompat.getColor(getContext(), R.color.colorAccent));
            } else if(buttonVariant.equals(getContext().getString(R.string.button_variant_danger))) {
                rootView.setBackground(getContext().getDrawable(R.drawable.button_bg_danger));
                textView.setTextColor(
                        ContextCompat.getColor(getContext(), R.color.colorDanger));
            } else if(buttonVariant.equals(getContext().getString(R.string.button_variant_fade))) {
                rootView.setBackground(getContext().getDrawable(R.drawable.button_bg_white));
                textView.setTextColor(
                        ContextCompat.getColor(getContext(), R.color.colorTextSecondary));
            }
        } else {
            if(buttonVariant.equals(getContext().getString(R.string.button_variant_white))) {
                rootView.setBackground(getContext().getDrawable(R.drawable.button_bg_ghost_white));
                textView.setTextColor(
                        ContextCompat.getColor(getContext(), R.color.colorWhite));
            } else if(buttonVariant.equals(getContext().getString(R.string.button_variant_accent))) {
                rootView.setBackground(getContext().getDrawable(R.drawable.button_bg_ghost_accent));
                textView.setTextColor(
                        ContextCompat.getColor(getContext(), R.color.colorAccent));
            } else if(buttonVariant.equals(getContext().getString(R.string.button_variant_danger))) {
                rootView.setBackground(getContext().getDrawable(R.drawable.button_bg_ghost_danger));
                textView.setTextColor(
                        ContextCompat.getColor(getContext(), R.color.colorDanger));
            } else if(buttonVariant.equals(getContext().getString(R.string.button_variant_fade))) {
                rootView.setBackground(getContext().getDrawable(R.drawable.button_bg_ghost_white));
                textView.setTextColor(
                        ContextCompat.getColor(getContext(), R.color.colorTextSecondary));
            }
        }
    }

    public String getButtonVariant() {
        return buttonVariant;
    }

    public void setButtonVariant(String buttonVariant) {
        this.buttonVariant = buttonVariant;
    }

    public Boolean getButtonGhost() {
        return buttonGhost;
    }

    public void setButtonGhost(Boolean buttonGhost) {
        this.buttonGhost = buttonGhost;
    }

    public void setText(String text) {
        textView.setText(text);
    }
}
