package com.vijay.filepicker.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


internal const val TAG = "FILEPICKER"

/**
 * Returns false if it is null
 */
internal fun Boolean?.orFalse(): Boolean = this ?: false

/**
 * Returns true if it is null
 * Warning: Please use this with caution. Boolean's default value is false
 */
internal fun Boolean?.orTrue(): Boolean = this ?: true

/**
 * create file and return it
 */
internal fun Context.createFile(filePath: String, fileName: String, extension: String): File? {
    val storageDir =
        File(getExternalFilesDir(null), filePath)

    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }
    val ext = if (extension.contains(".")) extension.replace(".", "") else extension
    return File.createTempFile(
        "$fileName-",
        ".$ext",
        storageDir
    )
}


/**
 * get file name from Uri
 */
internal fun Context.getFileName(uri: Uri): String? {
    val returnCursor = contentResolver.query(uri, null, null, null, null)
    var name: String? = null
    returnCursor?.use { cursor ->
        if (cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            Log.d("TAG", "Display Name: $name")
        }

    }
    return name
}

/**
 * copy file
 */
internal fun FileInputStream.copyFile(destination: File) {
    val inputChannel = channel
    val outChannel = FileOutputStream(destination).channel

    inputChannel?.use {
        it.transferTo(0, it.size(), outChannel)
    }
}

/**
 * create intent for open file and launch app according to file
 */
fun Context.openFile(file: File) {
    val openFile = Intent(Intent.ACTION_VIEW)
    openFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    openFile.data =
        FileProvider.getUriForFile(
            this,
            "com.vijay.filepicker.provider", file
        )
    try {
        startActivity(openFile)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "Cannot find app for open this file.", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Log.e(TAG, e.localizedMessage)
    }
}

fun Context.showToast(message: String) {
    Toast.makeText(
        applicationContext,
        message,
        Toast.LENGTH_SHORT
    ).show()
}
