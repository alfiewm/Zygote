package meng.zygote

import android.app.NotificationManager
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.pawegio.kandroid.startActivity
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.activity_main.*
import meng.zygote.api.RestAPI

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val restApi = RestAPI()
        restApi.getDemoResponse("endpoint", "50")
        widgetShowcase.setOnClickListener {
            startActivity<WidgetShowcaseActivity>()
        }
        toast("hola from kandroid")

        // example 1
//        val userLayout: ViewGroup = findViewById(R.id.users)
//        for (index in 0 until userLayout.childCount) {
//            val view = userLayout.getChildAt(index)
        // do something with index and view...
//        }
        val userLayout: ViewGroup = findViewById(R.id.users)
        userLayout.forEachIndexed { index, view ->
            // do something with index and view...
        }

        // example 2
        // In an Activity, on API 23+...
        val notifications = getSystemService(NotificationManager::class.java)
        // In an Activity, on all API levels...
        val notifications2 = ContextCompat.getSystemService(this,
                NotificationManager::class.java)
        // In an Activity...
        val notifications3 = systemService<NotificationManager>()


        // example 3
        userLayout.setPadding(
                10, userLayout.paddingTop, 10, userLayout.paddingBottom)
        userLayout.updatePadding(10, 10)
        userLayout.updatePadding(left = 10, right = 10)


        // example 4
//        val rect = userLayout.clipBounds
//        val left = rect.left
//        val top = rect.top
//        val right = rect.right
//        val bottom = rect.bottom
        // use left, top, right, bottom...
//        val (left, top, right, bottom) = userLayout.clipBounds
//        val (left, top, right) = userLayout.clipBounds
        val (left, _, right) = userLayout.clipBounds


        // example 5
        val phoneNumber = "18910527267"
        var onlyDigits = true
        for (c in phoneNumber) {
            if (!c.isDigit()) {
                onlyDigits = false
                break
            }
        }
        val onlyDigits2 = phoneNumber.all { it.isDigit() }
        val onlyDigits3 = TextUtils.isDigitsOnly(phoneNumber)

        for (view in userLayout) {
            // Do something with view...
        }

        userLayout.setOnClickListener {
            // React to click
        }

        userLayout.click {
            // React to click
        }
    }
}

fun View.click(listener: (View) -> Unit) {
    setOnClickListener(listener)
}

inline fun ViewGroup.forEachIndexed(action: (Int, View) -> Unit) {
    for (index in 0 until childCount) {
        action.invoke(index, getChildAt(index))
    }
}

operator fun ViewGroup.iterator() = object : MutableIterator<View> {
    private var index = 0
    override fun hasNext() = index < childCount
    override fun next() =
            getChildAt(index++) ?: throw IndexOutOfBoundsException()

    override fun remove() = removeViewAt(--index)
}

inline fun <reified T> Context.systemService() =
        ContextCompat.getSystemService(this, T::class.java)

inline fun View.updatePadding(
        left: Int = paddingLeft,
        top: Int = paddingTop,
        right: Int = paddingRight,
        bottom: Int = paddingBottom
) {
    setPadding(left, top, right, bottom)
}

inline operator fun Rect.component1() = left
inline operator fun Rect.component2() = right
inline operator fun Rect.component3() = top
inline operator fun Rect.component4() = bottom

inline fun CharSequence.isDigitsOnly() = TextUtils.isDigitsOnly(this)
