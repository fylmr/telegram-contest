package com.fylmr.telegramcontest.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.ScaleGestureDetector
import android.view.View

class PlotView : View {

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
    private var dx = 0f
    private var firstTouchX = 0f
    private var lastDifference = 0f

    // Paint
    private val strokeWidth = 8f
    private val guidelineTextSize = 32f

    // Measurement
    private val marginW = 64f
    private val marginH = 64f
    private val heightMinusMargin
        get() = height - marginH
    private val widthMinusMargin
        get() = width - marginW
    private val heightCoefficient
        get() = (heightMinusMargin - marginH) / max
    private val horizontalGapBetweenPoints: Float
        get() = (widthMinusMargin - marginW) * zoomFactor / (Math.min(values.size - 1, visibleValues - 1))
    private var zoomFactor = 1f

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

        // draw grid
        drawGuidelines(canvas)

        // draw bottom titles
        drawBottomTitles(canvas)

        // draw lines
        drawPlotLines(canvas)

        // draw points
        drawPlotPoints(canvas)

    }

    private fun drawGuidelines(canvas: Canvas) {
        val maxRounded = if (max > 100)
            max.div(10) * 10
        else
            max

        val step = maxRounded / 5

        (0..4).forEach {
            val yPos = it * step * heightCoefficient

            canvas.drawLine(
                    marginW / 2, heightMinusMargin - yPos,
                    width - marginW / 2, heightMinusMargin - yPos,
                    gridPaint
            )

            canvas.drawText(
                    "${it * step}",
                    marginW / 2, heightMinusMargin - guidelineTextSize - yPos,
                    horizontalGuideTextPaint
            )
        }
    }

    private fun drawBottomTitles(canvas: Canvas) {
        values.forEachIndexed { i, value ->
            canvas.drawText(
                    "$value", //todo key instead of value
                    marginW + i * horizontalGapBetweenPoints + dx,
                    heightMinusMargin + guidelineTextSize,
                    bottomGuideTextPaint
            )
        }
    }

    private fun drawPlotLines(canvas: Canvas) {
        values.forEachIndexed { i, value ->
            if (i == values.size - 1)
                return@forEachIndexed

            val nextValue = values[i + 1]

            val xStart = marginW + i * horizontalGapBetweenPoints + dx
            val yStart = heightMinusMargin - heightCoefficient * value
            val xStop = marginW + (i + 1) * horizontalGapBetweenPoints + dx
            val yStop = heightMinusMargin - heightCoefficient * nextValue

            canvas.drawLine(
                    xStart, yStart,
                    xStop, yStop,
                    linePaint
            )
        }
    }

    private fun drawPlotPoints(canvas: Canvas) {
        values.forEachIndexed { i, value ->
            val xPos = marginW + i * horizontalGapBetweenPoints + dx
            val yPos = heightMinusMargin - heightCoefficient * value
            canvas.drawCircle(
                    xPos, yPos,
                    strokeWidth * 2,
                    dotBackgroundPaint
            )
            canvas.drawCircle(
                    xPos, yPos,
                    strokeWidth,
                    dotForegroundPaint
            )
        }
    }

    // ===================================================
    // Moving
    // ===================================================

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        scaleDetector.onTouchEvent(event)

        if (scaleDetector.isInProgress)
            return true

        when (event.action) {

            ACTION_DOWN -> {
                firstTouchX = event.rawX
                lastDifference = 0f
            }

            ACTION_MOVE -> {
                val difference = event.rawX - firstTouchX
                dx += difference - lastDifference

                invalidate()

                onDragListener?.invoke(dx)

                lastDifference = difference
            }
        }

        return true
    }

    fun onScale(scaleFactor: Float) {
        zoomFactor *= scaleFactor
        invalidate()
    }

    fun drag(dx: Float) {
        this.dx = dx
        invalidate()
    }


    // ===================================================
    // Paints
    // ===================================================

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(15, 200, 13)
        style = Paint.Style.STROKE
        strokeWidth = this@PlotView.strokeWidth
    }

    private val dotBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(15, 200, 13)
    }

    private val dotForegroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 255, 255)
    }

    private val horizontalGuideTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(200, 200, 200)
        textSize = guidelineTextSize
        textAlign = Paint.Align.LEFT
    }

    private val bottomGuideTextPaint = horizontalGuideTextPaint.apply {
        textAlign = Paint.Align.CENTER
    }


    private val gridPaint = horizontalGuideTextPaint

    // ===================================================
    // Listeners
    // ===================================================
    var onDragListener: ((dx: Float) -> Unit)? = null

    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {

            detector?.scaleFactor?.let {
                onScale(it)
                onScaleListener?.invoke(it)
            }

            return true
        }
    }

    private val scaleDetector = ScaleGestureDetector(context, scaleListener)
    var onScaleListener: ((scaleFactor: Float) -> Unit)? = null

    // ===================================================
    // Other
    // ===================================================

    private fun updateValues() {
        values.max()?.let { max = it }
        values.min()?.let { min = it }
        invalidate()
    }

}