package com.ubaya.dailymemedigest_kelompok29

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import com.ubaya.dailymemedigest_kelompok29.Api.AuthServices
import com.ubaya.dailymemedigest_kelompok29.Api.MemeServices
import com.ubaya.dailymemedigest_kelompok29.Data.ApiResponse
import com.ubaya.dailymemedigest_kelompok29.Data.AuthBody
import com.ubaya.dailymemedigest_kelompok29.Data.MemeBody
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

class AddMemeActivity : BaseActivity() {

    private lateinit var sharePrefHelper: SharePrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_meme)
        sharePrefHelper = SharePrefHelper(getActivity())

        initComponents()
    }

    private fun initComponents() {
        initToolbar()

        findViewById<View>(R.id.ly_bottom_img_memes).visibility = View.GONE

        findButton(R.id.btn_submit).setOnClickListener {
            if (getEditTextValue(R.id.et_img_url).isEmpty()) {
                if (getEditTextValue(R.id.tl_img_url).isEmpty()) {
                    findViewById<TextInputLayout>(R.id.tl_img_url).error =
                        "Url Must Be Filled"
                }
            } else {
                postMemes()
            }
        }

        findEditText(R.id.et_img_url).addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if (text.length >= 10) {
                    findViewById<View>(R.id.progress_circular).visibility = View.VISIBLE
                    Picasso
                        .get()
                        .load(text)
                        .placeholder(R.drawable.placeholder_image)
                        .into(findImageView(R.id.img_memes), object : com.squareup.picasso.Callback{
                            override fun onSuccess() {
                                findViewById<View>(R.id.progress_circular).visibility = View.GONE
                            }

                            override fun onError(e: java.lang.Exception?) {
                                findViewById<View>(R.id.progress_circular).visibility = View.GONE
                            }
                        })
                }
            }
        })
    }

    private fun initToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as? Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Create Your Meme"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setNavigationOnClickListener {
            super.onBackPressed()
        }
    }

    private fun postMemes() {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                showPb()
            }

            try {
                val userData = sharePrefHelper.getString("user")
                val json = Gson().fromJson(userData, JsonObject::class.java)
                val body = MemeBody(
                    json.get("user_id").asInt,
                    getEditTextValue(R.id.et_img_url),
                    getEditTextValue(R.id.et_top_text),
                    getEditTextValue(R.id.et_bottom_text),
                )

                val memesServices = RetrofitHelper.client.create(MemeServices::class.java)
                val result: Call<ApiResponse<Any>> = memesServices.postMemes(body)

                result.enqueue(object : Callback<ApiResponse<Any>> {
                    override fun onResponse(
                        call: Call<ApiResponse<Any>>,
                        response: Response<ApiResponse<Any>>
                    ) {
                        if (response.isSuccessful && response.body()?.status == 200) {
                            showSnackbar(R.id.constraint_add_meme, "Login Successfully")
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                showSnackbar(
                                    R.id.constraint_add_meme,
                                    jObjError.getString("message")
                                )
                            } catch (e: java.lang.Exception) {
                                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        hidePb()
                    }

                    override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                        runOnUiThread {
                            showSnackbar(R.id.constraint_add_meme, t.message)
                        }
                        hidePb()
                    }
                })
            } catch (e: HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showSnackbar(R.id.constraint_add_meme, e.message)
                    hidePb()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showSnackbar(R.id.constraint_add_meme, e.message)
                    hidePb()
                }
            }
        }
    }

    private fun showPb() {
        findPb(R.id.pb_submit).visibility = View.VISIBLE
        findButton(R.id.btn_submit).visibility = View.GONE
    }

    private fun hidePb() {
        findPb(R.id.pb_submit).visibility = View.GONE
        findButton(R.id.btn_submit).visibility = View.VISIBLE
    }
}