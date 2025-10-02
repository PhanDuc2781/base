package com.example.base_project.ui.main.language

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.base_project.R
import com.example.base_project.base.AdapterEquatable

enum class LanguageModel(
    val id: Int = 0,
    @StringRes val nameLanguage: Int,
    @DrawableRes val iconFlag: Int,
    val localizeCode: String,
    var isShow: Boolean
) : AdapterEquatable {

    @SuppressLint("ConstantLocale")
    FRENCH(2, R.string.french, R.drawable.ic_flag_french, "fr", true),
    INDIA(3, R.string.india, R.drawable.ic_flag_india, "hi", true),
    INDONESIA(4, R.string.indonesia, R.drawable.ic_flag_indonesia, "in", true),
    JAPAN(5, R.string.japan, R.drawable.ic_flag_japan, "ja", true),
    BRAZIL(6, R.string.brazil, R.drawable.ic_flag_brazil, "pt", true),
    KOREA(7, R.string.korean, R.drawable.ic_flag_korean, "ko", true),
    VIETNAM(1, R.string.vietnam, R.drawable.ic_flag_vietnam, "vi", true),
    TURKEY(8, R.string.turkey, R.drawable.ic_flag_turkey, "tr", true),
    SPANISH(9, R.string.spanish, R.drawable.ic_flag_spanish, "es", true),
    ITALY(10, R.string.italian, R.drawable.ic_italy, "it", true),
    ENGLISH(0, R.string.english, R.drawable.ic_flag_english, "en", true);

    companion object {

        fun get(id: Int) = entries.firstOrNull { it.id == id }

        var current: LanguageModel = ENGLISH

        fun setCurrent(id: Int) {
            entries.forEach { it.isShow = true }
            current = get(id) ?: ENGLISH
            current.isShow = false
        }

        fun getIndex(key: String): Int = entries.find { it.localizeCode == key }?.id ?: ENGLISH.id
    }
}