package com.filipetrovic.auxilium.TunerView

import android.content.Context
import android.databinding.BindingAdapter
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.graphics.drawable.Drawable
import android.graphics.Bitmap


var yDestination: Float = 0f
var yCurrent: Float = 0f
val paint = Paint()
var indicatorActiveDrawable = Drawable.createFromPath(null)
var indicatorCorrectDrawable = Drawable.createFromPath(null)
var indicatorInactiveDrawable = Drawable.createFromPath(null)
var indicatorIncorrectDrawable = Drawable.createFromPath(null)

/**
 * TODO: document your custom view class.
 */
class TunerView : View {

    var type: Indicator.INDICATOR_TYPE = Indicator.INDICATOR_TYPE.ACTIVE;
    var indicatorHeight = 5;
    var indicatorBgHeight = 150;
    var needsToDraw = false

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
//        val a = context.obtainStyledAttributes(
//                attrs, R.styleable.TunerLine, defStyle, 0)
        indicatorActiveDrawable = Indicator(context, Indicator.INDICATOR_TYPE.ACTIVE, indicatorHeight, indicatorBgHeight)
        indicatorCorrectDrawable = Indicator(context, Indicator.INDICATOR_TYPE.CORRECT, indicatorHeight, indicatorBgHeight)
        indicatorInactiveDrawable = Indicator(context, Indicator.INDICATOR_TYPE.INACTIVE, indicatorHeight, indicatorBgHeight)
        indicatorIncorrectDrawable = Indicator(context, Indicator.INDICATOR_TYPE.INCORRECT, indicatorHeight, indicatorBgHeight)

        indicatorActiveDrawable.alpha = 0;
        indicatorCorrectDrawable.alpha = 0;
        indicatorInactiveDrawable.alpha = 0;
        indicatorIncorrectDrawable.alpha = 0;

    }

    fun setPercentage(myY: Double) {
        yDestination = myY.toFloat()
        invalidate()
    }

    @BindingAdapter("app:indicatorY")
    fun setIndicatorY(indicatorY: Double) {
        if(indicatorY != -1.00) {
            yDestination = indicatorY.toFloat()
            invalidate()
        }
    }

    @BindingAdapter("app:indicatorType")
    fun setIndicatorType(indicatorType: Indicator.INDICATOR_TYPE?) {
        if(indicatorType != null) {
            type = indicatorType
            invalidate()
        } else {
            type = Indicator.INDICATOR_TYPE.INACTIVE
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var increment = 10
        needsToDraw = false
        if(Math.abs(yCurrent - yDestination) > .1f) {
            var posIncrement: Float = (Math.abs(yCurrent - yDestination)) / 8
            if(posIncrement > canvas.height / 10)
                posIncrement = canvas.height / 10f
            if(yCurrent > yDestination) {
                yCurrent -= posIncrement
            } else {
                yCurrent += posIncrement
            }
            needsToDraw = true
        }

//        function mapRange (value, a, b, c, d) {
//            return c + ((value - a) / (b - a)) * (d - c);
//        }




        setWillNotDraw(false)



//        Log.d("TESTTEST", yDestination.toString())

        if(type == Indicator.INDICATOR_TYPE.CORRECT) {
            if(indicatorActiveDrawable.alpha > 0) {
                indicatorActiveDrawable.alpha -= increment;
                needsToDraw=true;
            }
            if(indicatorInactiveDrawable.alpha > 0) {
                indicatorInactiveDrawable.alpha -= increment
                needsToDraw=true
            }
            if(indicatorIncorrectDrawable.alpha > 0) {
                indicatorIncorrectDrawable.alpha -= increment
                needsToDraw=true
            }
            if(indicatorCorrectDrawable.alpha < 255 - increment) {
                indicatorCorrectDrawable.alpha += increment
                needsToDraw=true
            }
        } else if(type == Indicator.INDICATOR_TYPE.ACTIVE){
            if(indicatorCorrectDrawable.alpha > 0 + increment) {
                indicatorCorrectDrawable.alpha -= increment
                needsToDraw=true
            }

            if(indicatorActiveDrawable.alpha < 255 - increment) {
                indicatorActiveDrawable.alpha += increment
                needsToDraw=true
            }
            if(indicatorIncorrectDrawable.alpha > 0) {
                indicatorIncorrectDrawable.alpha -= increment
                needsToDraw=true
            }
            if(indicatorInactiveDrawable.alpha > 0) {
                indicatorInactiveDrawable.alpha -= increment
                needsToDraw=true
            }
        } else if(type == Indicator.INDICATOR_TYPE.INACTIVE){
            if(indicatorCorrectDrawable.alpha > 0) {
                indicatorCorrectDrawable.alpha -= increment
                needsToDraw=true
            }
            if(indicatorActiveDrawable.alpha > 0) {
                indicatorActiveDrawable.alpha -= increment
                needsToDraw=true
            }
            if(indicatorIncorrectDrawable.alpha > 0) {
                indicatorIncorrectDrawable.alpha -= increment
                needsToDraw=true
            }
//            if(indicatorInactiveDrawable.alpha < 255 - increment) {
//                indicatorInactiveDrawable.alpha += increment
//                needsToDraw=true
//            }
        } else if(type == Indicator.INDICATOR_TYPE.INCORRECT){
            if(indicatorCorrectDrawable.alpha > 0) {
                indicatorCorrectDrawable.alpha -= increment
                needsToDraw=true
            }
            if(indicatorActiveDrawable.alpha > 0) {
                indicatorActiveDrawable.alpha -= increment
                needsToDraw=true
            }
            if(indicatorInactiveDrawable.alpha > 0) {
                indicatorInactiveDrawable.alpha -= increment
                needsToDraw=true
            }
            if(indicatorIncorrectDrawable.alpha < 255 - increment) {
                indicatorIncorrectDrawable.alpha += increment
                needsToDraw=true
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

        var cx = canvas.width / 2
        var cy = (canvas.height - indicatorBgHeight) + ((yCurrent - 0) / (100 - 0)) * (indicatorBgHeight - (canvas.height - indicatorBgHeight))


        val bitmap = Bitmap.createBitmap(canvas.width, (canvas.width.toFloat() * (55f / 375f)).toInt(), Bitmap.Config.ARGB_8888)
        val bitmapCanvas = Canvas(bitmap)

//        Log.d("AUX_LOG", bitmap.height.toString() +  " " + canvas.width.toString());

        indicatorCorrectDrawable.setBounds(0, 0, bitmap.width, bitmap.height)
        indicatorCorrectDrawable.draw(bitmapCanvas)
        indicatorActiveDrawable.setBounds(0, 0, bitmap.width, bitmap.height)
        indicatorActiveDrawable.draw(bitmapCanvas)
        indicatorIncorrectDrawable.setBounds(0, 0, bitmap.width, bitmap.height)
        indicatorIncorrectDrawable.draw(bitmapCanvas)

        canvas.drawBitmap(bitmap, 0f, cy - bitmap.height / 2, paint)

        if(needsToDraw) {
            invalidate()
        }
    }
}
