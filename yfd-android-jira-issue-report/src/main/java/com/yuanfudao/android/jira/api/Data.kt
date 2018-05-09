package com.yuanfudao.android.jira.api

/**
 * Created by meng on 2018/5/8.
 */

class JIRAObj(
        var name: String = ""
)

class Project(
        var key: String = ""
)

class Issue(
        var project: Project = Project(""),
        var summary: String = "",
        var issuetype: JIRAObj = JIRAObj(""),
        var fixVersions: List<JIRAObj> = listOf(),
        var reporter: JIRAObj? = null,
        var assignee: JIRAObj? = null,
        var priority: JIRAObj? = null,
        var labels: List<String> = listOf(),
        var environment: String = "",
        var description: String = ""
)

class IssuePostBody(
        var fields: Issue? = null
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