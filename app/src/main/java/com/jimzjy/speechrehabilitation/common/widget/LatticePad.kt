package com.jimzjy.speechrehabilitation.common.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.jimzjy.speechrehabilitation.R
import java.util.*

const val LATTICE_PAD_MODE_EDIT_ADD = 0
const val LATTICE_PAD_MODE_EDIT_REMOVE = 1
const val LATTICE_PAD_MODE_DISPLAY = 2

class LatticePad : View {
    private var mPointColor = 0
    private var mStrokeColor = 0
    private var mXCount = 0
    private var mYCount = 0
    private var mRadius = 0f
    private val mStartRect = RectF(0f, 0f, 0f, 0f)
    val pointList = mutableListOf<PointPosition>()
    var backStack = LinkedList<List<PointPosition>>()

    private var mPaintPoint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPaintStroke = Paint(Paint.ANTI_ALIAS_FLAG)
    var mode = LATTICE_PAD_MODE_EDIT_ADD

    constructor(ctx: Context) : this(ctx, null)

    constructor(ctx: Context, attrs: AttributeSet?) : this(ctx, attrs, 0)

    constructor(ctx: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        val ta = ctx.obtainStyledAttributes(attrs, R.styleable.LatticePad, defStyleAttr, R.style.LatticePad)

        mPointColor = ta.getColor(R.styleable.LatticePad_lattice_pointColor, Color.parseColor(DEFAULT_LINE_COLOR))
        mStrokeColor = ta.getColor(R.styleable.LatticePad_strokeColor, Color.parseColor(DEFAULT_LINE_COLOR))
        mXCount = ta.getInt(R.styleable.LatticePad_xPointCount, 0)
        mYCount = ta.getInt(R.styleable.LatticePad_yPointCount, 0)
        if (ta.getBoolean(R.styleable.LatticePad_displayMode, false)) {
            mode = LATTICE_PAD_MODE_DISPLAY
        }

        ta.recycle()
        init()
    }

    private fun init() {
        mPaintPoint.style = Paint.Style.FILL
        mPaintPoint.color = mPointColor

        mPaintStroke.style = Paint.Style.STROKE
        mPaintStroke.strokeWidth = 2f
        mPaintStroke.color = mStrokeColor

        backStack.push(emptyList())
    }

    // 2.5R * (N - 1) + 4R = w or h
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val rX: Float = w / (2.5f * mXCount + 1.5f)
        val rY: Float = h / (2.5f * mYCount + 1.5f)
        mRadius = if (rX < rY) rX else rY

        mStartRect.left = (w - (2.5f * mRadius * (mXCount - 1) + 2 * mRadius)) / 2
        mStartRect.top = (h - (2.5f * mRadius * (mYCount - 1) + 2 * mRadius)) / 2
        mStartRect.right = mStartRect.left + mRadius * 2
        mStartRect.bottom = mStartRect.top + mRadius * 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            drawPointCircle(it)
        }
    }

    @Suppress("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (mode == LATTICE_PAD_MODE_DISPLAY) return true

        event?.let {
            val tmpX = it.x
            val tmpY = it.y

            when(it.action) {
                MotionEvent.ACTION_DOWN -> {
                    when(mode) {
                        LATTICE_PAD_MODE_EDIT_ADD -> addPoint(tmpX, tmpY)
                        LATTICE_PAD_MODE_EDIT_REMOVE -> removePoint(tmpX, tmpY)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    when(mode) {
                        LATTICE_PAD_MODE_EDIT_ADD -> addPoint(tmpX, tmpY)
                        LATTICE_PAD_MODE_EDIT_REMOVE -> removePoint(tmpX, tmpY)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    addToBackStack()
                }
                MotionEvent.ACTION_CANCEL -> {
                    addToBackStack()
                }
            }
            return true
        }
        return true
    }

    private fun addPoint(x: Float, y: Float) {
        val p = getPointPosition(x, y)
        if (p !in pointList && p.x < mXCount && p.y < mYCount && p.x >= 0 && p.y >= 0) {
            pointList.add(p)
            invalidate()
        }
    }

    private fun removePoint(x: Float, y: Float) {
        val p = getPointPosition(x, y)
        if (p in pointList && p.x < mXCount && p.y < mYCount && p.x >= 0 && p.y >= 0) {
            pointList.remove(p)
            invalidate()
        }
    }

    private fun getPointPosition(x: Float, y: Float): PointPosition {
        val l = mStartRect.left - 0.25 * mRadius
        val t = mStartRect.top - 0.25 * mRadius
        val step = 2.5f * mRadius

        val pX = ((x - l) / step).toInt()
        val pY = ((y - t) / step).toInt()

        return PointPosition(pX, pY)
    }

    private fun drawPointCircle(canvas: Canvas) {
        val rect = RectF()

        if (mode == LATTICE_PAD_MODE_EDIT_ADD || mode == LATTICE_PAD_MODE_EDIT_REMOVE) {
            for (i in 0 until mXCount) {
                rect.left = mStartRect.left + mRadius * 2.5f * i
                rect.top = mStartRect.top
                rect.right = rect.left + mRadius * 2
                rect.bottom = rect.top + mRadius * 2

                for (j in 0 until mYCount) {
                    rect.top = mStartRect.top + mRadius * 2.5f * j
                    rect.bottom = rect.top + mRadius * 2

                    canvas.drawOval(rect, mPaintStroke)
                }
            }
        }

        pointList.forEach {
            rect.left = mStartRect.left + mRadius * 2.5f * it.x
            rect.top = mStartRect.top + mRadius * 2.5f * it.y
            rect.right = rect.left + mRadius * 2
            rect.bottom = rect.top + mRadius * 2

            canvas.drawOval(rect, mPaintPoint)
        }
    }

    private fun addToBackStack() {
        val list = mutableListOf<PointPosition>()
        list.addAll(pointList)
        backStack.push(list)
    }

    fun setPointPosition(list: List<PointPosition>) {
        pointList.clear()
        pointList.addAll(list)

        addToBackStack()
        invalidate()
    }

    fun back() {
        if (backStack.size > 1) {
            backStack.pop()
            pointList.clear()
            pointList.addAll(backStack.peek())

            invalidate()
        }
    }

    fun clear() {
        addToBackStack()

        pointList.clear()
        invalidate()
    }
}

data class PointPosition(val x: Int, val y: Int)