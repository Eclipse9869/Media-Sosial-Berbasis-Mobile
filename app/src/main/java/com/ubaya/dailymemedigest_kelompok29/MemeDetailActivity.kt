package com.ubaya.dailymemedigest_kelompok29

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import com.ubaya.dailymemedigest_kelompok29.Adapter.CommentsAdapter
import com.ubaya.dailymemedigest_kelompok29.Api.MemeServices
import com.ubaya.dailymemedigest_kelompok29.Data.*
import com.ubaya.dailymemedigest_kelompok29.Helper.RetrofitHelper
import com.ubaya.dailymemedigest_kelompok29.Helper.SharePrefHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.Type


class MemeDetailActivity : BaseActivity() {

    private lateinit var sharePrefHelper: SharePrefHelper
    private lateinit var rv: RecyclerView
    private lateinit var adapter: CommentsAdapter
    private var commentsList = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meme_detail)
        sharePrefHelper = SharePrefHelper(getActivity())
        initComponenets()
    }

    private fun initComponenets() {
        initToolbar()
        initRv()

        findImageView(R.id.img_comment).visibility = View.GONE
        try {
            val type: Type = object : TypeToken<Memes?>() {}.type
            val meme = intent.extras?.getString("meme")
            meme?.let { it ->
                val meme: Memes = Gson().fromJson(it, type)
                findTextView(R.id.tv_text_atas).text = meme.teksAtas
                findTextView(R.id.tv_text_bawah).text = meme.teksBawah
                findTextView(R.id.tv_count_likes).text = meme.jumlahLike.let { jumlah ->
                    if ((jumlah ?: 0) > 0) {
                        jumlah.toString()
                    }

                    "0"
                }

                Picasso
                    .get()
                    .load(meme.urlMeme)
                    .into(findImageView(R.id.img_memes))

                fetchComments(meme.idMeme)
                findImageView(R.id.img_submit).setOnClickListener {
                    postComment(meme.idMeme)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun initToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as? Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Meme Detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            super.onBackPressed()
        }
    }

    private fun initRv() {
        adapter = CommentsAdapter(getActivity(), commentsList)
        rv = findViewById(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(getActivity())
        rv.itemAnimator = DefaultItemAnimator()
        rv.adapter = adapter
    }

    private fun fetchComments(idMeme: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                showPb()
            }

            try {
                val commentServices = RetrofitHelper.client.create(MemeServices::class.java)
                val result: Call<ApiResponse<Any>> = commentServices.getComments(idMeme)

                result.enqueue(object : Callback<ApiResponse<Any>> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<ApiResponse<Any>>,
                        response: Response<ApiResponse<Any>>
                    ) {
                        hidePb()
                        if (response.isSuccessful && response.body()?.status == 200) {
                            commentsList.clear()
                            val data = response.body()?.data as ArrayList<*>
                            data.forEach {
                                val json: MutableMap<String, String> =
                                    it as MutableMap<String, String>
                                val comment = Comment(
                                    json["full_name"].toString(),
                                    json["comment_date"].toString(),
                                    json["comment"].toString()
                                )
                                commentsList.add(comment)
                            }

                            if (commentsList.size == 0) {
                                rv.visibility = View.GONE
                                findTextView(R.id.tv_empty).visibility = View.VISIBLE
                            } else {
                                rv.visibility = View.VISIBLE
                                findTextView(R.id.tv_empty).visibility = View.GONE
                                adapter.notifyDataSetChanged()
                            }
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                showSnackbar(
                                    R.id.coordinator_home,
                                    jObjError.getString("message")
                                )
                            } catch (e: java.lang.Exception) {
                                showSnackbar(R.id.coordinator_home, e.message)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                        runOnUiThread {
                            showSnackbar(R.id.coordinator_home, t.message)
                        }
                        hidePb()
                    }
                })

            } catch (e: HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showSnackbar(R.id.coordinator_home, e.message)
                    hidePb()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showSnackbar(R.id.coordinator_home, e.message)
                    hidePb()
                }
            }
        }
    }

    private fun showPb() {
        findViewById<ProgressBar>(R.id.progress_circular).visibility = View.VISIBLE
    }

    private fun hidePb() {
        findViewById<ProgressBar>(R.id.progress_circular).visibility = View.GONE
    }

    private fun showPbSubmit() {
        findViewById<ProgressBar>(R.id.pb_submit).visibility = View.VISIBLE
        findViewById<View>(R.id.img_submit).visibility = View.GONE
    }

    private fun hidePbSubmit() {
        findViewById<ProgressBar>(R.id.pb_submit).visibility = View.GONE
        findViewById<View>(R.id.img_submit).visibility = View.VISIBLE
    }

    private fun postComment(idMeme: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                showPbSubmit()
            }

            try {
                val userData = sharePrefHelper.getString("user")
                val json = Gson().fromJson(userData, JsonObject::class.java)
                val body = CommentBody(
                    json.get("user_id").asInt,
                    idMeme,
                    getEditTextValue(R.id.et_comment),
                )

                val memesServices = RetrofitHelper.client.create(MemeServices::class.java)
                val result: Call<ApiResponse<Any>> = memesServices.postComment(body)

                result.enqueue(object : Callback<ApiResponse<Any>> {
                    override fun onResponse(
                        call: Call<ApiResponse<Any>>,
                        response: Response<ApiResponse<Any>>
                    ) {
                        if (response.isSuccessful && response.body()?.status == 200) {
                            showSnackbar(
                                R.id.constraint_detail,
                                "Comment Posted!"
                            )
                            fetchComments(idMeme)
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                showSnackbar(
                                    R.id.constraint_detail,
                                    jObjError.getString("message")
                                )
                            } catch (e: java.lang.Exception) {
                                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        hidePbSubmit()
                    }

                    override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                        runOnUiThread {
                            showSnackbar(R.id.constraint_detail, t.message)
                        }
                        hidePbSubmit()
                    }
                })
            } catch (e: HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showSnackbar(R.id.constraint_detail, e.message)
                    hidePbSubmit()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showSnackbar(R.id.constraint_detail, e.message)
                    hidePbSubmit()
                }
            }
        }
    }
}