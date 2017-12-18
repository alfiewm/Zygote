package meng.zygote.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView

/**
 * Created by meng on 2017/12/18.
 */
class PressableTextView : TextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when {
            event?.action == MotionEvent.ACTION_DOWN -> {
                alpha = 0.5f
            }
            event?.action == MotionEvent.ACTION_UP || event?.action == MotionEvent.ACTION_CANCEL -> {
                alpha = 1f
            }
        }
        return super.onTouchEvent(event)
    }
}