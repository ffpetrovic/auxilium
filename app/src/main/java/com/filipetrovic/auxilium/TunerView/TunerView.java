package com.filipetrovic.auxilium.TunerView;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;

/**
 * TODO: document your custom view class.
 */
public class TunerView extends View {

    /*
        Main tuner view. Works by keeping 4 different copies of
        indicators 4 different variations:
         ACTIVE, INACTIVE, CORRECT and INCORRECT
         and adjusting the alpha of the according one.
     */

    double yDestination = 0.0;
    double yCurrent = 0.0;
    Paint paint = new Paint();
    Drawable indicatorActiveDrawable = Drawable.createFromPath(null);
    Drawable indicatorCorrectDrawable = Drawable.createFromPath(null);
    Drawable indicatorInactiveDrawable = Drawable.createFromPath(null);
    Drawable indicatorIncorrectDrawable = Drawable.createFromPath(null);

    Bitmap bitmap;
    Canvas bitmapCanvas;

    Indicator.INDICATOR_TYPE type = Indicator.INDICATOR_TYPE.ACTIVE;
    int indicatorHeight = 5;
    int indicatorBgHeight = 150;
    boolean needsToDraw = false;

    public TunerView(Context context) {
        super(context, null);
        init(context, null, 0);
    }

    public TunerView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs, 0);
    }

    public TunerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Load attributes
        indicatorActiveDrawable = new Indicator(context, Indicator.INDICATOR_TYPE.ACTIVE, indicatorHeight, indicatorBgHeight);
        indicatorCorrectDrawable = new Indicator(context, Indicator.INDICATOR_TYPE.CORRECT, indicatorHeight, indicatorBgHeight);
        indicatorInactiveDrawable = new Indicator(context, Indicator.INDICATOR_TYPE.INACTIVE, indicatorHeight, indicatorBgHeight);
        indicatorIncorrectDrawable = new  Indicator(context, Indicator.INDICATOR_TYPE.INCORRECT, indicatorHeight, indicatorBgHeight);

        indicatorActiveDrawable.setAlpha(0);
        indicatorCorrectDrawable.setAlpha(0);
        indicatorInactiveDrawable.setAlpha(0);
        indicatorIncorrectDrawable.setAlpha(0);

    }

    void setPercentage(double myY) {
        yDestination = myY;
        invalidate();
    }

    @BindingAdapter("app:indicatorY")
    public static void setIndicatorY(TunerView tunerView, double indicatorY) {
        if(indicatorY != -1.00) {
            tunerView.yDestination = indicatorY;
            tunerView.invalidate();
        }
    }

    @BindingAdapter("app:indicatorType")
    public static void setIndicatorType(TunerView tunerView, Indicator.INDICATOR_TYPE indicatorType) {
        if(indicatorType != null) {
            tunerView.type = indicatorType;
            tunerView.invalidate();
        } else {
            tunerView.type = Indicator.INDICATOR_TYPE.INACTIVE;
            tunerView.invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int increment = 10;

        /*
            If false at the end of onDraw(),
            invalidate() will not be recursively called.
        */
        needsToDraw = false;

        /*
            Don't animate if the difference between
            the destination and current position is small enough.
        */
        if(Math.abs(yCurrent - yDestination) > .1) {
            /*
                Make animation increment as a fraction of the
                difference between destination and current position.
            */
            double posIncrement = (Math.abs(yCurrent - yDestination)) / 8.0;
            if(posIncrement > (double) getHeight() / 10.0)
                posIncrement = (double) getHeight() / 10.0;

            // Animation direction
            if(yCurrent > yDestination) {
                yCurrent -= posIncrement;
            } else {
                yCurrent += posIncrement;
            }

            needsToDraw = true;
        }

        setWillNotDraw(false);

        if(type == Indicator.INDICATOR_TYPE.CORRECT) {
            if(indicatorActiveDrawable.getAlpha() > 0) {
                indicatorActiveDrawable.setAlpha(indicatorActiveDrawable.getAlpha() - increment);
                needsToDraw=true;
            }
            if(indicatorInactiveDrawable.getAlpha() > 0) {
                indicatorInactiveDrawable.setAlpha(indicatorInactiveDrawable.getAlpha() - increment);
                needsToDraw=true;
            }
            if(indicatorIncorrectDrawable.getAlpha() > 0) {
                indicatorIncorrectDrawable.setAlpha(indicatorIncorrectDrawable.getAlpha() - increment);
                needsToDraw=true;
            }
            if(indicatorCorrectDrawable.getAlpha() < 255 - increment) {
                indicatorCorrectDrawable.setAlpha(indicatorCorrectDrawable.getAlpha() + increment);
                needsToDraw=true;
            }
        } else if(type == Indicator.INDICATOR_TYPE.ACTIVE){
            if(indicatorCorrectDrawable.getAlpha() > 0) {
                indicatorCorrectDrawable.setAlpha(indicatorCorrectDrawable.getAlpha() - increment);
                needsToDraw=true;
            }

            if(indicatorActiveDrawable.getAlpha() < 255 - increment) {
                indicatorActiveDrawable.setAlpha(indicatorActiveDrawable.getAlpha() + increment);
                needsToDraw=true;
            }
            if(indicatorIncorrectDrawable.getAlpha() > 0) {
                indicatorIncorrectDrawable.setAlpha(indicatorIncorrectDrawable.getAlpha() - increment);
                needsToDraw=true;
            }
            if(indicatorInactiveDrawable.getAlpha() > 0) {
                indicatorInactiveDrawable.setAlpha(indicatorInactiveDrawable.getAlpha() - increment);
                needsToDraw=true;
            }
        } else if(type == Indicator.INDICATOR_TYPE.INACTIVE){
            if(indicatorCorrectDrawable.getAlpha() > 0) {
                indicatorCorrectDrawable.setAlpha(indicatorCorrectDrawable.getAlpha() - increment);
                needsToDraw=true;
            }
            if(indicatorActiveDrawable.getAlpha() > 0) {
                indicatorActiveDrawable.setAlpha(indicatorActiveDrawable.getAlpha() - increment);
                needsToDraw=true;
            }
            if(indicatorIncorrectDrawable.getAlpha() > 0) {
                indicatorIncorrectDrawable.setAlpha(indicatorIncorrectDrawable.getAlpha() - increment);
                needsToDraw=true;
            }
//            if(indicatorInactiveDrawable.alpha < 255 - increment) {
//                indicatorInactiveDrawable.alpha += increment
//                needsToDraw=true
//            }
        } else if(type == Indicator.INDICATOR_TYPE.INCORRECT){
            if(indicatorCorrectDrawable.getAlpha() > 0) {
                indicatorCorrectDrawable.setAlpha(indicatorCorrectDrawable.getAlpha() - increment);
                needsToDraw=true;
            }
            if(indicatorActiveDrawable.getAlpha() > 0) {
                indicatorActiveDrawable.setAlpha(indicatorActiveDrawable.getAlpha() - increment);
                needsToDraw=true;
            }
            if(indicatorInactiveDrawable.getAlpha() > 0) {
                indicatorInactiveDrawable.setAlpha(indicatorInactiveDrawable.getAlpha() - increment);
                needsToDraw=true;
            }
            if(indicatorIncorrectDrawable.getAlpha() < 255 - increment) {
                indicatorIncorrectDrawable.setAlpha(indicatorIncorrectDrawable.getAlpha() + increment);
                needsToDraw=true;
            }
        }

//        indicatorCorrectDrawable.setBounds(0, cy .toInt(), cx*2, cy.toInt())
//        indicatorCorrectDrawable.draw(canvas)
//        indicatorActiveDrawable.setBounds(0, cy.toInt(), cx*2 , cy.toInt())
//        indicatorActiveDrawable.draw(canvas)
//        indicatorInactiveDrawable.setBounds(0, cy.toInt(), cx*2 , cy .toInt())
//        indicatorInactiveDrawable.draw(canvas)
//        indicatorIncorrectDrawable.setBounds(0, cy.toInt(), cx*2 , cy .toInt())
//        indicatorIncorrectDrawable.draw(canvas)
        double cy = (double) getHeight() - indicatorBgHeight + ((yCurrent - 0) / (100 - 0)) * (indicatorBgHeight - ((double) getHeight() - indicatorBgHeight));


        bitmap = Bitmap.createBitmap(getWidth(), (int) (getWidth() * (55.0 / 375.0)), Bitmap.Config.ARGB_8888);
        bitmapCanvas = new Canvas(bitmap);

        indicatorCorrectDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        indicatorCorrectDrawable.draw(bitmapCanvas);
        indicatorActiveDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        indicatorActiveDrawable.draw(bitmapCanvas);
        indicatorIncorrectDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        indicatorIncorrectDrawable.draw(bitmapCanvas);

        canvas.drawBitmap(bitmap, (float) 0, ((float) cy - (float) (bitmap.getHeight()) / 2f), paint);



        if(needsToDraw) {
            invalidate();
        }
    }
}
