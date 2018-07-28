package com.jimzjy.speechrehabilitation.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.jimzjy.speechrehabilitation.R

const val PLAYING_LINE_STROKE = 10
const val PLAYING_POINT_STOKE = 20

class Playing : View {
    private var mLineColor = 0
    private var mPointColor = 0
    private var mPointActiveColor = 0
    private var mHeight = 0f
    private var mWidth = 0f

    private val mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    var active = false
        set(value) {
            field = value
            invalidate()
        }

    constructor(ctx: Context) : this(ctx, null)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        val ta = ctx.obtainStyledAttributes(attrs, R.styleable.Playing, defStyleAttr, R.style.Playing)

        mLineColor = ta.getColor(R.styleable.Playing_lineColor, Color.parseColor(DEFAULT_LINE_COLOR))
        mPointColor = ta.getColor(R.styleable.Playing_pointColor, Color.parseColor(DEFAULT_LINE_COLOR))
        mPointActiveColor = ta.getColor(R.styleable.Playing_pointActiveColor, Color.parseColor(DEFAULT_LINE_COLOR))

        ta.recycle()
        init()
    }

    private fun init() {
        mLinePaint.color = mLineColor
        mLinePaint.strokeWidth = PLAYING_LINE_STROKE.toFloat()
        mLinePaint.style = Paint.Style.FILL_AND_STROKE
        mLinePaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width = 400
        val height = 200

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
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            drawLines(it)
        }
    }

    private fun drawLines(canvas: Canvas) {
        val stepX = (mWidth - paddingStart - paddingEnd) / 10
        val baseY = (mHeight - paddingTop - paddingBottom) * 0.1f
        val middleY = mHeight / 2

        var height = baseY
        var xPosition = stepX + paddingStart

        mLinePaint.color = if (active) mPointActiveColor else mPointColor
        mLinePaint.strokeWidth = PLAYING_POINT_STOKE.toFloat()
        canvas.drawPoint(xPosition, middleY, mLinePaint)
        xPosition += stepX

        mLinePaint.color = mLineColor
        mLinePaint.strokeWidth = PLAYING_LINE_STROKE.toFloat()
        for (i in 1..3) {
            canvas.drawLine(xPosition, middleY - height, xPosition, middleY + height, mLinePaint)
            height += baseY
            xPosition += stepX
        }

        for (i in 1..4) {
            canvas.drawLine(xPosition, middleY - height, xPosition, middleY + height, mLinePaint)
            height -= baseY
            xPosition += stepX
        }
    }
}