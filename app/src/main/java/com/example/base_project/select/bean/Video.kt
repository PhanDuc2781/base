package com.example.base_project.select.bean

import android.os.Parcelable
import com.example.base_project.base.AdapterEquatable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Video(
    val id: String,
    val duration: Int,
    val extension: String,
    val path: String,
    val size: Long,
    val state: Int = 1,
    val title: String,
) : Parcelable, AdapterEquatable