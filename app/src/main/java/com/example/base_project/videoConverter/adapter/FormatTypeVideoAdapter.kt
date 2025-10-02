package com.example.base_project.videoConverter.adapter

import android.util.Log
import com.example.base_project.R
import com.example.base_project.base.AdapterEquatable
import com.example.base_project.base.BaseSingleAdapter
import com.example.base_project.base.BaseViewHolder
import com.example.base_project.databinding.ItemFormatVideoBinding
import com.example.base_project.select.util.SelectConstants
import com.example.base_project.videoConverter.adapter.FormatTypeVideoAdapter.Companion.FORMAT_SELECTED

class FormatTypeVideoAdapter(
    private val onCLick: (FormatType) -> Unit,
    private val isVideoConverter: Boolean = true,
) :
    BaseSingleAdapter<FormatType, ItemFormatVideoBinding>(
        ItemFormatVideoBinding::inflate
    ) {
    var currentSelected = -1
    var currentFormat: FormatType? = null

    fun checkFormat(type: String) {
        Log.d("checkFormat", "checkFormat: $type")
        currentFormat = FormatType.entries.firstOrNull { it.type == type }

        val listType = if (!isVideoConverter) {
            FormatType.entries.filterNot { it.type == currentFormat?.type }
        } else {
            FormatType.entries
        }

        Log.d("checkFormat", "filtered list: $listType")
        submitList(listType)
    }

    companion object {
        const val FORMAT_SELECTED = "format_selected"
    }

    override fun createViewHolder(binding: ItemFormatVideoBinding): BaseViewHolder<ItemFormatVideoBinding> {
        return BaseViewHolder(binding).apply {
            binding.root.setOnClickListener {
                onCLick(getItem(adapterPosition))
                toggetSelected(adapterPosition)
            }
        }
    }

    override fun bindingViewHolder(
        holder: BaseViewHolder<ItemFormatVideoBinding>,
        position: Int,
    ) {
        val itemPosition = getItem(position)
        holder.binding.apply {
            txtType.text = itemPosition.type.uppercase()
            icRadio.setImageResource(
                if (position == currentSelected) R.drawable.ic_radio_checked
                else R.drawable.ic_radio
            )
            root.setBackgroundResource(if (position == currentSelected) R.drawable.bg_format_selected else R.drawable.bg_format)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int, payloads: List<Any?>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.contains(FORMAT_SELECTED)) {
            (holder as BaseViewHolder<ItemFormatVideoBinding>).binding.apply {
                icRadio.setImageResource(
                    if (position == currentSelected) R.drawable.ic_radio_checked else R.drawable.ic_radio
                )
                root.setBackgroundResource(if (position == currentSelected) R.drawable.bg_format_selected else R.drawable.bg_format)
            }
        }
    }
}

fun FormatTypeVideoAdapter.toggetSelected(position: Int) {
    val previousSelected = currentSelected
    currentSelected = position
    notifyItemChanged(previousSelected, FORMAT_SELECTED)
    notifyItemChanged(currentSelected, FORMAT_SELECTED)
}

enum class FormatType(val type: String) : AdapterEquatable {
    MP3(SelectConstants.EXTENSION_AUDIO_MP3),
    M4A(SelectConstants.EXTENSION_AUDIO_M4A),
    WAV(SelectConstants.EXTENSION_AUDIO_WAV),
    AAC(SelectConstants.EXTENSION_AUDIO_AAC)
}