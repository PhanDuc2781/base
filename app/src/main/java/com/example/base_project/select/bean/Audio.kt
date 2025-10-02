package com.example.base_project.select.bean

import android.os.Parcelable
import com.example.base_project.base.AdapterEquatable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Audio(
    val artist: String,
    val duration: Int,
    val extension: String,
    val id: String,
    val mimeType: String,
    val path: String,
    val size: Long,
    val state: Int = 1,
    val title: String,
    val albumArtist: String? = null
) : Parcelable , AdapterEquatable