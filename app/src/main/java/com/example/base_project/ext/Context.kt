package com.example.base_project.ext

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.base_project.applicattion.MainApplication
import java.io.File
import java.util.Formatter
import java.util.Locale
import java.util.concurrent.TimeUnit

fun Context.hasPermissions(permissions: Array<String>): Boolean = permissions.all {
    ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

@SuppressLint("DefaultLocale")
fun Int.formatSecondsToTime(): String {
    val hour = TimeUnit.MILLISECONDS.toHours(this.toLong())
    val minute = TimeUnit.MILLISECONDS.toMinutes(this.toLong()) % 60
    val second = TimeUnit.MILLISECONDS.toSeconds(this.toLong()) % 60
    return if (hour > 0) {
        String.format("%02d:%02d:%02d", hour, minute, second)
    } else {
        String.format("%02d:%02d", minute, second)
    }
}

fun Int.durationToSecond(): Int {
    return TimeUnit.MILLISECONDS.toSeconds(this.toLong()).toInt()
}

fun Int.toSeconds(): Int {
    return TimeUnit.MILLISECONDS.toSeconds(this.toLong()).toInt()
}


@SuppressLint("DefaultLocale")
fun Long.formatFileSize(): String {
    return android.text.format.Formatter.formatFileSize(
        MainApplication.instance.applicationContext,
        this
    )
}

fun Context.playVideoPath(videoPath: String) {
    val videoFile = File(videoPath)
    val videoUri: Uri = FileProvider.getUriForFile(
        this,
        "${this.packageName}.fileprovider",
        videoFile
    )

    val intent = Intent(Intent.ACTION_VIEW)
    intent.setDataAndType(videoUri, "video/*")
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    try {
        this.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        val context = null
        Toast.makeText(context, "Không có ứng dụng nào để mở video", Toast.LENGTH_SHORT).show()
    }
}

fun Context.openFolder(path: String) {
    val file = File(path)
    val uri = file.toURI().toString().toUri()

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "*/*")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        this.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(this, "Không tìm thấy app quản lý file", Toast.LENGTH_SHORT).show()
    }
}


fun Double.toOneDecimal(): Double {
    return String.format(Locale.US, "%.1f", this).toDouble()
}



