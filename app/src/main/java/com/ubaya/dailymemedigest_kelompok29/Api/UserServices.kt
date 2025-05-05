package com.ubaya.dailymemedigest_kelompok29.Api

import com.ubaya.dailymemedigest_kelompok29.Data.ApiResponse
import com.ubaya.dailymemedigest_kelompok29.Data.AuthBody
import com.ubaya.dailymemedigest_kelompok29.Data.UserBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserServices {
    @POST("user.php?action=edit")
    fun updateUser(
        @Body userBody: UserBody
    ): Call<ApiResponse<Any>>
}