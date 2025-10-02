package com.example.base_project.ext

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.Settings
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.base_project.BuildConfig
import com.example.base_project.applicattion.MainApplication
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume

suspend fun Activity.getInset(): WindowInsetModel = suspendCancellableCoroutine { conti ->

    fun returnData() {
        conti.resume(MainApplication.windowInset)
        conti.cancel()
    }
    if (MainApplication.windowInset.top > 0) {
        returnData()
    } else {
        window.decorView.setOnApplyWindowInsetsListener { v, insets ->
            if (insets.systemWindowInsetTop > 0 && conti.isActive) {
                MainApplication.windowInset =
                    WindowInsetModel(insets.systemWindowInsetTop, insets.systemWindowInsetBottom)
                returnData()
            }
            insets
        }
    }
}

fun AppCompatActivity.getScreenWidth(): Int {
    val point = Point()
    (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getSize(point)
    return point.x
}

data class WindowInsetModel(val top: Int, val bottom: Int)

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

inline fun <reified P : Parcelable> Intent.getParcelableArrayList(key: String): ArrayList<P>? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableArrayListExtra(key, P::class.java)
    } else {
        getParcelableArrayListExtra(key)
    }
}

fun AppCompatActivity.requestQueryAllFiles() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (!Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.setData(("package:" + applicationContext.packageName).toUri())
            startActivity(intent)
        }
    }
}

fun AppCompatActivity.shareFilePath(filePath: String) {
    val shareIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, filePath.toUri())
        type = "*/*"
    }
    startActivity(Intent.createChooser(shareIntent, null))
}