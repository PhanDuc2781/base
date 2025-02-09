package com.example.base_project.base

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.example.base_project.R

class BaseTextview @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {
    init {
        ResourcesCompat.getFont(
            context,
            if (typeface.isBold) R.font.hiragino_kaku_gothic_pro_w6 else R.font.hiragino_kaku_gothic_pro_w3
        )?.let {
            typeface = it
            includeFontPadding = false
        }
    }
}