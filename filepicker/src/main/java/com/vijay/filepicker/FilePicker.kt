package com.vijay.filepicker

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.File

/**
 * This will help to pick file and copy on another location and return that file path
 */

object FilePicker {
    /**
     * TAG's for identify fragment
     */
    const val FILE_PICKER_TAG = "filePicker"
    const val MULTIPLE_FILE_PICKER_TAG = "multipleFilePicker"
    const val IMAGE_CAPTURE_TAG = "imageCapture"
    const val VIDEO_CAPTURE_TAG = "videoCapture"

    /**
     * General class for file statuses functions
     */
    @Parcelize
    open class FileStatuses : Parcelable {
        @IgnoredOnParcel
        internal var onError: ((message: String) -> Unit)? = null

        @IgnoredOnParcel
        internal var onLoading: (() -> Unit)? = null

        //DSL
        fun onError(func: ((message: String) -> Unit)?) {
            this.onError = func
        }

        fun onLoading(func: (() -> Unit)?) {
            this.onLoading = func
        }
    }

    /**
     * manage statues for image capture
     */
    class ImageCaptureStatuses : FileStatuses() {
        internal var onSuccess: ((file: File) -> Unit)? = null

        //DSL
        fun onSuccess(func: ((file: File) -> Unit)?) {
            this.onSuccess = func
        }
    }

    /**
     * manage statues for video capture
     */
    class VideoCaptureStatuses : FileStatuses() {
        internal var onSuccess: ((file: File) -> Unit)? = null

        //DSL
        fun onSuccess(func: ((file: File) -> Unit)?) {
            this.onSuccess = func
        }
    }

    /**
     * manage statues for file picker
     */
    class PickFileStatuses : FileStatuses() {
        var mimeType: String = "*/*"
        var mimeTypes: ArrayList<String> = arrayListOf()

        internal var onSuccess: ((file: File) -> Unit)? = null

        //DSL
        fun onSuccess(func: ((file: File) -> Unit)?) {
            this.onSuccess = func
        }
    }

    /**
     * manage statues for multiple file picker
     */
    class PickFileMultipleStatuses : FileStatuses() {
        var mimeType: String = "*/*"
        var mimeTypes: ArrayList<String> = arrayListOf()

        internal var onSuccess: ((fileList: List<File>) -> Unit)? = null

        //DSL
        fun onSuccess(func: ((fileList: List<File>) -> Unit)?) {
            this.onSuccess = func
        }
    }
}

/**
 * Extension fun for pick any type of file
 * @param fileType pass filetype
 * @return lambda with FileStates obj
 */
fun FragmentActivity.pickFile(
    func: (FilePicker.PickFileStatuses.() -> Unit)
): FilePicker.FileStatuses {
    return initiateFilePicker(func)
}

fun Fragment?.pickFile(
    func: (FilePicker.PickFileStatuses.() -> Unit)
): FilePicker.PickFileStatuses {
    return (this?.activity as FragmentActivity).initiateFilePicker(func)
}

/**
 * initiate the file picking
 */
private fun FragmentActivity.initiateFilePicker(
    func: FilePicker.PickFileStatuses.() -> Unit
): FilePicker.PickFileStatuses {
    val fileStatuses = FilePicker.PickFileStatuses().apply(func)
    var filePickerFragment =
        supportFragmentManager.findFragmentByTag(FilePicker.FILE_PICKER_TAG) as? FilePickerFragment
    if (filePickerFragment != null) supportFragmentManager.fragments.clear()
    filePickerFragment = FilePickerFragment.newInstance(fileStatuses)
    supportFragmentManager.beginTransaction()
        .add(filePickerFragment, FilePicker.FILE_PICKER_TAG).commitNow()
    filePickerFragment.pickFile()

    return fileStatuses
}


/**
 * Extension fun for pick multiple file
 * @param fileType pass filetype
 * @return lambda with FileStates obj
 */
