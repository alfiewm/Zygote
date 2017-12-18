package meng.zygote

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.activity_widget_showcase.*

/**
 * Created by meng on 2017/12/18.
 */
class WidgetShowcaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_showcase)
        pressableIV.setOnClickListener {
            toast("I can still be clicked!")
        }
        pressableTV.setOnClickListener {
            toast("I, the tv, is clicked!")
        }
    }
}