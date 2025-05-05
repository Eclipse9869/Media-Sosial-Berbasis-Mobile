package com.ubaya.dailymemedigest_kelompok29.Data

import com.google.gson.annotations.SerializedName

data class MemeLikeBody(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("id_meme") val memeId: Int,
)