package com.example.base_project.select.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.base_project.R
import com.example.base_project.base.BaseSingleAdapter
import com.example.base_project.base.BaseViewHolder
import com.example.base_project.base.MyActivity
import com.example.base_project.databinding.ItemSelectAudioBinding
import com.example.base_project.enum.Type
import com.example.base_project.ext.formatFileSize
import com.example.base_project.ext.formatSecondsToTime
import com.example.base_project.ext.visibleOrGone
import com.example.base_project.ext.visibleOrInvisible
import com.example.base_project.select.bean.Audio

class SelectAudioAdapter(
    private val context: MyActivity,
    private val type: Type? = null,
    private val onClickItem: (Audio) -> Unit = {},
    private val onClickPlay: (Audio, Boolean) -> Unit = { _, _ -> },
    private val onClickAdd: (Audio) -> Unit = {},
    private val onClickMore: (Audio, View) -> Unit = { _, _ -> },
    private val onClickRemove: (Audio) -> Unit = {},
    private val onMaxSelectedReached: () -> Unit = {},
    private var needShowRemove: Boolean = false,
    private var isFolder: Boolean = false,
) :
    BaseSingleAdapter<Audio, ItemSelectAudioBinding>(ItemSelectAudioBinding::inflate) {
    var isPlaying = false
    var currentPlaySelected = -1
    private var currentAddSelected = mutableSetOf<String>()

    companion object {
        const val PLAY_STATE = "PLAY_STATE"
        const val ADD_STATE = "ADD_STATE"
    }

    fun setIsPlay(isPlay: Boolean){
        isPlaying = isPlay
        notifyItemChanged(currentPlaySelected, PLAY_STATE)
    }

    override fun createViewHolder(binding: ItemSelectAudioBinding): BaseViewHolder<ItemSelectAudioBinding> {
        return BaseViewHolder(binding).apply {
            binding.root.setOnClickListener {
                val item = getItem(bindingAdapterPosition)
                onClickItem(item)
            }

            binding.icPlay.setOnClickListener {
                val item = getItem(bindingAdapterPosition)
                togglePlayPause(bindingAdapterPosition)
                onClickPlay(item, isPlaying)
            }

            binding.icAdd.setOnClickListener {
                val item = getItem(bindingAdapterPosition)

                if (needShowRemove) {
                    onClickRemove(item)
                } else {
                    toggleAdd(bindingAdapterPosition)
                    onClickAdd(item)
                }
            }

            binding.icMore.setOnClickListener {
                val item = getItem(bindingAdapterPosition)
                onClickMore(item, binding.icMore)
            }
        }
    }

    override fun bindingViewHolder(
        holder: BaseViewHolder<ItemSelectAudioBinding>,
        position: Int,
    ) {
        val item = getItem(position)
        holder.binding.apply {
            txtDuration.text = item.duration.formatSecondsToTime()
            txtSize.text = item.size.formatFileSize()
            txtType.text = item.extension.uppercase()
            txtTitle.apply {
                /* isSelected = true*/
                text = item.title
            }

            icMore.visibleOrGone(isFolder)

            icAdd.apply {
                visibleOrGone(type == Type.MERGE_AUDIO || type == Type.MIX)
                if (needShowRemove) {
                    setImageResource(R.drawable.ic_remove_audio)
                } else {
                    val iconAddOrRemove =
                        if (currentAddSelected.contains(item.id)) R.drawable.ic_add_done
                        else R.drawable.ic_add
                    setImageResource(iconAddOrRemove)
                }
            }

            root.setBackgroundColor(
                if (currentAddSelected.contains(item.id)) context.getColor(R.color.color_item_selected)
                else context.getColor(R.color.color_background)
            )
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int, payloads: List<Any?>) {
        super.onBindViewHolder(holder, position, payloads)
        if (payloads.isNotEmpty()) {
            payloads.forEach { payload ->
                when (payload) {
                    PLAY_STATE -> {
                        (holder as BaseViewHolder<ItemSelectAudioBinding>).binding.icPlay.setImageResource(
                            if (isPlaying && currentPlaySelected == position) R.drawable.ic_pause else R.drawable.ic_play
                        )
                    }

                    ADD_STATE -> {
                        val item = getItem(position)
                        val iconAddOrRemove =
                            if (currentAddSelected.contains(item.id)) R.drawable.ic_add_done
                            else R.drawable.ic_add

                        (holder as BaseViewHolder<ItemSelectAudioBinding>).binding.icAdd.setImageResource(
                            iconAddOrRemove
                        )

                        holder.binding.root.setBackgroundColor(
                            if (currentAddSelected.contains(item.id)) context.getColor(R.color.color_item_selected)
                            else context.getColor(R.color.color_background)
                        )
                    }
                }
            }
        }
    }

    private fun toggleAdd(position: Int) {
        if (position == RecyclerView.NO_POSITION) return

        val item = getItem(position)
        val id = item.id

        if (currentAddSelected.contains(id)) {
            currentAddSelected.remove(id)
            notifyItemChanged(position, ADD_STATE)
            return
        }

        if (type == Type.MIX && currentAddSelected.size >= 2) {
            onMaxSelectedReached.invoke()
            return
        }

        currentAddSelected.add(id)
        notifyItemChanged(position, ADD_STATE)
    }


    fun togglePlayPause(position: Int) {
        if (position == currentPlaySelected) {
            isPlaying = !isPlaying
            notifyItemChanged(position, PLAY_STATE)
        } else {
            val previousPosition = currentPlaySelected
            currentPlaySelected = position
            isPlaying = true
            notifyItemChanged(previousPosition, PLAY_STATE)
            notifyItemChanged(position, PLAY_STATE)
        }
    }
}