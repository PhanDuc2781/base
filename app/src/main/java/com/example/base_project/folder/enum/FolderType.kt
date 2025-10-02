package com.example.base_project.folder.enum

import android.os.Parcelable
import androidx.annotation.StringRes
import com.example.base_project.R
import com.example.base_project.common.util.CommonConstants
import kotlinx.parcelize.Parcelize

@Parcelize
enum class FolderType(
    @StringRes val typeName: Int,
    val fileDir: String,
) : Parcelable {
    TRIM_DIR(R.string.trummed, CommonConstants.TRIM_DIR),
    MERGED(R.string.merged, CommonConstants.MERGE_DIR),
    VIDEO_CONVERTER(R.string.video_converter, CommonConstants.VIDEO_DIR),
    MIX(R.string.mix, CommonConstants.MIX_DIR),
    SPLIT(R.string.split, CommonConstants.SPLIT_DIR),
    VOLUME(R.string.volume, CommonConstants.VOLUME_DIR),
    SPEED(R.string.speed, CommonConstants.SPEED_DIR),
    AUDIO_CONVERTER(R.string.audio_converter, CommonConstants.CONVERT_DIR),
    REMOVE_PATH(R.string.remove_part, CommonConstants.REMOVE_DIR),
    MUTE_PATH(R.string.mute_path, CommonConstants.MUTE_DIR),
    COMPRESS(R.string.compress, CommonConstants.COMPRESS_DIR),
    REVERSE(R.string.reverse, CommonConstants.REVERSE_DIR);

    companion object {
        fun getFolderType(position: Int): FolderType? {
            return entries.getOrNull(position) ?: TRIM_DIR
        }

        fun getPosition(dir: String): Int {
            return entries.indexOfFirst { it.fileDir == dir }
        }
    }
}