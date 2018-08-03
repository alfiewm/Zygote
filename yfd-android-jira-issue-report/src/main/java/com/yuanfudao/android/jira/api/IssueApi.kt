package com.yuanfudao.android.jira.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by meng on 2018/5/8.
 */

interface IssueApi {

    companion object {

        fun createService(): IssueApi {
            return RestClient.jiraRetrofit.create(IssueApi::class.java)
        }
    }

    @POST("/rest/api/2/issue")
    fun createIssue(
        @Body issueBody: IssuePostBody
    ): Call<IssueResponse>

    @POST("/rest/api/2/search")
    fun searchIssues(
        @Body searchIssueBody: SearchIssueBody
    ): Call<SearchResponse>

    @POST("/rest/api/2/issue/{issueKey}/transitions")
    fun transitionIssue(
        @Path("issueKey") issueKey: String,
        @Body transitionPostBody: TransitionPostBody
    ): Call<Unit>
}