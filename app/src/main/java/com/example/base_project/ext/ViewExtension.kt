package com.example.base_project.ext

import android.view.View

fun View.hide() = this.apply {
    visibility = View.INVISIBLE
    isEnabled = false
}

fun View.show() = this.apply {
    visibility = View.VISIBLE
    isEnabled = true
}

fun View.gone() = this.apply {
    visibility = View.GONE
    isEnabled = false
}