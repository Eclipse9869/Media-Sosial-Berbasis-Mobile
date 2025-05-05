package com.ubaya.dailymemedigest_kelompok29.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.ubaya.dailymemedigest_kelompok29.Api.MemeServices
import com.ubaya.dailymemedigest_kelompok29.Data.ApiResponse
import com.ubaya.dailymemedigest_kelompok29.Data.CommentBody
import com.ubaya.dailymemedigest_kelompok29.Data.MemeLikeBody
import com.ubaya.dailymemedigest_kelompok29.Data.Memes
import com.ubaya.dailymemedigest_kelompok29.Helper.RetrofitHelper
import com.ubaya.dailymemedigest_kelompok29.Helper.SharePrefHelper
import com.ubaya.dailymemedigest_kelompok29.MemeDetailActivity
import com.ubaya.dailymemedigest_kelompok29.R
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class MemesAdapter(
    private var context: Context,
    private var items: List<Memes>,
    private var sharePrefHelper: SharePrefHelper
) :
    RecyclerView.Adapter<MemesAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memes = items[position]

        memes.urlMeme?.let {
            Picasso
                .get()
                .load(it)
                .into(holder.imgMemes)

        }

        memes.teksAtas?.let {
            holder.tvTeksAtas.text = it
        }
        memes.teksBawah?.let {
            holder.tvTextBawah.text = it
        }

        if (memes.isLike == true) {
            holder.imgLikes.setImageResource(R.drawable.like_red)
        } else {
            holder.imgLikes.setImageResource(R.drawable.like_black)
        }


        holder.tvCountLikes.text = if(memes.jumlahLike != null) memes.jumlahLike.toString() else "0"
        holder.tvCountComment.text = memes.totalComment.toString()
        holder.tvDate.text = memes.createdDate

        holder.imgLikes.setOnClickListener {
            if(memes.isLike != true){
                likeMeme(memes.idMeme, holder)
            }

        }

        holder.cardView.setOnClickListener {
            val intent = Intent(context, MemeDetailActivity::class.java)
            intent.putExtra("meme", Gson().toJson(memes))
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_memes, parent, false)

        return ViewHolder(itemView)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvCountLikes: TextView
        var imgLikes: ImageView
        var imgComments: ImageView
        var imgMemes: ImageView
        var tvTeksAtas: TextView
        var tvTextBawah: TextView
        var cardView: CardView
        var tvCountComment: TextView
        var tvDate: TextView

        init {
            tvCountLikes = view.findViewById(R.id.tv_count_likes)
            tvTeksAtas = view.findViewById(R.id.tv_text_atas)
            tvTextBawah = view.findViewById(R.id.tv_text_bawah)
            imgLikes = view.findViewById(R.id.img_likes)
            imgComments = view.findViewById(R.id.img_comment)
            imgMemes = view.findViewById(R.id.img_memes)
            cardView = view.findViewById(R.id.cv_meme)
            tvCountComment = view.findViewById(R.id.tv_count_comment)
            tvDate = view.findViewById(R.id.tv_created_date)
        }
    }

    private fun likeMeme(idMeme: Int, holder: ViewHolder) {
        GlobalScope.launch {
            try {
                val userData = sharePrefHelper.getString("user")
                val json = Gson().fromJson(userData, JsonObject::class.java)
                val body = MemeLikeBody(
                    json.get("user_id").asInt,
                    idMeme,
                )

                val memesServices = RetrofitHelper.client.create(MemeServices::class.java)
                val result: Call<ApiResponse<Any>> = memesServices.postLikes(body)

                result.enqueue(object : Callback<ApiResponse<Any>> {
                    override fun onResponse(
                        call: Call<ApiResponse<Any>>,
                        response: Response<ApiResponse<Any>>
                    ) {
                        if (response.isSuccessful && response.body()?.status == 200) {
                            holder.tvCountLikes.text = (holder.tvCountLikes.text.toString().toInt() + 1).toString()
                            holder.imgLikes.setImageResource(R.drawable.like_red)
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                val check = 1
                            } catch (e: java.lang.Exception) {

                            }
                        }

                    }

                    override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {

                    }
                })
            } catch (e: HttpException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}