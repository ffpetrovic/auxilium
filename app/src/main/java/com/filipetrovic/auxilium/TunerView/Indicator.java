package com.filipetrovic.auxilium.TunerView;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.filipetrovic.auxilium.R;

public class Indicator extends Drawable {

    private int indicatorHeight, indicatorBgHeight;

    public enum INDICATOR_TYPE {
        ACTIVE,
        CORRECT,
        INACTIVE,
        INCORRECT
    }

    Paint mPaint;
    Drawable indicatorDrawable;

    Rect mRect;
    Rect mRectBg;
    Path mPath;

    private INDICATOR_TYPE mType;

    public Indicator(Context context, INDICATOR_TYPE type, int indicatorHeight, int indicatorBgHeight) {
        mRect = new Rect();
        mRectBg = new Rect();

        this.indicatorHeight = indicatorHeight;
        this.indicatorBgHeight = indicatorBgHeight;

        if(type == INDICATOR_TYPE.ACTIVE) {
            indicatorDrawable = ContextCompat.getDrawable(context, R.drawable.indicator_active);
        } else if(type == INDICATOR_TYPE.CORRECT) {
            indicatorDrawable = ContextCompat.getDrawable(context, R.drawable.indicator_correct);
        } else if(type == INDICATOR_TYPE.INACTIVE) {
            indicatorDrawable = ContextCompat.getDrawable(context, R.drawable.indicator_inactive);
        } else if(type == INDICATOR_TYPE.INCORRECT) {
            indicatorDrawable = ContextCompat.getDrawable(context, R.drawable.indicator_incorrect);
        }

        mType = type;
    }



    @Override
    protected void onBoundsChange(Rect bounds) {
        mRectBg.set(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        indicatorDrawable.setDither(true);
        indicatorDrawable.setBounds(mRectBg);
        indicatorDrawable.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        indicatorDrawable.setAlpha(alpha);
    }

    @Override
    public int getAlpha() {
        return indicatorDrawable.getAlpha();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }


}