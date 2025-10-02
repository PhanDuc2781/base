package com.example.base_project.ui.main.main

import com.example.base_project.base.AdapterEquatable

sealed class HomeDataView : AdapterEquatable {
    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_MAIN_FEATURE = 1
        const val VIEW_TYPE_OTHER = 2
    }

    data object HeaderView : HomeDataView() {
        override fun getViewType(): Int = VIEW_TYPE_HEADER
    }

    data object MainFeatureView : HomeDataView() {
        override fun getViewType(): Int = VIEW_TYPE_MAIN_FEATURE
    }

    data object OtherView : HomeDataView() {
        override fun getViewType(): Int = VIEW_TYPE_OTHER
    }
}