package com.yuanfudao.android.jira.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.yuanfudao.android.jira.JIRAIssueReport
import com.yuanfudao.android.jira.R
import com.yuanfudao.android.jira.api.*
import kotlinx.android.synthetic.main.jira_activity_create_issue.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class JIRACreateIssueActivity : AppCompatActivity() {

    companion object {

        const val ARG_SCREENSHOT_FILE_PATH = "SCREENSHOT_FILE_PATH"
        private const val PREF_KEY_ISSUE_PROJECT = "PREF_KEY_ISSUE_PROJECT"
        private const val PREF_KEY_ISSUE_REPORTER = "PREF_KEY_ISSUE_REPORTER"
        private const val PREF_KEY_ISSUE_ASSIGNEE = "PREF_KEY_ISSUE_ASSIGNEE"
        private const val PREF_KEY_ISSUE_FIX_VERSION = "PREF_KEY_ISSUE_FIX_VERSION"
    }

    private var progressDialog: ProgressDialog? = null

    private val screenshotPath: String by lazy {
        intent.getStringExtra(ARG_SCREENSHOT_FILE_PATH) ?: ""
    }

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("jira-issue-report-config", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.jira_activity_create_issue)
        title = "Fire in the Hole!"
        initViews()
        createBtn.setOnClickListener {
            createIssue()
        }
    }

    private fun initViews() {
        val config = JIRAIssueReport.config

        val defaultProject = sharedPreferences.getString(PREF_KEY_ISSUE_PROJECT, config?.projectKeys?.get(0))
        issueProject.setText(defaultProject)
        issueProject.choices = config?.projectKeys ?: listOf()

        issueType.setText("Bug")
        issueType.choices = listOf("Bug", "Task", "Story")

        issuePriority.setText("Medium")
        issuePriority.choices = listOf("Highest", "High", "Medium", "Low", "Lowest")

        issueLabels.setText("Android")
        issueEnvironment.setText("${Build.MANUFACTURER}/${Build.MODEL}/${Build.VERSION.SDK_INT}")
        val defaultFixVersion = sharedPreferences.getString(
                PREF_KEY_ISSUE_FIX_VERSION, "V${config?.appVersion}")
        issueFixVersion.setText(defaultFixVersion)

        if (config != null) {
            val defaultReporter = sharedPreferences.getString(PREF_KEY_ISSUE_REPORTER, config.defaultReporter())
            issueReporter.setText(defaultReporter)
            issueReporter.choices = config.reporters

            val defaultAssignee = sharedPreferences.getString(PREF_KEY_ISSUE_ASSIGNEE, config.assignees[0])
            issueAssignee.setText(defaultAssignee)
            issueAssignee.choices = config.assignees
        }

        issueSummary.setText("A BUG")
        issueSummary.requestFocus()

        issueDescription.setText("账号:${config?.loginUserPhone}")
    }

    private fun createIssue() {
        val issue = Issue().apply {
            project = Project(issueProject.text.toString())
            issuetype = JIRAObj(issueType.text.toString())
            priority = JIRAObj(issuePriority.text.toString())
            labels = issueLabels.text.split(" ")
            environment = issueEnvironment.text.toString()
            fixVersions = listOf(JIRAObj(issueFixVersion.text.toString()))
            reporter = JIRAObj(issueReporter.text.toString())
            assignee = JIRAObj(issueAssignee.text.toString())
            summary = issueSummary.text.toString()
            description = issueDescription.text.toString()
        }

        progressDialog = ProgressDialog.show(this, "", "创建中...")
        IssueApi.createService().createIssue(IssuePostBody(issue))
                .enqueue(object : Callback<IssueResponse> {

                    override fun onFailure(call: Call<IssueResponse>, t: Throwable) {
                        progressDialog?.dismiss()
                        alertError(null)
                        appendLog("create issue connection error ${t.message}")
                    }

                    override fun onResponse(call: Call<IssueResponse>, response: Response<IssueResponse>) {
                        if (response.isSuccessful && response.body() != null) {
                            appendLog("create issue success: ${response.body().key}")
                            uploadAttachmentsIfAny(response.body().key)
                        } else {
                            progressDialog?.dismiss()
                            alertError(response.errorBody())
                            appendLog("create issue server error")
                        }
                    }
                })
    }

    private fun uploadAttachmentsIfAny(issueKey: String) {
        val file = File(screenshotPath)
        if (!file.exists()) {
            progressDialog?.dismiss()
            appendLog("could not find screenshot file : $screenshotPath")
            alertSuccess(issueKey)
            return
        }
        val fileUri = Uri.fromFile(file)
        var mimeType: String? = contentResolver.getType(fileUri)
        if (mimeType == null) {
            mimeType = "image/*"
        }
        val requestBody = RequestBody.create(MediaType.parse(mimeType), file)
        val multipartBody = MultipartBody.Part.createFormData("file", file.name, requestBody)
        appendLog("screenshot $screenshotPath uploading...")
        AttachmentApi.createService().addAttachments(issueKey, multipartBody)
                .enqueue(object : Callback<Any> {

                    override fun onFailure(call: Call<Any>, t: Throwable) {
                        progressDialog?.dismiss()
                        alertSuccess(issueKey)
                        toast("Upload screenshot failed")
                        appendLog("upload screenshot connection error - ${t.message}")
                    }

                    override fun onResponse(call: Call<Any>, response: Response<Any>) {
                        progressDialog?.dismiss()
                        alertSuccess(issueKey)
                        if (response.isSuccessful) {
                            appendLog("upload screenshot success")
                        } else {
                            toast("Upload screenshot failed")
                            appendLog("upload screenshot servererror")
                        }
                    }
                })
    }

    private fun alertSuccess(issueKey: String) {
        notifyUsers(issueKey)
        updateSP()
        AlertDialog.Builder(this)
                .setMessage("创建成功： $issueKey")
                .setPositiveButton("知道了", null)
                .setOnDismissListener { finish() }
                .show()
    }

    private fun notifyUsers(issueKey: String) {
        val wechatMessage = WechatMessage(
                content = "有一个新 Bug 与你相关：https://jira.zhenguanyu.com/browse/$issueKey")
        if (notifyReporterCheckBox.isChecked) {
            wechatMessage.touser.add(issueReporter.text.toString())
        }
        if (notifyAssigneeCheckBox.isChecked) {
            wechatMessage.touser.add(issueAssignee.text.toString())
        }
        if (wechatMessage.touser.isEmpty()) {
            return
        }
        WechatWorkApi.createService().sendMessage(wechatMessage)
                .enqueue(object : Callback<Any> {

                    override fun onFailure(call: Call<Any>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<Any>?, response: Response<Any>?) {
                    }
                })
    }

    private fun updateSP() {
        sharedPreferences.edit {
            putString(PREF_KEY_ISSUE_PROJECT, issueProject.text.toString())
            putString(PREF_KEY_ISSUE_REPORTER, issueReporter.text.toString())
            putString(PREF_KEY_ISSUE_ASSIGNEE, issueAssignee.text.toString())
            putString(PREF_KEY_ISSUE_FIX_VERSION, issueFixVersion.text.toString())
        }
    }

    private fun alertError(responseBody: ResponseBody?) {
        val errorMessage: String = if (responseBody != null) {
            try {
                val errorJson: JsonElement = JsonParser().parse(responseBody.string())
                errorJson.asJsonObject["errors"]
                        .asJsonObject.entrySet()
                        .joinToString("\n") { it.key.toUpperCase() + " : " + it.value }
            } catch (ignored: Exception) {
            }.toString()
        } else {
            "未知错误"
        }
        AlertDialog.Builder(this)
                .setMessage(errorMessage)
                .setPositiveButton("知道了", null)
                .show()
    }

    private val enableLog = false

    private fun appendLog(message: String?) {
        if (enableLog) {
            message?.let {
                logView.text = "$it\n${logView.text}\n"
            }
        }
    }

    private fun Context.toast(text: CharSequence): Toast = Toast.makeText(this, text, Toast.LENGTH_SHORT).apply { show() }

    private inline fun SharedPreferences.edit(
            commit: Boolean = false,
            action: SharedPreferences.Editor.() -> Unit
    ) {
        val editor = edit()
        action(editor)
        if (commit) {
            editor.commit()
        } else {
            editor.apply()
        }
    }
}
