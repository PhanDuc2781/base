package com.example.base_project.select.adapter


import android.content.Context
import com.bumptech.glide.Glide
import com.example.base_project.base.BaseSingleAdapter
import com.example.base_project.base.BaseViewHolder
import com.example.base_project.databinding.ItemVideoBinding
import com.example.base_project.ext.formatFileSize
import com.example.base_project.ext.formatSecondsToTime
import com.example.base_project.select.bean.Video

class SelectVideoAdapter(
    private val context: Context,
    private val onClickItem: (Video) -> Unit = {},
    private val onClickPlay: (Video) -> Unit = {},
) : BaseSingleAdapter<Video, ItemVideoBinding>(ItemVideoBinding::inflate) {
    override fun createViewHolder(binding: ItemVideoBinding): BaseViewHolder<ItemVideoBinding> {
        return BaseViewHolder(binding).apply {
            binding.root.setOnClickListener {
                onClickItem(getItem(adapterPosition))
            }

            binding.icPlay.setOnClickListener {
                val item = getItem(adapterPosition)
                onClickPlay.invoke(item)
            }
        }
    }

    override fun bindingViewHolder(
        holder: BaseViewHolder<ItemVideoBinding>,
        position: Int,
    ) {
        val item = getItem(position)
        holder.binding.apply {
            txtTitle.isSelected = true
            Glide.with(context).load(item.path).into(icThumb)
            txtDuration.text = item.duration.formatSecondsToTime()
            txtSize.text = item.size.formatFileSize()
            txtType.text = item.extension.uppercase()
            txtTitle.text = item.title
        }
    }
}