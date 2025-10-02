package com.example.base_project.enum

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class Type : Parcelable {
    TRIM,
    MERGE_AUDIO,
    VIDEO_CONVERTER,
    AUDIO_CONVERTER,
    MIX,
    SPLIT,
    REMOVE_PART,
    MUTE_PART,
    VOLUME,
    SPEED,
    COMPRESS,
    REVERSE
}