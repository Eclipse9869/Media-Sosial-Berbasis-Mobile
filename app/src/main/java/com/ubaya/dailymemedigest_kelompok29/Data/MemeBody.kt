package com.ubaya.dailymemedigest_kelompok29.Data

import com.google.gson.annotations.SerializedName

data class MemeBody(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("url_meme") val urlMeme: String,
    @SerializedName("teks_atas") val textAtas: String,
    @SerializedName("teks_bawah") val textBawah: String,
)