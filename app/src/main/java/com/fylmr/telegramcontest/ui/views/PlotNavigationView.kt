package com.fylmr.telegramcontest.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class PlotNavigationView : View {

    // Plot values
    var values = intArrayOf()
        set(value) {
            field = value
            updateValues()
        }
    var visibleValues = 5
    private var max = Int.MAX_VALUE
    private var min = Int.MIN_VALUE

    // Motion
    private var movingWindow = false
    private var firstTouchX = 0f
    private var lastDifference = 0f

    // Window
    private var windowStart = 0f
    private var windowEnd = 0f
    private var windowShift = 0f

    // Paint
    private val strokeWidth = 8f

    // Measurement
    private val marginW = 32f
    private val marginH = 32f
    private val heightMinusMargin
        get() = height - marginH
    private val widthMinusMargin
        get() = width - marginW
    private val heightCoefficient
        get() = (heightMinusMargin - marginH) / max
    private val horizontalGapBetweenPoints
        get() = (widthMinusMargin - marginW) / (values.size - 1)

    // Listeners
    var onDragListener: ((dx: Float) -> Unit)? = null
    var onScaleListener: ((scaleFactor: Float) -> Unit)? = null


    // ===================================================
    // Creating view
    // ===================================================

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    // ===================================================
    // Drawing
    // ===================================================

    override fun onDraw(canvas: Canvas) {
        if (windowEnd == 0f)
            windowEnd = (widthMinusMargin - marginW) / (Math.min(values.size - 1, visibleValues))

        drawBackground(canvas)
        drawHandles(canvas)
        drawPlotLines(canvas)
    }

    private fun drawHandles(canvas: Canvas) {

    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawRect(0f, 0f, windowStart + windowShift, height.toFloat(), borderPaint)
        canvas.drawRect(windowEnd + windowShift, 0f, width.toFloat(), height.toFloat(), borderPaint)
    }

    private fun drawPlotLines(canvas: Canvas) {
        values.forEachIndexed { i, value ->
            if (i == values.size - 1)
                return@forEachIndexed

            val nextValue = values[i + 1]

            val xStart = marginW + i * horizontalGapBetweenPoints
            val yStart = heightMinusMargin - heightCoefficient * value
            val xStop = marginW + (i + 1) * horizontalGapBetweenPoints
            val yStop = heightMinusMargin - heightCoefficient * nextValue

            canvas.drawLine(
                    xStart, yStart,
                    xStop, yStop,
                    linePaint
            )
        }
    }

    // ===================================================
    // Moving
    // ===================================================

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                firstTouchX = event.rawX
                lastDifference = 0f

                movingWindow = false
                if (event.rawX in windowStart + windowShift..windowEnd + windowShift) {
                    movingWindow = true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (!movingWindow)
                    return false

                val difference = event.rawX - firstTouchX
                windowShift += difference - lastDifference

                invalidate()

                lastDifference = difference

                onDragListener?.invoke(windowShift)
            }
        }

        return true
    }

    fun onScale(scaleFactor: Float) {
        windowStart /= scaleFactor
        windowEnd /= scaleFactor
        invalidate()
    }

    fun drag(dx: Float) {
        this.windowShift = -dx
        invalidate()
    }

    // ===================================================
    // Paint
    // ===================================================

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(20, 0, 0, 0)
        style = Paint.Style.FILL
    }

    private val handlesPaint = borderPaint.apply {
        color = Color.argb(40, 0, 0, 0)
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(15, 200, 13)
        style = Paint.Style.STROKE
        strokeWidth = this@PlotNavigationView.strokeWidth
    }

    // ===================================================
    // Other
    // ===================================================

    private fun updateValues() {
        values.max()?.let { max = it }
        values.min()?.let { min = it }
        invalidate()
    }
}