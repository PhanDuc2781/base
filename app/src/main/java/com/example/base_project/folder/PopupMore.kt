package com.example.base_project.folder

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.graphics.drawable.toDrawable
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.MorePopupBinding
import com.example.base_project.ext.dpToPx

class PopupMore(
    private val context: Context,
    private val onShare: () -> Unit = {},
    private val onDetails: () -> Unit = {},
    private val onDelete: () -> Unit = {},
) {
    private var popupWindow: PopupWindow? = null
    private lateinit var binding: MorePopupBinding

    fun show(anchorView: View) {
        binding = MorePopupBinding.inflate(LayoutInflater.from(context))

        binding.layoutDelete.setOnSingleClickListener {
            onDelete()
            dismiss()
        }

        binding.layoutDetail.setOnSingleClickListener {
            onDetails()
            dismiss()
        }

        binding.layoutShare.setOnSingleClickListener {
            onShare()
            dismiss()
        }

        val screenWidth = context.resources.displayMetrics.widthPixels
        val popupWidth = (screenWidth * 0.5).toInt()
        val marginRightPx = context.dpToPx(20)

        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        val anchorX = location[0]

        val offsetX = screenWidth - popupWidth - anchorX - marginRightPx
        val offsetY = context.dpToPx(10)

        val height = ViewGroup.LayoutParams.WRAP_CONTENT

        popupWindow = PopupWindow(binding.root, popupWidth, height, true).apply {
            elevation = 8f
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            showAsDropDown(anchorView, offsetX, offsetY)
        }
    }

    fun dismiss() {
        popupWindow?.dismiss()
    }
}