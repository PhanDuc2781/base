package com.example.base_project.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.base_project.databinding.ItemLoadmoreBinding

interface Equatable {
    override fun equals(other: Any?): Boolean
    fun equalsContent(other: Any?): Boolean = equals(other)
}

interface AdapterEquatable : Equatable {
    @androidx.annotation.IntRange(from = 0)
    fun getViewType(): Int = 0
}

abstract class BaseAdapter<T : AdapterEquatable?> :
    ListAdapter<T, BaseViewHolder<*>>(object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T & Any, newItem: T & Any): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: T & Any, newItem: T & Any): Boolean {
            return oldItem.equalsContent(newItem)
        }
    }) {

    fun showLoading() {
        val current = currentList.toMutableList()
        current.add(null)
        submitList(current)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        currentList.getOrNull(position)?.let {
            onBindViewHolder(holder, it, position)
        }
    }

    open fun onBindLoadingViewHolder(parent: ViewGroup): ViewBinding =
        ItemLoadmoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun getItemViewType(position: Int): Int {
        return currentList.getOrNull(position)?.getViewType() ?: -1
    }

    fun <V : Any> getData(holder: BaseViewHolder<*>, type: Class<V>): V? {
        return currentList.getOrNull(holder.adapterPosition)?.let {
            type.cast(it)
        }
    }

    abstract fun createViewHolder(parent: ViewGroup, valueBase: T): BaseViewHolder<*>
    abstract fun onBindViewHolder(holder: BaseViewHolder<*>, data: T, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val value = currentList.find { viewType == it?.getViewType() }
        return if (value == null) {
            val binding = onBindLoadingViewHolder(parent)
            BaseViewHolder(binding)
        } else {
            createViewHolder(parent, value)
        }
    }
}

abstract class BaseSingleAdapter<E : AdapterEquatable, B : ViewBinding>(val inflate: Inflate2<B>) :
    BaseAdapter<E>() {

    abstract fun createViewHolder(binding: B): BaseViewHolder<B>
    abstract fun bindingViewHolder(holder: BaseViewHolder<B>, position: Int)

    override fun onBindViewHolder(holder: BaseViewHolder<*>, data: E, position: Int) {
        (holder as? BaseViewHolder<B>)?.apply {
            bindingViewHolder(this, position)
        }
    }

    override fun createViewHolder(parent: ViewGroup, valueBase: E): BaseViewHolder<*> {
        val binding = inflate.invoke(LayoutInflater.from(parent.context), parent, false)
        return createViewHolder(binding)
    }
}

class BaseViewHolder<B : ViewBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)

