package com.yuanfudao.android.jira

/**
 * Created by meng on 2018/5/9.
 */
interface JIRAInfoProvider {

    val projectKeys: List<String>
    val appVersion: String
    val reporters: List<String>
    val assignees: List<String>
    val reporterPhoneMap: Map<String, List<String>>
    val loginUserPhone: String

    fun defaultReporter(): String {
        when {
            reporters.isEmpty() -> return ""
            loginUserPhone.isBlank() -> return reporters[0]
            else -> {
                reporterPhoneMap.forEach {
                    if (it.value.contains(loginUserPhone)) {
                        return it.key
                    }
                }
                return reporters[0]
            }
        }
    }
}