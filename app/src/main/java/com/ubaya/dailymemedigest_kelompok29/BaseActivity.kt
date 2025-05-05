package com.ubaya.dailymemedigest_kelompok29

import android.Manifest.permission
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

open class BaseActivity : AppCompatActivity() {

    protected fun findButton(id: Int): Button {
        return findViewById<View>(id) as Button
    }

    protected fun findCheckbox(id: Int): CheckBox {
        return findViewById<View>(id) as CheckBox
    }

    protected fun findImageView(id: Int): ImageView {
        return findViewById<View>(id) as ImageView
    }

    protected fun findTextView(id: Int): TextView {
        return findViewById<View>(id) as TextView
    }

    protected fun findEditText(id: Int): EditText {
        return findViewById<View>(
            id
        ) as EditText
    }

    protected fun findPb(id: Int): ProgressBar {
        return findViewById<View>(
            id
        ) as ProgressBar
    }

    protected fun getTextViewVal(id: Int): String? {
        return findTextView(id).text.toString()
    }

    protected fun getTextViewVal(id: TextView): String {
        return id.text.toString()
    }

    protected fun getEditTextValue(id: Int): String {
        return findEditText(id).text.toString()
    }

    protected fun getEditTextValue(editText: EditText): String? {
        return editText.text.toString()
    }


    protected fun setTextTv(id: Int, obj: Any) {
        findTextView(id).text = obj.toString()
    }

    protected fun setTextEt(id: Int, obj: Any) {
        findEditText(id).setText(obj.toString())
    }

    protected fun getActivity(): Activity {
        return this
    }

    public fun showSnackbar(layoutId: Int, message: String?) {
        if (!message.isNullOrEmpty()) {
            val snackBar = Snackbar.make(
                findViewById(layoutId), message,
                Snackbar.LENGTH_LONG
            ).setAction("Action", null)
            snackBar.show()
        }

    }

    public fun showInfoDialog(
        tittle: String?,
        message: String?,
        onClickListenerOK: DialogInterface.OnClickListener?,
        onClickListenerNO: DialogInterface.OnClickListener?
    ) {
        var onClickListenerOK = onClickListenerOK
        if (onClickListenerOK == null) {
            onClickListenerOK = onClickListenerDismiss
        }
        showDialog(
            getActivity(),
            tittle,
            message,
            "Ya",
            "Tidak",
            onClickListenerOK,
            onClickListenerNO
        )
    }

    public fun showInfoDialog(
        tittle: String?,
        message: String?,
        onClickListenerOK: DialogInterface.OnClickListener?
    ) {
        var onClickListenerOK = onClickListenerOK
        if (onClickListenerOK == null) {
            onClickListenerOK = onClickListenerDismiss
        }
        showDialog(getActivity(), tittle, message, "Ok", "", onClickListenerOK, null)
    }

    private val onClickListenerDismiss =
        DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.dismiss() }

    private fun showDialog(
        context: Context?,
        title: String?,
        message: String?,
        button1: String?,
        button2: String?,
        listener1: DialogInterface.OnClickListener?,
        listener2: DialogInterface.OnClickListener?
    ) {
        val dlg = AlertDialog.Builder(context)
        if (title != null && title != "") {
            dlg.setTitle(title)
        }
        if (message != null && message != "") {
            dlg.setMessage(message)
        }
        if (button1 != null && button1 != "" && button1 != null && button1 != "") {
            dlg.setPositiveButton(button1, listener1)
            dlg.setNegativeButton(button2, listener2)
        } else if (button1 != null && button1 != "") {
            dlg.setNeutralButton(button1, listener1)
        }
        //dlg.setCancelable(false);
        dlg.create().show()
    }

    public fun isPickImagePermissionGranted(context: AppCompatActivity): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context, permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            context, permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    val PERMISSION_REQUEST_CODE = 38

    public fun requestPickImagePermissions(activity: AppCompatActivity) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                permission.WRITE_EXTERNAL_STORAGE,
                permission.READ_EXTERNAL_STORAGE,
                permission.CAMERA
            ), PERMISSION_REQUEST_CODE
        )
    }

    private var bottomSheetOptionImage: BottomSheetOptionImage? = null

    fun initBottomSheetOptionImage(resultImage: BottomSheetOptionImage.OnResultImage) {
        bottomSheetOptionImage = BottomSheetOptionImage(this)
        bottomSheetOptionImage?.initDialog()
        bottomSheetOptionImage?.onResultImg(resultImage)
        bottomSheetOptionImage?.initResultPickImageGallery()
        bottomSheetOptionImage?.initResultTakeImage()
    }

    open fun showBottomSheetOptionImage() {
        bottomSheetOptionImage?.showDialog()
    }
}