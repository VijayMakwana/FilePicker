package com.vijay.filepicker.file

import android.content.ActivityNotFoundException
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.vijay.filepicker.FilePicker
import com.vijay.filepicker.FilePickerFragment
import com.vijay.filepicker.utils.copyFile
import com.vijay.filepicker.utils.createFile
import com.vijay.filepicker.utils.getFileName
import com.vijay.filepicker.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.InputStream


class PickFile(
    private val fragment: Fragment
) {
    fun dispatchPickFileIntent(mFileStatus: FilePicker.PickFileStatuses?) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            if (mFileStatus?.mimeTypes.isNullOrEmpty().not()) {
                val s: Array<String>? = mFileStatus?.mimeTypes?.toTypedArray()
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, s)
            } else {
                type = mFileStatus?.mimeType
            }
        }
        try {
            fragment.startActivityForResult(intent, FilePickerFragment.REQ_FILE)
        } catch (e: ActivityNotFoundException) {
            e.localizedMessage?.let { mFileStatus?.onError?.invoke(it) }
        } catch (ex: Exception) {
            ex.localizedMessage?.let { mFileStatus?.onError?.invoke(it) }
        }
    }

    fun onActivityResult(data: Intent?, mFileStatus: FilePicker.PickFileStatuses?) {
        data?.data?.let { uri ->
            val context = fragment.context
            var openInputStream: InputStream? = null
            (fragment as FilePickerFragment).launch {

                try {
                    withContext(Dispatchers.IO) {
                        openInputStream = context?.contentResolver?.openInputStream(uri)
                    }
                } catch (e: Exception) {
                    e.localizedMessage?.let { mFileStatus?.onError?.invoke(it) }
                }

                val filePath = "media/files"

                val fileName =
                    context?.getFileName(uri).orEmpty().ifEmpty { "FILE" }

                val destination =
                    context?.createFile(
                        filePath,
                        fileName.substringBeforeLast("."),
                        fileName.substringAfterLast(".")
                    )

                destination?.let { file ->
                    withContext(Dispatchers.IO) {
                        (openInputStream as? FileInputStream)?.copyFile(file)
                    }
                    if (file.exists()) {
                        mFileStatus?.onSuccess?.invoke(destination)
                    } else {
                        mFileStatus?.onError?.invoke("File not exist")
                    }
                }
            }
        }
    }
}
