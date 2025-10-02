package com.example.base_project.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.base_project.R
import com.example.base_project.base.AdapterEquatable

enum class ItemOtherOption(
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
) : AdapterEquatable {
    AUDIO_CONVERT(R.drawable.ic_audio_converter, R.string.audio_converter),
    MIX(R.drawable.ic_mix, R.string.mix),
    SPLIT(R.drawable.ic_sprit, R.string.split),
    REMOVE_PART(R.drawable.ic_remove_part, R.string.remove_part),
    MUTE_PART(R.drawable.ic_mute_part, R.string.mute_path),
    VOLUME(R.drawable.ic_volume, R.string.volume),
    SPEED(R.drawable.ic_speed, R.string.speed),
    COMPRESS(R.drawable.ic_compare, R.string.compress),
    REVERSE(R.drawable.ic_reverse, R.string.reverse)
}