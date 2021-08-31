package com.redravencomputing.minipaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs

private const val STROKE_WIDTH = 12f // has to be a float

class MyCanvasView(context: Context) : View(context) {
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    private val paint = Paint().apply {
        color = drawColor
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled
        isDither = true
        style = Paint.Style.STROKE // default: Fill
        strokeJoin = Paint.Join.ROUND // default; Miter
        strokeCap = Paint.Cap.ROUND // default: Butt
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin)
    }

    private var path = Path()

    private var motionTouchEventX: Float? = 0f
    private var motionTouchEventY: Float? = 0f

    private var currentX = 0f
    private var currentY = 0f

    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (::extraBitmap.isInitialized) extraBitmap.recycle() //Used to prevent memory leak by recycling the bitmap
        extraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(extraBitmap, 0f, 0f, null)
    }

    private fun touchStart() {
        path.reset()
        if(motionTouchEventX != null && motionTouchEventY != null) {
            path.moveTo(motionTouchEventX!!, motionTouchEventY!!)
            currentX = motionTouchEventX!!
            currentY = motionTouchEventY!!
        }

    }

    private fun touchMove() {
        if(motionTouchEventX != null && motionTouchEventY != null) {
            val dx = abs(motionTouchEventX!! - currentX)
            val dy = abs(motionTouchEventY!! - currentY)
            if(dx >= touchTolerance || dy >= touchTolerance) {
                // QuadTo() adds a quadratic bezier from the last point,
                // approaching control point (x1,y1), and ending at (x2,y2).
                path.quadTo(currentX, currentY,(motionTouchEventX!! + currentX) / 2, (motionTouchEventY!! + currentY) / 2)
                currentX = motionTouchEventX!!
                currentY = motionTouchEventY!!
                // Draw the path in the extra bitmap to cache it
                extraCanvas.drawPath(path, paint)
            }
            invalidate()
        }
    }

    private fun touchUp() {
        //Reset the path so it doesnt get drqwn again
        path.reset()

    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        motionTouchEventX = event?.x
        motionTouchEventY = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }
}