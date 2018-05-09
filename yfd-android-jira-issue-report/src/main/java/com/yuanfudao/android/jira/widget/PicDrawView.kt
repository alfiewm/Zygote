package com.yuanfudao.android.jira.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView

/**
 * Created by meng on 2018/5/13.
 */
class PicDrawView @JvmOverloads constructor(
        context: Context,
        attributes: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ImageView(context, attributes, defStyleAttr) {

    companion object {
        private const val TOUCH_TOLERANCE = 4f
    }

    private var mPath: Path
    private val mPaint: Paint = Paint()
    private val mPaths: MutableList<Path> = mutableListOf()
    private val mUndonePaths: MutableList<Path> = mutableListOf()

    private var mX: Float = 0.toFloat()
    private var mY: Float = 0.toFloat()

    init {
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = Color.RED
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeWidth = 6f
        mPath = Path()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaths.forEach {
            canvas.drawPath(it, mPaint)
        }
        canvas.drawPath(mPath, mPaint)
    }

    private fun onTouchStart(x: Float, y: Float) {
        mUndonePaths.clear()
        mPath.reset()
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun onTouchMove(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    private fun onTouchUp() {
        mPath.lineTo(mX, mY)
        mPaths.add(mPath)
        mPath = Path()
    }

    fun undo() {
        if (mPaths.size > 0) {
            mUndonePaths.add(mPaths.removeAt(mPaths.size - 1))
            invalidate()
        }
    }

    fun redo() {
        if (mUndonePaths.size > 0) {
            mPaths.add(mUndonePaths.removeAt(mUndonePaths.size - 1))
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> onTouchStart(x, y)
            MotionEvent.ACTION_MOVE -> onTouchMove(x, y)
            MotionEvent.ACTION_UP -> onTouchUp()
        }
        invalidate()
        return true
    }
}