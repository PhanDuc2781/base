package com.example.base_project.ui.setting

import androidx.recyclerview.widget.RecyclerView
import com.example.base_project.BuildConfig
import com.example.base_project.applicattion.storage
import com.example.base_project.base.BaseSingleAdapter
import com.example.base_project.base.BaseViewHolder
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.ItemSettingBinding
import com.example.base_project.ext.visibleOrGone
import com.example.base_project.ui.main.language.LanguageModel

class SettingAdapter(private val onClickListener: (SettingItem) -> Unit) :
    BaseSingleAdapter<SettingItem, ItemSettingBinding>(ItemSettingBinding::inflate) {
    override fun createViewHolder(binding: ItemSettingBinding): BaseViewHolder<ItemSettingBinding> {
        return BaseViewHolder(binding).apply {
            binding.root.setOnSingleClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClickListener.invoke(SettingItem.getSettingItem(adapterPosition))
                }
            }
        }
    }

    override fun bindingViewHolder(
        holder: BaseViewHolder<ItemSettingBinding>,
        position: Int,
    ) {
        val item = getItem(position)
        with(holder.binding) {
            icIcon.setImageResource(item.icon)
            txtName.setText(item.title)
            val currentLanguage = LanguageModel.get(storage.appLanguage)
            txtLanguage.visibleOrGone(item == SettingItem.LANGUAGE || item == SettingItem.VERSION)
            currentLanguage?.let {
                if (item == SettingItem.LANGUAGE) {
                    txtLanguage.text = holder.binding.root.context.getString(it.nameLanguage)
                } else {
                    txtLanguage.text = BuildConfig.VERSION_NAME
                }
            }
        }
    }

    init {
        submitList(SettingItem.entries)
    }

}