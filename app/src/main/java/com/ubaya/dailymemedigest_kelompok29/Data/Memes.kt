package com.ubaya.dailymemedigest_kelompok29.Data

import com.google.gson.annotations.SerializedName

data class Memes(
    @SerializedName("id_meme") val idMeme: Int,
    @SerializedName("url_meme") val urlMeme: String?,
    @SerializedName("teks_atas") val teksAtas: String?,
    @SerializedName("teks_bawah") val teksBawah: String?,
    @SerializedName("jumlah_like") val jumlahLike: Int?,
    @SerializedName("user_id") val userId: Int,
    val isLike: Boolean?,
    @SerializedName("total_comment") val totalComment: Int,
    @SerializedName("created_date") val createdDate: String?,
)