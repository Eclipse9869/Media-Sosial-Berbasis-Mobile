package com.ubaya.dailymemedigest_kelompok29.Api

import com.ubaya.dailymemedigest_kelompok29.Data.ApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LeaderboardServices {
    @GET("leaderboard.php")
    fun getLeaderboard(): Call<ApiResponse<Any>>
}