package com.ubaya.dailymemedigest_kelompok29

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.ubaya.dailymemedigest_kelompok29.Helper.SharePrefHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home2.*


class MainActivity : BaseActivity(), BottomSheetOptionImage.OnResultImage {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var sharePrefHelper: SharePrefHelper
    private lateinit var viewHeader: View
    private var settingFragment: SettingFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharePrefHelper = SharePrefHelper(getActivity())

        initComponents()

        initBottomSheetOptionImage(this)
    }

    private fun initComponents() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        findViewById<View>(R.id.cv_logout).visibility = View.GONE

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navBottomView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        val navController = findNavController(R.id.nav_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_my_creation, R.id.nav_leaderboard, R.id.nav_settings
            ), drawerLayout
        )

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupWithNavController(navBottomView, navController);

        viewHeader = navView.getHeaderView(0)
        viewHeader.findViewById<View>(R.id.img_logout).setOnClickListener {
            logout()
        }

        findViewById<View>(R.id.cv_logout).setOnClickListener {
            logout()
        }

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_settings -> {
                    findViewById<View>(R.id.cv_logout).visibility = View.VISIBLE
                    NavigationUI.onNavDestinationSelected(it, navController)
                }
                else -> {
                    findViewById<View>(R.id.cv_logout).visibility = View.GONE
                    NavigationUI.onNavDestinationSelected(it, navController)
                }
            }
            true
        }

        navBottomView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_settings -> {
                    findViewById<View>(R.id.cv_logout).visibility = View.VISIBLE
                    NavigationUI.onNavDestinationSelected(it, navController)
                }
                else -> {
                    findViewById<View>(R.id.cv_logout).visibility = View.GONE
                    NavigationUI.onNavDestinationSelected(it, navController)
                }
            }
            true
        }

        refreshName()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun logout() {
        showInfoDialog("Konfirmasi", "Logout App ?",
            { _: DialogInterface?, _: Int ->
                val sharePrefHelper = SharePrefHelper(getActivity())
                sharePrefHelper.clearAll()

                val intent = Intent(getActivity(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finishAffinity()
            }
        ) { dialog, _ -> dialog.dismiss() }
    }

    fun refreshName() {
        try {
            val userData = sharePrefHelper.getString("user")
            val json = Gson().fromJson(userData, JsonObject::class.java)
            viewHeader.findViewById<TextView>(R.id.tv_name).text =
                json.get("first_name").let {
                    if (it == null || it.asString == "null") {
                        "(Name)"
                    } else {
                        it.asString + " " + json.get("last_name").asString

                    }
                }
            viewHeader.findViewById<TextView>(R.id.tv_username).text =
                json.get("username").let {
                    if (it == null || it.asString == "null") {
                        "(Username)"
                    } else {
                        it.asString
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
            viewHeader.findViewById<TextView>(R.id.tv_name).text = "Name"
            viewHeader.findViewById<TextView>(R.id.tv_username).text = "Username"
        }
    }

    fun setProfileDrawer(bitmap: Bitmap?) {
        if (bitmap != null) {
            viewHeader.findViewById<ImageView>(R.id.img_profile).setImageBitmap(bitmap)
        }
    }

    override fun resultImage(uri: Uri?) {
        if (settingFragment != null) {
            settingFragment!!.resultImage(uri)
        }
    }

    fun setOnImgResult(fragment: SettingFragment?) {
        this.settingFragment = fragment
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionCamera =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val WritePermision =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        val readExternalPermission =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        if (permissions.isNotEmpty()) {
            val check = Build.VERSION.SDK_INT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //camera permission is last index
                if (permissions[permissions.size - 1] == Manifest.permission.CAMERA && permissionCamera == PackageManager.PERMISSION_GRANTED) {
                    showBottomSheetOptionImage()
                } else {
                    showSnackbar(
                        R.id.coordinator_home,
                        "Please Granted Permission For Pick Image"
                    )
                }
            } else {
                val isGrantedAll = BooleanArray(permissions.size)
                for (i in permissions.indices) {
                    if (permissions[i] == Manifest.permission.CAMERA && permissionCamera == PackageManager.PERMISSION_GRANTED) {
                        isGrantedAll[i] = true
                    } else if (permissions[i] == Manifest.permission.WRITE_EXTERNAL_STORAGE && WritePermision == PackageManager.PERMISSION_GRANTED) {
                        isGrantedAll[i] = true
                    } else if (permissions[i] == Manifest.permission.READ_EXTERNAL_STORAGE && readExternalPermission == PackageManager.PERMISSION_GRANTED) {
                        isGrantedAll[i] = true
                    }
                }
                var isGranted = true
                for (granted in isGrantedAll) {
                    if (!granted) {
                        isGranted = false
                        break
                    }
                }
                if (!isGranted) {
                    showSnackbar(
                        R.id.constraint_setting,
                        "Please Granted Permission For Pick Image"
                    )
                } else {
                    showBottomSheetOptionImage()
                }
            }
        }
    }
}