package com.example.base_project.compress.adapter

import com.example.base_project.base.BaseSingleAdapter
import com.example.base_project.base.BaseViewHolder
import com.example.base_project.compress.SampleRate
import com.example.base_project.databinding.ItemOptionCompressBinding

class SampleRateAdapter(
    private val onItemClicked: (SampleRate) -> Unit,
) : BaseSingleAdapter<SampleRate, ItemOptionCompressBinding>(
    ItemOptionCompressBinding::inflate
) {
    companion object {
        const val SAMPLE_RATE_SELECTED = "SAMPLE_RATE_SELECTED"
    }

    init {
        submitList(SampleRate.entries)
    }

    private var currentSampleRate: SampleRate = SampleRate._8000_HZ
    private var currentSelected = -1

    fun setCurrent(sampleRate: SampleRate) {
        val previousSelected = currentSelected
        currentSelected = currentList.indexOf(sampleRate)
        currentSampleRate = sampleRate
        if (currentSelected != -1) {
            notifyItemChanged(previousSelected, SAMPLE_RATE_SELECTED)
            notifyItemChanged(currentSelected, SAMPLE_RATE_SELECTED)
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
            root.setBackgroundResource(if (item == currentSampleRate) com.example.base_project.R.drawable.bg_format_selected else com.example.base_project.R.drawable.bg_format)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int, payloads: List<Any?>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.contains(SAMPLE_RATE_SELECTED)) {
            (holder as BaseViewHolder<ItemOptionCompressBinding>).binding.apply {
                root.setBackgroundResource(if (getItem(position) == currentSampleRate) com.example.base_project.R.drawable.bg_format_selected else com.example.base_project.R.drawable.bg_format)
            }
        }
    }
}