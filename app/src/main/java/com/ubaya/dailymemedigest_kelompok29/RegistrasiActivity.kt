package com.ubaya.dailymemedigest_kelompok29

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.ubaya.dailymemedigest_kelompok29.Api.AuthServices
import com.ubaya.dailymemedigest_kelompok29.Data.ApiResponse
import com.ubaya.dailymemedigest_kelompok29.Data.User

import com.ubaya.dailymemedigest_kelompok29.Data.AuthBody
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

class RegistrasiActivity : BaseActivity() {

    private lateinit var sharePrefHelper: SharePrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrasi)
        sharePrefHelper = SharePrefHelper(getActivity())

        initComponents()
    }

    private fun initComponents() {
        findButton(R.id.btnRegisCreate).setOnClickListener {
            if (getEditTextValue(R.id.et_username).isEmpty()
                || getEditTextValue(R.id.et_password).isEmpty()
                || getEditTextValue(R.id.et_confirm_password).isEmpty()
            ) {
                if (getEditTextValue(R.id.et_username).isEmpty()) {
                    findViewById<TextInputLayout>(R.id.txtRegisUsername).error =
                        "Username must be filled"
                }
                if (getEditTextValue(R.id.et_password).isEmpty()) {
                    findViewById<TextInputLayout>(R.id.txtRegisPassword).error =
                        "Password must be filled"
                }
                if (getEditTextValue(R.id.et_confirm_password).isEmpty()) {
                    findViewById<TextInputLayout>(R.id.txtRegisRepeat).error =
                        "Confirm Password must be filled"
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    findViewById<TextInputLayout>(R.id.txtRegisRepeat).error = null
                    findViewById<TextInputLayout>(R.id.txtRegisUsername).error = null
                    findViewById<TextInputLayout>(R.id.txtRegisRepeat).error = null
                }, 2000)

            } else if (getEditTextValue(R.id.et_password) != getEditTextValue(R.id.et_confirm_password)) {
                findViewById<TextInputLayout>(R.id.txtRegisRepeat).error =
                    "Confirm Password not match"
                Handler(Looper.getMainLooper()).postDelayed({
                    findViewById<TextInputLayout>(R.id.txtRegisRepeat).error = null
                }, 2000)
            } else {
                regist()
            }
        }

        findButton(R.id.btnRegisBack).setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun regist() {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                showPb()
            }

            try {
                val registerServices = RetrofitHelper.client.create(AuthServices::class.java)
                val result: Call<ApiResponse<Any>> = registerServices.register(
                    AuthBody(
                        getEditTextValue(R.id.et_username),
                        getEditTextValue(R.id.et_password)
                    )
                )
                result.enqueue(object : Callback<ApiResponse<Any>> {
                    override fun onResponse(
                        call: Call<ApiResponse<Any>>,
                        response: Response<ApiResponse<Any>>
                    ) {
                        hidePb()
                        if (response.isSuccessful && response.body()?.status == 200) {
                            val data = response.body()?.data
                            if (data == null) {
                                showSnackbar(
                                    R.id.constraint_regist,
                                    "Register Fail, User Not Inserted"
                                )
                                return
                            }

                            val toJson = Gson().toJson(data)
                            sharePrefHelper.putString("user", toJson)

                            showSnackbar(R.id.constraint_regist, "Register Successfully")
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                showSnackbar(R.id.constraint_regist, jObjError.getString("message"))
                            } catch (e: java.lang.Exception) {
                                showSnackbar(R.id.constraint_regist, e.message)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                        runOnUiThread {
                            showSnackbar(R.id.constraint_regist, t.message)
                        }
                        hidePb()
                    }
                })

            } catch (e: HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showSnackbar(R.id.constraint_regist, e.message)
                    hidePb()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showSnackbar(R.id.constraint_regist, e.message)
                    hidePb()
                }
            }
        }
    }

    private fun showPb() {
        findPb(R.id.progress_circular).visibility = View.VISIBLE
        findButton(R.id.btnRegisCreate).visibility = View.GONE
    }

    private fun hidePb() {
        findPb(R.id.progress_circular).visibility = View.GONE
        findButton(R.id.btnRegisCreate).visibility = View.VISIBLE
    }
}