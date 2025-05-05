package com.ubaya.dailymemedigest_kelompok29.Api

import com.ubaya.dailymemedigest_kelompok29.Data.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MemeServices {

    @GET("memes.php?action=memes")
    fun getMemes(@Query("user_id") userId: Int): Call<ApiResponse<Any>>

    @GET("memes.php?action=my")
    fun getMemesMy(@Query("user_id") userId: Int): Call<ApiResponse<Any>>

    @POST("memes.php?action=like")
    fun postLikes(@Body memeLikeBody: MemeLikeBody): Call<ApiResponse<Any>>

    @GET("memes.php?action=comments")
    fun getComments(@Query("id_meme") idMeme: Int): Call<ApiResponse<Any>>

    @POST("memes.php?action=postComments")
    fun postComment(
        @Body commentBody: CommentBody
    ): Call<ApiResponse<Any>>

    @POST("memes.php?action=postMemes")
    fun postMemes(
        @Body memesBody: MemeBody
    ): Call<ApiResponse<Any>>

}