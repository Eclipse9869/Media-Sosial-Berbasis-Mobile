package com.ubaya.dailymemedigest_kelompok29

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.ubaya.dailymemedigest_kelompok29.Api.AuthServices
import com.ubaya.dailymemedigest_kelompok29.Data.ApiResponse
import com.ubaya.dailymemedigest_kelompok29.Data.AuthBody
import com.ubaya.dailymemedigest_kelompok29.Data.User
import com.ubaya.dailymemedigest_kelompok29.Helper.RetrofitHelper
import com.ubaya.dailymemedigest_kelompok29.Helper.SharePrefHelper
import kotlinx.coroutines.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response


class LoginActivity : BaseActivity() {

    private lateinit var sharePrefHelper: SharePrefHelper
    private lateinit var onResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharePrefHelper = SharePrefHelper(getActivity())

        onResultActivity()

        // direct ot mainactivity when user was login before
        if (sharePrefHelper.getBoolean("isLogin")) {
            val intent = Intent(getActivity(), MainActivity::class.java)
            onResult.launch(intent)
            finish()
            return
        }

        initComponents()
    }

    private fun initComponents() {
        setTextEt(R.id.et_username, "regiii")
        setTextEt(R.id.et_password, "123")

        findButton(R.id.btnCreate).setOnClickListener {
            val intent = Intent(this, RegistrasiActivity::class.java)
            onResult.launch(intent)
        }

        findButton(R.id.btnLogin).setOnClickListener {
            login()
        }
    }

    private fun login() {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                showPb()
            }

            try {
                val loginService = RetrofitHelper.client.create(AuthServices::class.java)
                val result: Call<ApiResponse<Any>> = loginService.login(
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
                        if (response.isSuccessful && response.body()?.status == 200) {
                            val data = response.body()?.data
                            if (data == null) {
                                showSnackbar(
                                    R.id.constraint_login,
                                    "Login Fail, User Not Found"
                                )
                                return
                            }

                            val toJson = Gson().toJson(data)
                            sharePrefHelper.putString("user", toJson)
                            sharePrefHelper.putBoolean("isLogin", true)

                            showSnackbar(R.id.constraint_login, "Login Successfully")
                            val intent = Intent(getActivity(), MainActivity::class.java)
                            onResult.launch(intent)
                            finish()
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                showSnackbar(R.id.constraint_login, jObjError.getString("message"))
                            } catch (e: java.lang.Exception) {
                                Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
                            }
                        }
                        hidePb()
                    }

                    override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                        runOnUiThread {
                            showSnackbar(R.id.constraint_login, t.message)
                        }
                        hidePb()
                    }
                })
            } catch (e: HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showSnackbar(R.id.constraint_login, e.message)
                    hidePb()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showSnackbar(R.id.constraint_login, e.message)
                    hidePb()
                }
            }
        }
    }

    private fun onResultActivity() {
        onResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    sharePrefHelper.putBoolean("isLogin", true)
                    val intent = Intent(this, MainActivity::class.java)
                    onResult.launch(intent)
                }
            }
    }

    private fun showPb() {
        findPb(R.id.progress_circular).visibility = View.VISIBLE
        findButton(R.id.btnCreate).visibility = View.GONE
        findButton(R.id.btnLogin).visibility = View.GONE
    }

    private fun hidePb() {
        findPb(R.id.progress_circular).visibility = View.GONE
        findButton(R.id.btnCreate).visibility = View.VISIBLE
        findButton(R.id.btnLogin).visibility = View.VISIBLE
    }
}