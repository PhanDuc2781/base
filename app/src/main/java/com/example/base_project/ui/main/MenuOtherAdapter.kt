package com.example.base_project.ui.main

import com.example.base_project.base.BaseSingleAdapter
import com.example.base_project.base.BaseViewHolder
import com.example.base_project.databinding.ItemOtherBinding

class  MenuOtherAdapter(private val onClickItem: (ItemOtherOption) -> Unit) :
    BaseSingleAdapter<ItemOtherOption, ItemOtherBinding>(ItemOtherBinding::inflate) {
    override fun createViewHolder(binding: ItemOtherBinding): BaseViewHolder<ItemOtherBinding> {
        return BaseViewHolder(binding)
    }

    override fun bindingViewHolder(
        holder: BaseViewHolder<ItemOtherBinding>,
        position: Int,
    ) {
        val item = getItem(position)
        holder.binding.txtTitle.isSelected = true
        holder.binding.icon.setImageResource(item.icon)
        holder.binding.txtTitle.setText(item.title)
        holder.binding.root.setOnClickListener {
            onClickItem.invoke(item)
        }
    }

    init {
        submitList(ItemOtherOption.entries)
    }
}