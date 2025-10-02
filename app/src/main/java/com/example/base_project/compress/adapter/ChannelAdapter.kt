package com.example.base_project.compress.adapter

import com.example.base_project.base.BaseSingleAdapter
import com.example.base_project.base.BaseViewHolder
import com.example.base_project.compress.Channel
import com.example.base_project.databinding.ItemOptionCompressBinding

class ChannelAdapter(private val onItemClicked: (Channel) -> Unit) :
    BaseSingleAdapter<Channel, ItemOptionCompressBinding>(
        ItemOptionCompressBinding::inflate
    ) {
    companion object {
        const val CHANEL_SELECTED = "CHANEL_SELECTED"
    }

    init {
        submitList(Channel.entries)
    }

    private var currentChannel: Channel = Channel.MONO
    private var currentSelected = -1

    fun setCurrent(channel: Channel) {
        val previousSelected = currentSelected
        currentSelected = currentList.indexOf(channel)
        currentChannel = channel
        if (currentSelected != -1) {
            notifyItemChanged(previousSelected, CHANEL_SELECTED)
            notifyItemChanged(currentSelected, CHANEL_SELECTED)
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
            root.setBackgroundResource(if (item == currentChannel) com.example.base_project.R.drawable.bg_format_selected else com.example.base_project.R.drawable.bg_format)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int, payloads: List<Any?>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.contains(CHANEL_SELECTED)) {
            (holder as BaseViewHolder<ItemOptionCompressBinding>).binding.apply {
                root.setBackgroundResource(if (getItem(position) == currentChannel) com.example.base_project.R.drawable.bg_format_selected else com.example.base_project.R.drawable.bg_format)
            }
        }
    }
}