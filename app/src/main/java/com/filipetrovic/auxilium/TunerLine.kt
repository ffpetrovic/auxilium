package com.filipetrovic.auxilium

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.support.constraint.solver.widgets.Rectangle
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

/**
 * TODO: document your custom view class.
 */
class TunerLine : View {
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
        val a = context.obtainStyledAttributes(
                attrs, R.styleable.TunerLine, defStyle, 0)
    }

    private fun invalidateTextPaintAndMeasurements() {
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var p = Paint()
        p.color = Color.RED

        canvas.drawRect(0f,0f, 50f, 50f, p);
    }
;}
