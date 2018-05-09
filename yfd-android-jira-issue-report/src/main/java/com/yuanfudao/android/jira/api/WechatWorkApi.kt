package com.yuanfudao.android.jira.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by meng on 2018/5/14.
 */
interface WechatWorkApi {

    companion object {

        fun createService(): WechatWorkApi {
            return RestClient.wechatRetrofit.create(WechatWorkApi::class.java)
        }
    }

    @POST("/weixin/")
    fun sendMessage(@Body wechatMessage: WechatMessage): Call<Any>
}