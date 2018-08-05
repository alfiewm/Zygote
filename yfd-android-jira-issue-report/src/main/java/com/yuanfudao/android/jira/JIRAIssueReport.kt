package com.yuanfudao.android.jira

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.annotation.WorkerThread
import android.text.TextUtils
import android.util.Log
import com.yuanfudao.android.jira.api.*
import com.yuanfudao.android.jira.service.ShakeDetectService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private val issueApi: IssueApi by lazy {
        IssueApi.createService()
    }

    @WorkerThread
    fun queryIssue(issueSign: String): Pair<Boolean, Issue?> {
        val response = issueApi.searchIssues(SearchIssueBody(jql = "text ~ \"$issueSign\"",
            fields = listOf("status"))).execute()
        val searchResponse: SearchResponse? = response.body()
        if (!response.isSuccessful || searchResponse == null || searchResponse.issues.isEmpty()) {
            return Pair(false, null)
        }
        return Pair(true, searchResponse.issues[0])
    }

    @WorkerThread
    fun reopenIssue(issueKey: String): Boolean {
        val transitionPostBody = TransitionPostBody(TransitionFields(TRANSITION_REOPEN))
        return issueApi.transitionIssue(issueKey, transitionPostBody).execute().isSuccessful
    }

    @WorkerThread
    fun createIssue(issueFields: IssueFields): String {
        val response = issueApi.createIssue(IssuePostBody(issueFields)).execute()
        val issueResponse: IssueResponse? = response.body()
        if (!response.isSuccessful || issueResponse == null) {
            return ""
        }
        return issueResponse.key
    }
}