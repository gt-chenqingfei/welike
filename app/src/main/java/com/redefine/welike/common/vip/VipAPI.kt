package com.redefine.welike.common.vip

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface VipAPI {
    @GET("certify/config/{version}")
    fun vipConfig(@Path("version") version : String): Call<ResponseBody>

    @GET("certify/config/fe/")
    fun vipVerify(): Call<ResponseBody>
}