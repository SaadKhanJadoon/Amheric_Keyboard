package com.amherickeyboard.voicetyping.translator

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

fun Activity.copyUriToExternalFilesDir(uri: Uri, fileName: String) {
    try {
        val inputStream = contentResolver.openInputStream(uri)
        val tempDir = externalCacheDir.toString()
        if (inputStream != null) {
            val file = File("$tempDir/$fileName")
            val fos = FileOutputStream(file)
            val bis = BufferedInputStream(inputStream)
            val bos = BufferedOutputStream(fos)
            val byteArray = ByteArray(1024)
            var bytes = bis.read(byteArray)
            while (bytes > 0) {
                bos.write(byteArray, 0, bytes)
                bos.flush()
                bytes = bis.read(byteArray)
            }
            bos.close()
            fos.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Activity.getFileNameByUri(uri: Uri): String {
    var fileName = System.currentTimeMillis().toString()
    val cursor = contentResolver.query(uri, null, null, null, null)
    if (cursor != null && cursor.count > 0) {
        cursor.moveToFirst()
        fileName =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
        cursor.close()
    }
    return fileName
}

fun Activity.isNetworkConnected(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return cm.activeNetworkInfo != null
}

fun Activity.showShortToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}