package com.vijay.filepicker.camera

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.vijay.filepicker.FilePicker
import com.vijay.filepicker.FilePickerFragment
import com.vijay.filepicker.utils.createFile
import com.vijay.filepicker.utils.showToast
import java.io.File
import java.io.IOException


/**
 *  Manage the camera intent for image capture and return captured image
 */
class CaptureImage(private val fragment: Fragment) {

    var photoFile: File? = null

    /**
     * dispatch image capture intent
     */
    fun dispatchCaptureImageIntent(mImageCaptureStatus: FilePicker.ImageCaptureStatuses?) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            fragment.context?.let { ctx ->
                try {
                    // Create the File where the photo should go
                    photoFile = try {
                        ctx.createFile("media/images", "IMG", "jpg")
                    } catch (ex: IOException) {
                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also { file ->
                        val photoURI: Uri = FileProvider.getUriForFile(
                            ctx,
                            "com.vijay.filepicker.provider",
                            file
                        )
                        /**
                         * create file and generate uri with that file and pass it to the intent
                         * so captured image will write into that uri path
                         */
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        fragment.startActivityForResult(
                            takePictureIntent,
                            FilePickerFragment.REQ_CAPTURE_IMAGE
                        )
                    }
                } catch (e: ActivityNotFoundException) {
                    e.localizedMessage?.let { mImageCaptureStatus?.onError?.invoke(it) }
                } catch (ex: Exception) {
                    ex.localizedMessage?.let { mImageCaptureStatus?.onError?.invoke(it) }
                }
            }

        }
    }

    /**
     * pass result file to the function
     */
    fun onActivityResult(mImageCaptureStatus: FilePicker.ImageCaptureStatuses?) {
        photoFile?.let { file ->
            if (file.exists()) {
                mImageCaptureStatus?.onSuccess?.invoke(file)
            } else {
                mImageCaptureStatus?.onError?.invoke("File not exist")
            }
        }
    }
}
