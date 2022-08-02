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

    private var mPickFileStatus: FilePicker.PickFileStatuses? = null
    private var mPickMultipleFileStatus: FilePicker.PickFileMultipleStatuses? = null
    private var mImageCaptureStatus: FilePicker.ImageCaptureStatuses? = null
    private var mVideoCaptureStatus: FilePicker.VideoCaptureStatuses? = null

    companion object {
        private const val ARG_KEY_FILE_STATUSES = "ARG_KEY_FileStatues"

        fun newInstance(fileStatuses: FilePicker.FileStatuses): FilePickerFragment {
            val filePickerFragment = FilePickerFragment()
            filePickerFragment.arguments = Bundle().apply {
                putParcelable(ARG_KEY_FILE_STATUSES, fileStatuses)
            }
            return filePickerFragment
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
        arguments?.let { args ->
            when (val fileStatuses =
                args.getParcelable<FilePicker.FileStatuses>(ARG_KEY_FILE_STATUSES)) {
                is FilePicker.PickFileStatuses -> mPickFileStatus = fileStatuses
                is FilePicker.PickFileMultipleStatuses -> mPickMultipleFileStatus = fileStatuses
                is FilePicker.ImageCaptureStatuses -> mImageCaptureStatus = fileStatuses
                is FilePicker.VideoCaptureStatuses -> mVideoCaptureStatus = fileStatuses
            }
        }

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
        } else {
            mPickFileStatus?.onError?.invoke(getString(R.string.message_user_cancelled))
            mPickMultipleFileStatus?.onError?.invoke(getString(R.string.message_user_cancelled))
            mImageCaptureStatus?.onError?.invoke(getString(R.string.message_user_cancelled))
            mVideoCaptureStatus?.onError?.invoke(getString(R.string.message_user_cancelled))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
