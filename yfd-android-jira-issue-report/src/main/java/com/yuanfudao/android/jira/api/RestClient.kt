package com.yuanfudao.android.jira.api

import android.util.Base64
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by meng on 2018/5/8.
 */
object RestClient {

    private const val jiraBaseUrl = "xxxx"
    private const val wechatWorkBaseUrl = "xxxxx"
    private const val robotName = "xxxxx"
    private const val robotPwd = "xxxxx"

    private val authString: String by lazy {
        Base64.encodeToString("$robotName:$robotPwd".toByteArray(), Base64.NO_WRAP)
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder().addInterceptor { chain ->
            val request = chain.request()
            val builder = request.newBuilder()
            if (request.url().toString().contains("xxxxxx")) {
                builder.addHeader("Authorization", "Basic $authString")
            }
            if (request.url().toString().contains("attachments")) {
                builder.addHeader("X-Atlassian-Token", "nocheck")
            }
            chain.proceed(builder.build())
        }.build()
    }

    private val gsonConverterFactory by lazy {
        GsonConverterFactory.create()
    }

    val jiraRetrofit: Retrofit by lazy {
        Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(gsonConverterFactory)
                .baseUrl(jiraBaseUrl)
                .build()
    }

    val wechatRetrofit: Retrofit by lazy {
        Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(gsonConverterFactory)
                .baseUrl(wechatWorkBaseUrl)
                .build()
    }
}