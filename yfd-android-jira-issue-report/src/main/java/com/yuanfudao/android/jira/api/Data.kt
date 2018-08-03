package com.yuanfudao.android.jira.api

import com.google.gson.annotations.SerializedName

/**
 * Created by meng on 2018/5/8.
 */

class JIRAObj(
    var name: String = ""
)

class Project(
    var key: String = ""
)

enum class StatusType {
    @SerializedName("UNKNOWN")
    UNKNOWN,
    @SerializedName("TODO")
    TODO,
    @SerializedName("In Progress")
    IN_PROGRESS,
    @SerializedName("Done")
    DONE,
    @SerializedName("Reopen")
    REOPEN,
    @SerializedName("Closed")
    CLOSED
}

class Status(
    var id: Int,
    var name: StatusType = StatusType.TODO
)

class IssueFields(
    var project: Project = Project(""),
    var summary: String = "",
    var issuetype: JIRAObj = JIRAObj(""),
    var fixVersions: List<JIRAObj> = listOf(),
    var reporter: JIRAObj? = null,
    var assignee: JIRAObj? = null,
    var priority: JIRAObj? = null,
    var labels: List<String> = listOf(),
    var environment: String = "",
    var description: String = "",
    var status: Status? = null
)

class Issue(
    var id: String = "",
    var key: String = "",
    var fields: IssueFields? = null
) {
    fun isResolved(): Boolean {
        return fields?.status?.name == StatusType.DONE || fields?.status?.name == StatusType.CLOSED
    }
}

class IssuePostBody(
    var fields: IssueFields? = null
)

data class IssueResponse(
    val id: String = "",
    val key: String = "",
    val self: String = ""
)

class WechatMessage(
    var touser: MutableSet<String> = mutableSetOf(),
    var toparty: MutableSet<String> = mutableSetOf(),
    var content: String = ""
)

class SearchIssueBody(
    var jql: String, // 搜索语句
    var startAt: Int = 0, // 起始cursor
    var maxResults: Int = 50, // 最大返回Issue数量
    var fields: List<String> = listOf() // 返回Issue字段列表，传空默认返回所有字段
)

data class SearchResponse(
    var total: Int,
    var issues: List<Issue> = listOf()
)

const val TRANSITION_TODO = 11
const val TRANSITION_IN_PROGRESS = 21
const val TRANSITION_DONE = 31
const val TRANSITION_CLOSE = 41
const val TRANSITION_REOPEN = 51
const val TRANSITION_LATER = 61

class TransitionFields(
    val id: Int
)

class TransitionPostBody(
    var transition: TransitionFields
)