package com.ubaya.dailymemedigest_kelompok29.Data

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("first_name") val firstName: String?,
    @SerializedName("last_name") val lastName: String?,
    @SerializedName("url_image") val urlImage: String?,
    @SerializedName("privacy_setting") val privacySetting: String?,
)
