package com.example.base_project.compress.adapter

import com.example.base_project.base.BaseSingleAdapter
import com.example.base_project.base.BaseViewHolder
import com.example.base_project.compress.Bitrate
import com.example.base_project.compress.Channel
import com.example.base_project.databinding.ItemOptionCompressBinding

class BitrateAdapter(private val onItemClicked: (Bitrate) -> Unit) :
    BaseSingleAdapter<Bitrate, ItemOptionCompressBinding>(ItemOptionCompressBinding::inflate) {
    companion object {
        const val BITRATE_SELECTED = "BITRATE_SELECTED"
    }

    init {
        submitList(Bitrate.entries)
    }

    private var currentBitrate: Bitrate = Bitrate._32_KBPS
    private var currentSelected = -1

    fun setCurrent(bitrate: Bitrate) {
        val previousSelected = currentSelected
        currentSelected = currentList.indexOf(bitrate)
        currentBitrate = bitrate
        if (currentSelected != -1) {
            notifyItemChanged(previousSelected, BITRATE_SELECTED)
            notifyItemChanged(currentSelected, BITRATE_SELECTED)
        }
    }

    override fun createViewHolder(binding: ItemOptionCompressBinding): BaseViewHolder<ItemOptionCompressBinding> {
        return BaseViewHolder(binding).apply {
            binding.root.setOnClickListener {
                onItemClicked(getItem(adapterPosition))
            }
        }
    }

    override fun bindingViewHolder(
        holder: BaseViewHolder<ItemOptionCompressBinding>,
        position: Int,
    ) {
        val item = getItem(position)
        holder.binding.apply {
            txtType.text = item.typeName
            root.setBackgroundResource(if (item == currentBitrate) com.example.base_project.R.drawable.bg_format_selected else com.example.base_project.R.drawable.bg_format)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int, payloads: List<Any?>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.contains(BITRATE_SELECTED)) {
            (holder as BaseViewHolder<ItemOptionCompressBinding>).binding.apply {
                root.setBackgroundResource(if (getItem(position) == currentBitrate) com.example.base_project.R.drawable.bg_format_selected else com.example.base_project.R.drawable.bg_format)
            }
        }
    }

}