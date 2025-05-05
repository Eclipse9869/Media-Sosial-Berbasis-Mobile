package com.ubaya.dailymemedigest_kelompok29.Data

import com.google.gson.annotations.SerializedName

data class UserBody(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("first_name") val first_name: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("privacy_setting") val privacySetting: Int,
)
