package meng.zygote.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView

/**
 * Created by meng on 2017/12/18.
 */
class ClickableImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : ImageView(context, attrs, defStyleAttr) {

    init {
        setOnTouchListener { _, event ->
            when {
                event.action == MotionEvent.ACTION_DOWN -> {
                    alpha = 0.5f
                }
                event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL -> {
                    alpha = 1f
                }
            }
            true
        }
    }
}