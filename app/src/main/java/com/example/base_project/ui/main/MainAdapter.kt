package com.example.base_project.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.base_project.base.BaseAdapter
import com.example.base_project.base.BaseViewHolder
import com.example.base_project.base.MyActivity
import com.example.base_project.base.setOnSingleClickListener
import com.example.base_project.databinding.LayoutMainFeatureViewBinding
import com.example.base_project.databinding.LayoutMainHeaderBinding
import com.example.base_project.databinding.LayoutOtherOptionViewBinding
import com.example.base_project.ui.main.main.HomeDataView

class MainAdapter(
    private val activity: MyActivity,
    private val onSettingClick: () -> Unit = {},
    private val onClickTrimAudio: () -> Unit = {},
    private val onClickMergeAudio: () -> Unit = {},
    private val onClickVideoConverter: () -> Unit = {},
    private val onClickMyFolder: () -> Unit = {},
) : BaseAdapter<HomeDataView>() {
    override fun createViewHolder(
        parent: ViewGroup,
        valueBase: HomeDataView,
    ): BaseViewHolder<*> {
        val holder = when (valueBase) {
            is HomeDataView.HeaderView -> {
                val binding = LayoutMainHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                val holder = BaseViewHolder(binding)
                binding.icSetting.setOnSingleClickListener {
                    onSettingClick.invoke()
                }
                holder
            }

            is HomeDataView.MainFeatureView -> {
                val binding = LayoutMainFeatureViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                val holder = BaseViewHolder(binding)

                binding.linearTrimAudio.setOnSingleClickListener {
                    onClickTrimAudio.invoke()
                }

                binding.linearVideoConverter.setOnSingleClickListener {
                    onClickVideoConverter.invoke()
                }

                binding.linearMergeAudio.setOnSingleClickListener {
                    onClickMergeAudio.invoke()
                }

                binding.linearMyFolder.setOnSingleClickListener {
                    onClickMyFolder.invoke()
                }
                holder
            }

            is HomeDataView.OtherView -> {
                val binding = LayoutOtherOptionViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                val holder = BaseViewHolder(binding)

                val menuOther = MenuOtherAdapter {
                    (activity as MainActivity).handleMenuItem(it)
                }
                holder.binding.recyclerOther.layoutManager =
                    GridLayoutManager(holder.binding.root.context, 5)
                binding.recyclerOther.adapter = menuOther
                holder
            }
        }

        return holder
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<*>,
        data: HomeDataView,
        position: Int,
    ) {
        when (data) {
            is HomeDataView.HeaderView -> {}
            is HomeDataView.MainFeatureView -> {}
            is HomeDataView.OtherView -> {}
        }
    }
}