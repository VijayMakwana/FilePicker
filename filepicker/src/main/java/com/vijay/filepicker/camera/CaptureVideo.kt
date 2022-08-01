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
import java.io.File
import java.io.IOException


/**
 *  Manage the camera intent for image capture and return captured image
 */
class CaptureVideo(private val fragment: Fragment) {

    var videoFile: File? = null

    /**
     * dispatch video capture intent
     */
    fun dispatchCaptureVideoIntent(mVideoCaptureStatus: FilePicker.VideoCaptureStatuses?) {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            fragment.context?.let { ctx ->
                try {
                    // Create the File where the photo should go
                    videoFile = try {
                        ctx.createFile("media/videos", "VID", "mp4")
                    } catch (ex: IOException) {
                        null
                    }
                    // Continue only if the File was successfully created
                    videoFile?.also { file ->
                        val videoURI: Uri = FileProvider.getUriForFile(
                            ctx,
                            "${fragment.context?.packageName}.provider",
                            file
                        )
                        /**
                         * create file and generate uri with that file and pass it to the intent
                         * so captured image will write into that uri path
                         */
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                        fragment.startActivityForResult(
                            takeVideoIntent,
                            FilePickerFragment.REQ_CAPTURE_VIDEO
                        )
                    }
                } catch (e: ActivityNotFoundException) {
                    e.localizedMessage?.let { mVideoCaptureStatus?.onError?.invoke(it) }
                } catch (ex: Exception) {
                    ex.localizedMessage?.let { mVideoCaptureStatus?.onError?.invoke(it) }
                }
            }
        }
    }

    /**
     * pass result file to the function
     */
    fun onActivityResult(mVideoCaptureStatus: FilePicker.VideoCaptureStatuses?) {
        videoFile?.let { file ->
            if (file.exists()) {
                mVideoCaptureStatus?.onSuccess?.invoke(file)
            } else {
                mVideoCaptureStatus?.onError?.invoke("File not exist")
            }
        }
    }
}
