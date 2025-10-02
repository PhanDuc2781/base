package com.example.base_project.ui.main.language

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.base_project.R
import com.example.base_project.base.BaseSingleAdapter
import com.example.base_project.base.BaseViewHolder
import com.example.base_project.databinding.ItemLanguageBinding

class LanguageAdapter(
    private val context: Context,
    private val onClick: (LanguageModel) -> Unit
) :
    BaseSingleAdapter<LanguageModel, ItemLanguageBinding>(ItemLanguageBinding::inflate) {
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    fun clearSelection() {
        val previousSelected = selectedPosition
        selectedPosition = RecyclerView.NO_POSITION
        notifyItemChanged(previousSelected)
    }


    override fun bindingViewHolder(holder: BaseViewHolder<ItemLanguageBinding>, position: Int) {
        val itemPosition = getItem(position)
        holder.binding.apply {
            itemPosition?.let {
                icFlag.load(itemPosition.iconFlag)
                icRadio.load(if (selectedPosition == position) R.drawable.ic_radio_checked else R.drawable.ic_unselected_languae)
                txtName.text = context.getString(itemPosition.nameLanguage)
                root.setBackgroundResource(if (selectedPosition == position) R.drawable.bg_choose_lg else R.drawable.bg_language)
            }
        }
    }

    override fun createViewHolder(binding: ItemLanguageBinding): BaseViewHolder<ItemLanguageBinding> {
        return BaseViewHolder(binding).apply {
            binding.root.rootView.setOnClickListener {
                val previousSelectedPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousSelectedPosition)
                notifyItemChanged(selectedPosition)
                getItem(selectedPosition)?.let {
                    onClick.invoke(it)
                }
            }
        }
    }

    init {
        submitList(LanguageModel.entries.filter { it.isShow })
    }
}