package com.vijay.filepicker.file

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.vijay.filepicker.FilePicker
import com.vijay.filepicker.FilePickerFragment
import com.vijay.filepicker.utils.copyFile
import com.vijay.filepicker.utils.createFile
import com.vijay.filepicker.utils.getFileName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.InputStream


class PickMultipleFile(
    private val fragment: Fragment
) {
    fun dispatchPickMultipleFileIntent(mFileStatus: FilePicker.PickFileMultipleStatuses?) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            if (mFileStatus?.mimeTypes.isNullOrEmpty().not()) {
                val s: Array<String>? = mFileStatus?.mimeTypes?.toTypedArray()
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, s)
            } else {
                type = mFileStatus?.mimeType
            }
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }

        try {
            fragment.startActivityForResult(intent, FilePickerFragment.REQ_MULTIPLE_FILE)
        } catch (e: ActivityNotFoundException) {
            e.localizedMessage?.let { mFileStatus?.onError?.invoke(it) }
        } catch (ex: Exception) {
            ex.localizedMessage?.let { mFileStatus?.onError?.invoke(it) }
        }
    }

    fun onActivityResult(data: Intent?, mFileStatus: FilePicker.PickFileMultipleStatuses?) {
        (fragment as FilePickerFragment).launch {
            val fileList = arrayListOf<File>()

            if (data?.clipData == null) {
                data?.data?.let { uri ->
                    processUriToFile(fragment, uri, fileList)
                }
            } else {
                data.clipData?.let { clipData ->
                    fileList.clear()
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        processUriToFile(fragment, uri, fileList)
                    }
                }
            }
            if (fileList.isNotEmpty()) {
                mFileStatus?.onSuccess?.invoke(fileList)
            } else {
                mFileStatus?.onError?.invoke("Error in fetching files, Please try again")
            }
        }
    }

    private suspend fun processUriToFile(
        fragment: FilePickerFragment,
        uri: Uri,
        fileList: ArrayList<File>
    ) {
        val context = fragment.context

        var openInputStream: InputStream? = null

        try {
            withContext(Dispatchers.IO) {
                openInputStream = context?.contentResolver?.openInputStream(uri)
            }
        } catch (e: Exception) {
        }

        val filePath = "media/files"

        val fileName =
            if (context?.getFileName(uri).orEmpty().isEmpty()) "FILE" else context?.getFileName(uri)
                .orEmpty()

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
                fileList.add(file)
            }
        }
    }
}
