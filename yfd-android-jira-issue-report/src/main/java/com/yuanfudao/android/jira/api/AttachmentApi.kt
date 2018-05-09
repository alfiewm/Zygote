package com.yuanfudao.android.jira.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Created by meng on 2018/5/8.
 */
interface AttachmentApi {

    companion object {

        fun createService(): AttachmentApi {
            return RestClient.jiraRetrofit.create(AttachmentApi::class.java)
        }
    }

    @Multipart
    @POST("/rest/api/2/issue/{issueId}/attachments")
    fun addAttachments(
            @Path("issueId") issueId: String,
            @Part file: MultipartBody.Part
    ): Call<Any>
}