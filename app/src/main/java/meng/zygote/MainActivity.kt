package meng.zygote

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
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
    }
}
