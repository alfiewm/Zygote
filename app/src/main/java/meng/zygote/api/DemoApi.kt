package meng.zygote.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by meng on 2017/12/4.
 */
interface DemoApi {

    @GET("/topjson")
    fun getTop(@Query("after") after: String,
               @Query("limit") limit: String): Call<DemoResponse>
}