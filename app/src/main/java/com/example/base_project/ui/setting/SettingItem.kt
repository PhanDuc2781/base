package com.example.base_project.ui.setting

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.base_project.R
import com.example.base_project.base.AdapterEquatable
import com.example.base_project.ui.main.language.LanguageModel

enum class SettingItem(
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
) : AdapterEquatable {
    LANGUAGE(R.drawable.ic_setting_language, R.string.language),
    RATE_APP(R.drawable.ic_setting_rate, R.string.rater_us),
    FEEDBACK(R.drawable.ic_setting_feedback, R.string.feedback),
    PRIVACY_POLICY(R.drawable.ic_privacy, R.string.privacy),
    VERSION(R.drawable.ic_version, R.string.version);

    companion object {
        fun getSettingItem(position: Int): SettingItem {
            return entries.getOrElse(position) { LANGUAGE }
        }
    }
}