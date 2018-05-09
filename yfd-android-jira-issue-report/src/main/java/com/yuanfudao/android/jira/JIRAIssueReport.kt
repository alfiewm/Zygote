package com.yuanfudao.android.jira

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.yuanfudao.android.jira.service.ShakeDetectService

/**
 * Created by meng on 2018/5/8.
 */
object JIRAIssueReport {

    var config: JIRAInfoProvider? = null
        private set
    private lateinit var applicationContext: Application

    fun init(jiraInfoProvider: JIRAInfoProvider, context: Application) {
        this.config = jiraInfoProvider
        this.applicationContext = context
        context.startService(Intent(context, ShakeDetectService::class.java))
    }

    fun isAppForeground(): Boolean {
        val activityManager = applicationContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val tasksInfo = activityManager.getRunningTasks(1)
        val packageName = applicationContext.packageName
        return TextUtils.equals(packageName, tasksInfo[0].topActivity.packageName)
    }
}