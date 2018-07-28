package com.jimzjy.speechrehabilitation.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.jimzjy.speechrehabilitation.R

const val DEFAULT_LINE_COLOR = "#4BC7E7"
const val OUT_LINE_STROKE_WIDTH = 5
const val OUT_INNER_LINE_STROKE_WIDTH = 3
const val LINE_STROKE_WIDTH = 8
const val POINT_STROKE_WIDTH = 20
const val WIDTH_DIVIDE_LINE_CHART = 15
const val HEIGHT_DIVIDE_LINE_CHART = 15

class LineChart : View {
    var lineList = listOf<FloatArray>()
    var lineColorList = listOf<Int>()
    var xTextList = listOf<String>()

    private var mYUnit = ""
    private var mXUnit = ""
    private var mWidth = 0f
    private var mHeight = 0f
    private var mOutLineColor = 0
    private var mOutLineTextColor = 0

    private val mPaintPoint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintOutLine = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintOutText = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(ctx: Context) : this(ctx, null)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        val ta = ctx.obtainStyledAttributes(attrs, R.styleable.LineChart, defStyleAttr, R.style.LineChart)

        mYUnit = ta.getString(R.styleable.LineChart_YUnit)
        mXUnit = ta.getString(R.styleable.LineChart_XUnit)
        mOutLineColor = ta.getColor(R.styleable.LineChart_outLineColor, Color.parseColor(DEFAULT_LINE_COLOR))
        mOutLineTextColor = ta.getColor(R.styleable.LineChart_outLineTextColor, Color.parseColor(DEFAULT_LINE_COLOR))

