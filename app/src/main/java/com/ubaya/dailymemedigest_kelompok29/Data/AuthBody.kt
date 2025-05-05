package com.ubaya.dailymemedigest_kelompok29.Data

import com.squareup.moshi.Json

data class AuthBody(
    @Json(name = "username") val username: String,
    @Json(name = "password") val password: String
)
