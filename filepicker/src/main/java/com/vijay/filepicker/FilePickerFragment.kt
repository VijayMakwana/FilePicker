package com.vijay.filepicker


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.vijay.filepicker.camera.CaptureImage
import com.vijay.filepicker.camera.CaptureVideo
import com.vijay.filepicker.file.PickFile
import com.vijay.filepicker.file.PickMultipleFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 *
 */
class FilePickerFragment : Fragment(), CoroutineScope {
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    companion object {
        private var mPickFileStatus: FilePicker.PickFileStatuses? = null
        private var mPickMultipleFileStatus: FilePicker.PickFileMultipleStatuses? = null
        private var mImageCaptureStatus: FilePicker.ImageCaptureStatuses? = null
        private var mVideoCaptureStatus: FilePicker.VideoCaptureStatuses? = null

        fun newInstance(fileStatuses: FilePicker.FileStatuses): FilePickerFragment {
            when (fileStatuses) {
                is FilePicker.PickFileStatuses -> mPickFileStatus = fileStatuses
                is FilePicker.PickFileMultipleStatuses -> mPickMultipleFileStatus = fileStatuses
                is FilePicker.ImageCaptureStatuses -> mImageCaptureStatus = fileStatuses
                is FilePicker.VideoCaptureStatuses -> mVideoCaptureStatus = fileStatuses
            }

            return FilePickerFragment()
        }

        const val REQ_FILE = 113
        const val REQ_MULTIPLE_FILE = 114
        const val REQ_CAPTURE_IMAGE = 115
        const val REQ_CAPTURE_VIDEO = 116
    }

    private var pickFileObj: PickFile? = null
    private var pickMultipleFileObj: PickMultipleFile? = null
    private var captureImageObj: CaptureImage? = null
    private var captureVideoObj: CaptureVideo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    fun pickFile() {
        mPickFileStatus?.onLoading?.invoke()
        pickFileObj = PickFile(fragment = this)
        pickFileObj?.dispatchPickFileIntent(mPickFileStatus)
    }

    fun pickMultipleFile() {
        mPickMultipleFileStatus?.onLoading?.invoke()
        pickMultipleFileObj = PickMultipleFile(fragment = this)
        pickMultipleFileObj?.dispatchPickMultipleFileIntent(mPickMultipleFileStatus)
    }

    fun captureImage() {
        mImageCaptureStatus?.onLoading?.invoke()
        captureImageObj = CaptureImage(fragment = this)
        captureImageObj?.dispatchCaptureImageIntent(mImageCaptureStatus)
    }

    fun captureVideo() {
        mVideoCaptureStatus?.onLoading?.invoke()
        captureVideoObj = CaptureVideo(fragment = this)
        captureVideoObj?.dispatchCaptureVideoIntent(mVideoCaptureStatus)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_FILE -> {
                    pickFileObj?.onActivityResult(data, mPickFileStatus)
                }

                REQ_CAPTURE_IMAGE -> {
                    captureImageObj?.onActivityResult(mImageCaptureStatus)
                }

                REQ_CAPTURE_VIDEO -> {
                    captureVideoObj?.onActivityResult(mVideoCaptureStatus)
                }

                REQ_MULTIPLE_FILE -> {
                    pickMultipleFileObj?.onActivityResult(data, mPickMultipleFileStatus)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