        ta.recycle()
        init()
    }

    private fun init() {
        mPaintPoint.style = Paint.Style.FILL_AND_STROKE
        mPaintPoint.strokeCap = Paint.Cap.ROUND

        mPaintOutLine.style = Paint.Style.STROKE
        mPaintOutLine.strokeWidth = OUT_LINE_STROKE_WIDTH.toFloat()
        mPaintOutLine.color = mOutLineColor

        mPaintOutText.style = Paint.Style.FILL
        mPaintOutText.textAlign = Paint.Align.CENTER
        mPaintOutText.color = mOutLineTextColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width = 400
        val height = 400

        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT &&
                layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(width, height)
        } else if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(width, heightSize)
        } else if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, height)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mWidth = w.toFloat()
        mHeight = h.toFloat()

        mPaintOutText.textSize = (mWidth + mHeight) / 60f
        mPaintOutLine.textSize = (mWidth + mHeight) / 70f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val list = getLineListForDraw()

        canvas?.let {
            drawOutline(it)
            drawPointLine(it, list)
        }
    }

    private fun getLineListForDraw(): List<FloatArray> {
        val list = mutableListOf<FloatArray>()
        val stepX = mWidth / WIDTH_DIVIDE_LINE_CHART
        val num = xTextList.size
        val stepXT = (mWidth - stepX * 2) / num

        for (i in 0 until lineList.size) {
            val yArray = lineList[i]
            val floatList = mutableListOf<Float>()

            if (yArray.size <= 2) {
                for (i2 in 0 until yArray.size) {
                    if (yArray[i2] != -1f) floatList.addAll(arrayOf(stepXT * i2 + stepXT / 2 + stepX, percentToPosition(yArray[i2])))
                }
//                if (yArray[0] != -1f) floatList.addAll(arrayOf(stepXT * 0 + stepXT / 2 + stepX, percentToPosition(yArray[0])))
//                if (yArray[1] != -1f) floatList.addAll(arrayOf(stepXT * 1 + stepXT / 2 + stepX, percentToPosition(yArray[1])))
            } else {
                if (yArray[0] != -1f) floatList.addAll(arrayOf(stepXT * 0 + stepXT / 2 + stepX, percentToPosition(yArray[0])))
                for (i2 in 1 until yArray.size) {
                    if (yArray[i2] == -1f) continue
                    floatList.addAll(arrayOf(stepXT * i2 + stepXT / 2 + stepX, percentToPosition(yArray[i2])))
                    floatList.addAll(arrayOf(stepXT * i2 + stepXT / 2 + stepX, percentToPosition(yArray[i2])))
                }
            }
            list.add(floatList.toFloatArray())
        }

        return list
    }

    // percent: 0.1 -> 10%
    private fun percentToPosition(percent: Float): Float {
        val stepY = mWidth / HEIGHT_DIVIDE_LINE_CHART

        return (mHeight - stepY * 2) * (1f - percent) + stepY
    }

    private fun drawOutline(canvas: Canvas) {
        val stepX = mWidth / WIDTH_DIVIDE_LINE_CHART
        val stepY = mWidth / HEIGHT_DIVIDE_LINE_CHART

        mPaintOutLine.strokeWidth = OUT_LINE_STROKE_WIDTH.toFloat()
        canvas.drawLine(stepX, stepY, mWidth - stepX, stepY, mPaintOutLine)
        canvas.drawLine(stepX, stepY, stepX, mHeight - stepY, mPaintOutLine)
        canvas.drawLine(stepX, mHeight - stepY, mWidth - stepX, mHeight - stepY, mPaintOutLine)
        canvas.drawLine(mWidth - stepX, mHeight - stepY, mWidth - stepX, stepY, mPaintOutLine)

        val stepY4 = (mHeight - stepY * 2) / 4
        mPaintOutLine.strokeWidth = OUT_INNER_LINE_STROKE_WIDTH.toFloat()
        val lineTextHeight2 = mPaintOutLine.fontSpacing / 2
        val percent = 75
        mPaintOutLine.style = Paint.Style.FILL
        for (i in 1..3) {
            canvas.drawLine(stepX, stepY + stepY4 * i, mWidth - stepX, stepY + stepY4 * i, mPaintOutLine)
            canvas.drawText((percent - (i - 1) * 25).toString(), stepX + lineTextHeight2, stepY + stepY4 * i - lineTextHeight2, mPaintOutLine)
        }
        mPaintOutLine.style = Paint.Style.STROKE

        val num = xTextList.size
        val stepXT = (mWidth - stepX * 2) / num
        val textHeight2 = mPaintOutText.fontSpacing / 2

        for (i in 0 until num) {
            canvas.drawLine(stepXT * i + stepXT / 2 + stepX, mHeight - stepY, stepXT * i + stepXT / 2 + stepX, mHeight - stepY + stepY / 7, mPaintOutLine)
            canvas.drawText(xTextList[i], stepXT * i + stepXT / 2 + stepX, mHeight - stepY / 2 + textHeight2, mPaintOutText)
        }

        val textPath = Path()
        val unitTextWidth = mPaintOutText.measureText(mYUnit)
        textPath.moveTo(stepX / 2, mHeight / 2 + unitTextWidth / 2)
        textPath.lineTo(stepX / 2, mHeight / 2 - unitTextWidth / 2)
        canvas.drawTextOnPath(mYUnit, textPath, 0f, mPaintOutText.fontSpacing / 2, mPaintOutText)
    }

    private fun drawPointLine(canvas: Canvas, pointList: List<FloatArray>) {
        for (i in 0 until pointList.size) {
            if (lineColorList.size >= i + 1) {
                mPaintPoint.color = lineColorList[i]
            } else {
                mPaintPoint.color = Color.parseColor(DEFAULT_LINE_COLOR)
            }

            mPaintPoint.strokeWidth = LINE_STROKE_WIDTH.toFloat()
            canvas.drawLines(pointList[i], mPaintPoint)
            mPaintPoint.strokeWidth = POINT_STROKE_WIDTH.toFloat()
            canvas.drawPoints(pointList[i], mPaintPoint)
        }
    }

    fun rePaint() {
        invalidate()
    }
}