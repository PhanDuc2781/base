package com.example.base_project.ext

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlin.math.cos
import kotlin.math.sin

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

fun View.visibleOrGone(visible: Boolean) = this.apply {
    visibility = if (visible) View.VISIBLE else View.GONE
    isEnabled = visible
}

fun View.visibleOrInvisible(visible: Boolean) = this.apply {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
    isEnabled = visible
}

fun Int.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}


fun AppCompatTextView.gradient(startColor: Int, endColor: Int, angle: Float) = this.apply {
    post {
        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()

        if (width == 0f || height == 0f) return@post

        val angleRad = Math.toRadians(angle.toDouble())

        val x0 = width / 2 - cos(angleRad) * width / 2
        val y0 = height / 2 - sin(angleRad) * height / 2
        val x1 = width / 2 + cos(angleRad) * width / 2
        val y1 = height / 2 + sin(angleRad) * height / 2

        val shader = LinearGradient(
            x0.toFloat(), y0.toFloat(),
            x1.toFloat(), y1.toFloat(),
            startColor,
            endColor,
            Shader.TileMode.CLAMP
        )

        paint.shader = shader
        invalidate()
    }
}

fun EditText.textChanges(): Flow<String> {
    return callbackFlow {
        val listener = doOnTextChanged { text, _, _, _ -> trySend(text.toString()) }
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text.toString()) }
}

fun SeekBar.onSeekChange(action: (Int) -> Unit) {
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(
            seekBar: SeekBar?,
            progress: Int,
            fromUser: Boolean,
        ) {
            if (fromUser) {
                action(progress)
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    })
}