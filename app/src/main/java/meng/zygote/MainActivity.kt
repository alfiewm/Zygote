package meng.zygote

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.pawegio.kandroid.startActivity
import com.yuanfudao.android.jira.JIRAInfoProvider
import com.yuanfudao.android.jira.JIRAIssueReport
import com.yuanfudao.android.jira.ui.JIRAEntryActivity
import kotlinx.android.synthetic.main.activity_main.*
import meng.zygote.api.RestAPI

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val restApi = RestAPI()
        restApi.getDemoResponse("endpoint", "50")
        widgetShowcase.setOnClickListener {
            startActivity<WidgetShowcaseActivity>()
        }
        JIRAIssueReport.init(object : JIRAInfoProvider {
            override val projectKeys: List<String>
                get() = listOf("YFD", "YFDL")
            override val appVersion: String
                get() = "6.7.0"
            override val reporters: List<String>
                get() = listOf("wangxin", "cuiyt", "wangmeng")
            override val assignees: List<String>
                get() = listOf("wangmeng", "liqiang", "liangzy")
            override val reporterPhoneMap: Map<String, List<String>>
                get() = mapOf(
                        "wangxin" to listOf("10088883585", "10038587654"),
                        "cuiyt" to listOf("14588884001", "14588884002"),
                        "wangmeng" to listOf("18910527267", "10088882131"))
            override val loginUserPhone: String
                get() = "18910527267"
        }, application)
        bugReport.setOnClickListener {
            startActivity(Intent(this, JIRAEntryActivity::class.java))
        }
    }
}
