package com.ubaya.dailymemedigest_kelompok29

import android.net.Uri
import android.os.Environment
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File

class BottomSheetOptionImage
constructor(
    private var context: BaseActivity,
) {

    private lateinit var dialog: BottomSheetDialog
    private lateinit var view: View

    private lateinit var onResultImage: OnResultImage
    private var latestTmpUri: Uri? = null

    private lateinit var takeImageResult: ActivityResultLauncher<Uri?>
    private lateinit var selectImageFromGalleryResult: ActivityResultLauncher<String>

    private var imgFlag: String? = null

    class Builder(private val context: BaseActivity) {

        fun build(): BottomSheetOptionImage {
            return BottomSheetOptionImage(context)
        }
    }

    fun initDialog() {
        dialog = BottomSheetDialog(context)
        view = context.layoutInflater.inflate(R.layout.layout_option_image, null)
        dialog.setContentView(view)

        view.findViewById<View>(R.id.ly_kamera).setOnClickListener {
            takeImage()
        }
        view.findViewById<View>(R.id.ly_gallery).setOnClickListener {
            selectImageFromGallery()
        }
    }

    fun showDialog() {
        dialog.show()
    }

    fun onResultImg(onResultImage: OnResultImage) {
        this.onResultImage = onResultImage
    }

    private fun takeImage() {
        context.lifecycleScope.launchWhenResumed {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    fun initResultTakeImage() {
        takeImageResult =
            context.registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
                dialog.dismiss()
                if (isSuccess) {
                    latestTmpUri?.let { uri ->
                        onResultImage.resultImage(uri)
                    }
                }
            }
    }

    fun initResultPickImageGallery() {
        selectImageFromGalleryResult =
            context.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                dialog.dismiss()
                uri?.let {
                    onResultImage.resultImage(it)
                }
            }
    }

    fun setImgFlag(flag: String) {
        this.imgFlag = flag
    }

    private fun getTmpFileUri(): Uri {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: throw IllegalStateException("Dir not found")
        val tmpFile = File.createTempFile("tmp_image_file", ".jpg", storageDir).apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }

    interface OnResultImage {
        fun resultImage(uri: Uri?)
    }
}