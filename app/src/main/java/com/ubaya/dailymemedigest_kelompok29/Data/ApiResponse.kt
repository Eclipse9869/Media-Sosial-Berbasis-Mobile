package com.ubaya.dailymemedigest_kelompok29.Data

import com.squareup.moshi.Json

data class ApiResponse<T>(
    val status: Int,
    val message: String,
    val data: T,
)