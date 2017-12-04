package meng.zygote

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import meng.zygote.api.RestAPI

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val restApi = RestAPI()
        restApi.getDemoResponse("endpoint", "50")
    }
}
