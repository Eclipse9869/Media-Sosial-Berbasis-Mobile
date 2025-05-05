package com.ubaya.dailymemedigest_kelompok29

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ubaya.dailymemedigest_kelompok29.Adapter.MemesAdapter
import com.ubaya.dailymemedigest_kelompok29.Api.MemeServices
import com.ubaya.dailymemedigest_kelompok29.Data.ApiResponse
import com.ubaya.dailymemedigest_kelompok29.Data.Memes
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

class MyCreationFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fView: View
    private lateinit var rv: RecyclerView
    private var memesList = mutableListOf<Memes>()
    private lateinit var adapter: MemesAdapter

    private lateinit var sharePrefHelper: SharePrefHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fView = inflater.inflate(R.layout.fragment_home2, container, false)

        sharePrefHelper = SharePrefHelper(mainActivity)
        initComponents()

        return fView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = (activity as MainActivity)
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    private fun initComponents() {
        initRv()

        fView.findViewById<View>(R.id.fab_tambah).visibility = View.GONE
    }

    private fun initRv() {
        adapter = MemesAdapter(requireContext(), memesList, sharePrefHelper)
        rv = fView.findViewById(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.itemAnimator = DefaultItemAnimator()
        rv.adapter = adapter
    }

    private fun fetchData() {
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                showPb()
            }

            try {
                val userData = sharePrefHelper.getString("user")
                val json = Gson().fromJson(userData, JsonObject::class.java)

                val memesServices = RetrofitHelper.client.create(MemeServices::class.java)
                val result: Call<ApiResponse<Any>> =
                    memesServices.getMemesMy(json.get("user_id").asInt)

                result.enqueue(object : Callback<ApiResponse<Any>> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<ApiResponse<Any>>,
                        response: Response<ApiResponse<Any>>
                    ) {
                        memesList.clear()
                        hidePb()
                        if (response.isSuccessful && response.body()?.status == 200) {
                            val data = response.body()?.data as ArrayList<*>
                            data.forEach {
                                val json: MutableMap<String, String> =
                                    it as MutableMap<String, String>
                                val meme = Memes(
                                    json["id_meme"].toString().toInt(),
                                    json["url_meme"].toString(),
                                    json["teks_atas"].toString(),
                                    json["teks_bawah"].toString(),
                                    json["jumlah_like"].toString().toInt(),
                                    json["user_id"].toString().toInt(),
                                    json["is_like"].toString() == "1",
                                    json["jumlah_like"].let { like ->
                                        if (like == null || like == "null") {
                                            0
                                        } else {
                                            like.toInt()
                                        }
                                    },
                                    json["created_date"],
                                )
                                memesList.add(meme)
                            }
                            adapter.notifyDataSetChanged()
                        } else {
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                mainActivity.showSnackbar(
                                    R.id.coordinator_home,
                                    jObjError.getString("message")
                                )
                            } catch (e: java.lang.Exception) {
                                mainActivity.showSnackbar(R.id.coordinator_home, e.message)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                        mainActivity.runOnUiThread {
                            mainActivity.showSnackbar(R.id.coordinator_home, t.message)
                        }
                        hidePb()
                    }
                })

            } catch (e: HttpException) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    mainActivity.showSnackbar(R.id.coordinator_home, e.message)
                    hidePb()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    mainActivity.showSnackbar(R.id.coordinator_home, e.message)
                    hidePb()
                }
            }
        }
    }

    private fun showPb() {
        fView.findViewById<ProgressBar>(R.id.progress_circular).visibility = View.VISIBLE
    }

    private fun hidePb() {
        fView.findViewById<ProgressBar>(R.id.progress_circular).visibility = View.GONE
    }
}