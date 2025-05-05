package com.ubaya.dailymemedigest_kelompok29.Data

import com.google.gson.annotations.SerializedName

data class Leaderboard(
    @SerializedName("url_image") val urlImage: String?,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("jumlah_like") val like: Int,
)