fun FragmentActivity.pickMultipleFile(
    func: (FilePicker.PickFileMultipleStatuses.() -> Unit)
): FilePicker.PickFileMultipleStatuses {
    return initiateMultipleFilePicker(func)
}

fun Fragment?.pickMultipleFile(
    func: (FilePicker.PickFileMultipleStatuses.() -> Unit)
): FilePicker.PickFileMultipleStatuses {
    return (this?.activity as FragmentActivity).initiateMultipleFilePicker(func)
}

/**
 * initiate the file picking
 */
private fun FragmentActivity.initiateMultipleFilePicker(
    func: FilePicker.PickFileMultipleStatuses.() -> Unit
): FilePicker.PickFileMultipleStatuses {
    val fileStatuses = FilePicker.PickFileMultipleStatuses().apply(func)
    var filePickerFragment =
        supportFragmentManager.findFragmentByTag(FilePicker.MULTIPLE_FILE_PICKER_TAG) as? FilePickerFragment
    if (filePickerFragment !is FilePickerFragment) {
        filePickerFragment = FilePickerFragment.newInstance(fileStatuses)
        supportFragmentManager.beginTransaction()
            .add(filePickerFragment, FilePicker.MULTIPLE_FILE_PICKER_TAG).commitNow()
    }

    filePickerFragment.pickMultipleFile()

    return fileStatuses
}


/**
 * Extension fun for capture image
 * @return lambda with FileStates obj
 */
fun FragmentActivity.captureImage(
    func: (FilePicker.ImageCaptureStatuses.() -> Unit)
): FilePicker.ImageCaptureStatuses {
    return initiateImageCapture(func)
}

fun Fragment?.captureImage(
    func: (FilePicker.ImageCaptureStatuses.() -> Unit)
): FilePicker.FileStatuses {
    return (this?.activity as FragmentActivity).initiateImageCapture(func)
}

/**
 * initiate the image capture
 */
private fun FragmentActivity.initiateImageCapture(
    func: FilePicker.ImageCaptureStatuses.() -> Unit
): FilePicker.ImageCaptureStatuses {
    val fileStatuses = FilePicker.ImageCaptureStatuses().apply(func)
    var filePickerFragment =
        supportFragmentManager.findFragmentByTag(FilePicker.IMAGE_CAPTURE_TAG) as? FilePickerFragment
    if (filePickerFragment !is FilePickerFragment) {
        filePickerFragment = FilePickerFragment.newInstance(fileStatuses)
        supportFragmentManager.beginTransaction()
            .add(filePickerFragment, FilePicker.IMAGE_CAPTURE_TAG).commitNow()
    }

    filePickerFragment.captureImage()

    return fileStatuses
}


/**
 * Extension fun for capture video
 * @return lambda with FileStates obj
 */
fun FragmentActivity.captureVideo(
    func: (FilePicker.VideoCaptureStatuses.() -> Unit)
): FilePicker.VideoCaptureStatuses {
    return initiateVideoCapture(func)
}

fun Fragment?.captureVideo(
    func: (FilePicker.VideoCaptureStatuses.() -> Unit)
): FilePicker.VideoCaptureStatuses {
    return (this?.activity as FragmentActivity).initiateVideoCapture(func)
}

/**
 * initiate the video capture
 */
private fun FragmentActivity.initiateVideoCapture(
    func: FilePicker.VideoCaptureStatuses.() -> Unit
): FilePicker.VideoCaptureStatuses {
    val fileStatuses = FilePicker.VideoCaptureStatuses().apply(func)
    var filePickerFragment =
        supportFragmentManager.findFragmentByTag(FilePicker.VIDEO_CAPTURE_TAG) as? FilePickerFragment
    if (filePickerFragment !is FilePickerFragment) {
        filePickerFragment = FilePickerFragment.newInstance(fileStatuses)
        supportFragmentManager.beginTransaction()
            .add(filePickerFragment, FilePicker.VIDEO_CAPTURE_TAG).commitNow()
    }

    filePickerFragment.captureVideo()

    return fileStatuses
}




