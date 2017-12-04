package meng.zygote.api

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by meng on 2017/12/4.
 */
class RestAPI {
    private val demoApi: DemoApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://www.host.com")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
        demoApi = retrofit.create(DemoApi::class.java)
    }

    fun getDemoResponse(after: String, limit: String): Call<DemoResponse> {
        return demoApi.getTop(after, limit)
    }
}