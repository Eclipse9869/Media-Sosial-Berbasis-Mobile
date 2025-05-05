package com.ubaya.dailymemedigest_kelompok29

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ubaya.dailymemedigest_kelompok29.Api.UserServices
import com.ubaya.dailymemedigest_kelompok29.Data.ApiResponse
import com.ubaya.dailymemedigest_kelompok29.Data.UserBody
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

class SettingFragment : Fragment(), BottomSheetOptionImage.OnResultImage {

    private lateinit var mainActivity: MainActivity
    private lateinit var sharePrefHelper: SharePrefHelper
    private lateinit var fView: View

    private var profileBitmap: Bitmap? = null
    private var profileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fView = inflater.inflate(R.layout.fragment_setting2, container, false)

        sharePrefHelper = SharePrefHelper(mainActivity)
        initComponents()

        return fView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            mainActivity = (activity as MainActivity)
            mainActivity.setOnImgResult(this)
        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    override fun onDetach() {
        super.onDetach()
        try{
            mainActivity = (activity as MainActivity)
            mainActivity.setOnImgResult(null)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun initComponents() {
        try {
            val userData = sharePrefHelper.getString("user")
            val json = Gson().fromJson(userData, JsonObject::class.java)

            fView.findViewById<EditText>(R.id.et_first_name)
                .setText(json.get("first_name").asString)
            fView.findViewById<EditText>(R.id.et_last_name)
                .setText(json.get("last_name").asString)
            fView.findViewById<CheckBox>(R.id.cb_privacy).isChecked =
                json.get("privacy_setting").asString == "1"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        fView.findViewById<View>(R.id.img_profile).setOnClickListener {
            if (mainActivity.isPickImagePermissionGranted(mainActivity)) {
                mainActivity.showBottomSheetOptionImage()
            } else {
                mainActivity.requestPickImagePermissions(mainActivity)
            }
        }
        fView.findViewById<View>(R.id.btn_submit).setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                showPb()
            }

            try {
                val isPrivacy: Int = if (fView.findViewById<CheckBox>(R.id.cb_privacy).isChecked) {
                    1
                } else {
                    0
                }

                val userData = sharePrefHelper.getString("user")
                val json = Gson().fromJson(userData, JsonObject::class.java)
                val body = UserBody(
                    json.get("user_id").asInt,
                    fView.findViewById<EditText>(R.id.et_first_name).text.toString(),
                    fView.findViewById<EditText>(R.id.et_last_name).text.toString(),
                    isPrivacy,
                )

                val memesServices = RetrofitHelper.client.create(UserServices::class.java)
                val result: Call<ApiResponse<Any>> = memesServices.updateUser(body)

                result.enqueue(object : Callback<ApiResponse<Any>> {
                    override fun onResponse(
                        call: Call<ApiResponse<Any>>,
                        response: Response<ApiResponse<Any>>
                    ) {
                        if (response.isSuccessful && response.body()?.status == 200) {
                            val data = response.body()?.data
                            if (data == null) {
                                mainActivity.showSnackbar(
                                    R.id.constraint_regist,
                                    "Register Fail, User Not Inserted"
                                )
                                return
                            }

                            val toJson = Gson().toJson(data)
                            sharePrefHelper.putString("user", toJson)

                            mainActivity.refreshName()
                            mainActivity.showSnackbar(
                                R.id.constraint_setting,
                                "Data Has Been Updated"
                            )
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                mainActivity.showSnackbar(
                                    R.id.constraint_setting,
                                    jObjError.getString("message")
                                )
                            } catch (e: java.lang.Exception) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                        hidePb()
                    }

                    override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                        mainActivity.runOnUiThread {
                            mainActivity.showSnackbar(R.id.constraint_setting, t.message)
                        }
                        hidePb()
                    }
                })
            } catch (e: HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    mainActivity.showSnackbar(R.id.constraint_setting, e.message)
                    hidePb()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    mainActivity.showSnackbar(R.id.constraint_setting, e.message)
                    hidePb()
                }
            }
        }
    }

    private fun showPb() {
        fView.findViewById<View>(R.id.pb_submit).visibility = View.VISIBLE
        fView.findViewById<View>(R.id.btn_submit).visibility = View.GONE
    }

    private fun hidePb() {
        fView.findViewById<View>(R.id.pb_submit).visibility = View.GONE
        fView.findViewById<View>(R.id.btn_submit).visibility = View.VISIBLE
    }


    private fun getBitmapFromUri(uri: Uri?): Bitmap? {
        return try {
            val dummy = ImageView(requireContext())
            dummy.setImageURI(uri)
            val drawable = dummy.drawable as BitmapDrawable
            drawable.bitmap
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun resultImage(uri: Uri?) {
        if (uri != null) {
            profileUri = uri
            profileBitmap = getBitmapFromUri(uri)
            fView.findViewById<ImageView>(R.id.img_profile).setImageBitmap(profileBitmap)
            mainActivity.setProfileDrawer(profileBitmap)
        }
    }

}