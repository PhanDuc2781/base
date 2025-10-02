package com.example.base_project.ui.main.language

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.Locale

object LocaleHelper {
    fun setLocale(context: Context, languageCode: String?, recreate: Boolean = true) {
        val locale = languageCode?.let { Locale(it) }
        locale?.let { Locale.setDefault(it) }
        val resources: Resources = context.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
        if (recreate) (context as Activity).recreate()
    }
}
@Suppress("NAME_SHADOWING")
class MyContextWrapper(base: Context?) : ContextWrapper(base) {
    companion object {
        @SuppressLint("ObsoleteSdkInt")
        fun wrap(context: Context?, language: String): ContextWrapper {
            var context = context
            val config = context?.resources?.configuration
            val sysLocale: Locale? = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                config?.let { getSystemLocale(it) }
            } else {
                config?.let { getSystemLocaleLegacy(it) }
            }
            if (language != "" && sysLocale?.language != language) {
                val locale = Locale(language)
                Locale.setDefault(locale)
                config?.let { setSystemLocale(it, locale) }
            }

            config?.fontScale = 1.0f
            context = config?.let { context.createConfigurationContext(it) }
            return MyContextWrapper(context)
        }

        private fun getSystemLocaleLegacy(config: Configuration): Locale {
            return config.locales.get(0)
        }

        private fun getSystemLocale(config: Configuration): Locale {
            return config.locales[0]
        }

        private fun setSystemLocale(config: Configuration, locale: Locale?) {
            config.setLocale(locale)
        }
    }
}