package meng.zygote

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import com.pawegio.kandroid.startActivity
import com.pawegio.kandroid.toast
import com.yuanfudao.android.jira.JIRAInfoProvider
import com.yuanfudao.android.jira.JIRAIssueReport
import com.yuanfudao.android.jira.api.IssueFields
import com.yuanfudao.android.jira.api.JIRAObj
import com.yuanfudao.android.jira.api.Project
import kotlinx.android.synthetic.main.activity_main.*
import meng.zygote.api.RestAPI

class MainActivity : Activity() {

    private var workerThread: HandlerThread? = null
    private var workerHandler: Handler? = null
    private var mainHandler: Handler? = null

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
        mainHandler = Handler()
        workerThread = HandlerThread("jira-report")
        workerThread?.start()
        workerHandler = Handler(workerThread?.looper)
        bugReport.setOnClickListener {
            workerHandler?.post { doJiraStuff() }
        }
    }

    private fun doJiraStuff() {
        val leakHash = "9cc398d67d71040d68bbae28437601cea5baa743"
        val oldIssue = JIRAIssueReport.queryIssue(leakHash)
        if (oldIssue != null) {
            if (oldIssue.isResolved()) {
                val result = JIRAIssueReport.reopenIssue(oldIssue.key)
                if (result) {
                    mainHandler?.post { toast("reopened issue ${oldIssue.key}") }
                } else {
                    mainHandler?.post { toast("reopen issue ${oldIssue.key} failed") }
                }
            } else {
                mainHandler?.post { toast("issue already exist ${oldIssue.key}") }
            }
            return
        }
        val issue = IssueFields().apply {
            project = Project("YFD")
            issuetype = JIRAObj("Bug")
            priority = JIRAObj("Medium")
            labels = listOf("memory-leak", "android")
            environment = "${Build.MANUFACTURER}/${Build.MODEL}/${Build.VERSION.SDK_INT}"
            assignee = JIRAObj("wangmeng")
            summary = "【MemoryLeak】 $leakHash"
            description = "E  java.lang.RuntimeException: LessonChannelsFragment leak from HandlerThread (holder=THREAD, type=LOCAL)\n" +
                "E      at android.os.HandlerThread.<Java Local>(HandlerThread.java:42)\n" +
                "E      at android.os.Message.callback(Message.java:42)\n" +
                "E      at com.fenbi.tutor.module.lesson.home.banner.BannerCtrl\$2.this\$0(BannerCtrl\$2.java:42)\n" +
                "E      at com.fenbi.tutor.module.lesson.home.banner.BannerCtrl.bannerContainer(BannerCtrl.java:42)\n"
        }
        val issueKey = JIRAIssueReport.createIssue(issue)
        if (issueKey.isBlank()) {
            mainHandler?.post { toast("create issue failed") }
        } else {
            mainHandler?.post { toast("create issue success : $issueKey") }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        workerThread?.join()
    }
}
