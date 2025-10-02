package com.example.base_project.wave_form

import android.content.Context
import android.graphics.Point
import android.util.TypedValue
import android.view.WindowManager

object Pixels {
    private var screenWidth:Int = 0

    fun pxtodp(context: Context, value: Int): Int {
        val r = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            r.displayMetrics
        ).toInt()
    }
}