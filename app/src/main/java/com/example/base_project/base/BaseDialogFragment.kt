package com.example.base_project.base

import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.example.base_project.R

typealias Inflate4<B> = (LayoutInflater, ViewGroup?, Boolean) -> B
@Suppress("DEPRECATION")
abstract class BaseDialogFragment<B : ViewBinding>(private val inflate: Inflate4<B>) : DialogFragment() {
    lateinit var binding: B

    companion object {
        const val MAX_SDK = 34
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.decorView?.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate.invoke(inflater, container, false)
        setupView()

        if (Build.VERSION.SDK_INT > MAX_SDK) {
            val parent = FrameLayout(inflater.context)
            val percent = 90.toFloat() / 100
            val dm = Resources.getSystem().displayMetrics
            val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
            val percentWidth = rect.width() * percent
            binding.root.layoutParams = FrameLayout.LayoutParams(
                percentWidth.toInt(),
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
            parent.addView(binding.root)
            return parent
        } else {
            return binding.root
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            if (Build.VERSION.SDK_INT > MAX_SDK) R.style.DialogTheme_transparent_34 else R.style.DialogTheme_transparent
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        if (Build.VERSION.SDK_INT > MAX_SDK) {
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        } else {
            setWidthPercent(90)
        }
    }

    open fun setupView() {}
}

fun DialogFragment.setWidthPercent(percentage: Int) {
    val percent = percentage.toFloat() / 100
    val dm = Resources.getSystem().displayMetrics
    val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
    val percentWidth = rect.width() * percent
    dialog?.window?.setLayout(percentWidth.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
}