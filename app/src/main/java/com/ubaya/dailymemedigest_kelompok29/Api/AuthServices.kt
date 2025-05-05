package com.ubaya.dailymemedigest_kelompok29.Api


import com.ubaya.dailymemedigest_kelompok29.Data.ApiResponse
import com.ubaya.dailymemedigest_kelompok29.Data.AuthBody
import com.ubaya.dailymemedigest_kelompok29.Data.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthServices {

    @POST("loginmeme.php")
    fun login(
        @Body loginBody: AuthBody
    ): Call<ApiResponse<Any>>

    @POST("registrasi.php")
    fun register(
        @Body loginBody: AuthBody
    ): Call<ApiResponse<Any>>

}