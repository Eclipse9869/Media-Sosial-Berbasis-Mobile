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
import com.ubaya.dailymemedigest_kelompok29.Adapter.LeaderboardAdapter
import com.ubaya.dailymemedigest_kelompok29.Api.LeaderboardServices
import com.ubaya.dailymemedigest_kelompok29.Api.MemeServices
import com.ubaya.dailymemedigest_kelompok29.Data.ApiResponse
import com.ubaya.dailymemedigest_kelompok29.Data.Leaderboard
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

class LeaderboardFragment : Fragment() {
    private lateinit var mainActivity: MainActivity
    private lateinit var fView: View
    private lateinit var rv: RecyclerView
    private var leaderList = mutableListOf<Leaderboard>()
    private lateinit var adapter: LeaderboardAdapter

    private lateinit var sharePrefHelper: SharePrefHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        fView.findViewById<View>(R.id.fab_tambah).setOnClickListener {
            val inent = Intent(requireContext(), AddMemeActivity::class.java)
            startActivity(inent)
        }
    }

    private fun initRv() {
        adapter = LeaderboardAdapter(requireContext(), leaderList)
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
                val leaderboardServices =
                    RetrofitHelper.client.create(LeaderboardServices::class.java)
                val result: Call<ApiResponse<Any>> =
                    leaderboardServices.getLeaderboard()

                result.enqueue(object : Callback<ApiResponse<Any>> {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<ApiResponse<Any>>,
                        response: Response<ApiResponse<Any>>
                    ) {
                        leaderList.clear()
                        hidePb()
                        if (response.isSuccessful && response.body()?.status == 200) {
                            val data = response.body()?.data as ArrayList<*>
                            data.forEach { it ->
                                val json: MutableMap<String, String> =
                                    it as MutableMap<String, String>
                                val leaderboard = Leaderboard(
                                    json["url_image"].toString(),
                                    json["full_name"].toString(),
                                    json["total_like"]!!.toInt()
                                )
                                leaderList.add(leaderboard)
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