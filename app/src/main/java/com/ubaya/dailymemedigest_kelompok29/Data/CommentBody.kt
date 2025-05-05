package com.ubaya.dailymemedigest_kelompok29.Data

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json

data class CommentBody(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("id_meme") val memeId: Int,
    @SerializedName("comment") val comment: String,
)
