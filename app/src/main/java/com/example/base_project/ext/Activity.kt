package com.example.base_project.ext

import android.app.Activity
import com.example.base_project.applicattion.MainApplication
import kotlinx.coroutines.suspendCancellableCoroutine
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

data class WindowInsetModel(val top: Int, val bottom: Int)