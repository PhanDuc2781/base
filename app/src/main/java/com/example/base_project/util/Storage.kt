package com.example.base_project.util

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaRouter.UserRouteInfo
import com.example.base_project.BuildConfig
import com.example.base_project.applicattion.storage
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Target(AnnotationTarget.CLASS)
annotation class StorageEncodable

@Singleton
class Storage @Inject constructor(
    @ApplicationContext val context: Context,
    val gson: Gson
) {
    companion object {
        const val VERSION = 1
    }

    private val prefs = context.getSharedPreferences(
        BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE
    )

    var version by sharedPref(gson, prefs, 0)
    var userInfo: UserRouteInfo? by optionalSharedPref(gson, prefs)
    var accessToken by sharedPref(gson, prefs, "")

    fun logout() {
        accessToken = ""
        userInfo = null
    }

    fun migrate() {
        PrefMigrate.entries.filter { it.newVersion in (version + 1)..VERSION }
            .sortedBy { it.newVersion }.forEach { it.migrate() }
    }
}

inline fun <reified T> sharedPref(
    gson: Gson, prefs: SharedPreferences, defaultValue: T = defaultForType()
) = object : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        prefs[gson, getKey(thisRef, property), defaultValue] ?: defaultValue

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        prefs[gson, getKey(thisRef, property)] = value
    }

    private fun getKey(thisRef: Any, property: KProperty<*>) =
        BuildConfig.APPLICATION_ID + property.name
}

inline fun <reified T> optionalSharedPref(
    gson: Gson, prefs: SharedPreferences, defaultValue: T? = null
) = object : ReadWriteProperty<Any, T?> {
    override fun getValue(thisRef: Any, property: KProperty<*>) =
        prefs[gson, getKey(thisRef, property), defaultValue]

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        prefs[gson, getKey(thisRef, property)] = value
    }

    private fun getKey(thisRef: Any, property: KProperty<*>) =
        BuildConfig.APPLICATION_ID + property.name
}

inline fun <reified T> defaultForType(): T = when (T::class) {
    String::class -> "" as T
    Int::class -> 0 as T
    Boolean::class -> false as T
    Float::class -> 0F as T
    Long::class -> 0L as T
    else -> throw IllegalArgumentException("Default value not found for type ${T::class.simpleName}")
}

inline operator fun <reified T> SharedPreferences.get(
    gson: Gson,
    key: String,
    defaultValue: T?
): T? {
    return when (T::class) {
        Boolean::class -> this.getBoolean(key, defaultValue as? Boolean ?: false) as T
        Float::class -> this.getFloat(key, defaultValue as? Float ?: 0f) as T
        Double::class -> java.lang.Double.longBitsToDouble(
            this.getLong(key, java.lang.Double.doubleToLongBits(defaultValue as? Double ?: 0.0))
        ) as T
        Int::class -> this.getInt(key, defaultValue as? Int ?: 0) as T
        Long::class -> this.getLong(key, defaultValue as? Long ?: 0L) as T
        String::class -> this.getString(key, defaultValue as? String ?: "") as T
        else -> {
            when {
                defaultValue is Set<*> -> this.getStringSet(key, defaultValue as? Set<String> ?: emptySet()) as T
                T::class.isEncodable() -> getString(key, null)?.let { gson.fromJson(it, T::class.java) }
                else -> defaultValue
            }
        }
    }
}


inline operator fun <reified T> SharedPreferences.set(gson: Gson, key: String, value: T?) {
    val editor = this.edit()

    when (T::class) {
        Boolean::class -> (value as? Boolean)?.let { editor.putBoolean(key, it) }
        Float::class -> (value as? Float)?.let { editor.putFloat(key, it) }
        Double::class -> (value as? Double)?.let {
            editor.putLong(
                key, java.lang.Double.doubleToLongBits(it)
            )
        }

        Int::class -> (value as? Int)?.let { editor.putInt(key, it) }
        Long::class -> (value as? Long)?.let { editor.putLong(key, it) }
        String::class -> (value as? String)?.let { editor.putString(key, it) }
        else -> {
            if (T::class.isEncodable()) {
                editor.putString(key, if (value == null) "" else gson.toJson(value))
            } else if (value is Set<*>) {
                (value as? Set<String>)?.let { editor.putStringSet(key, it) }
            }
        }
    }

    editor.apply()
}

val Storage.isLogin: Boolean
    get() = accessToken.trim().isNotEmpty()

enum class PrefMigrate(val newVersion: Int) {
    M_0_1(1);

    fun migrate() {
        when (this) {
            M_0_1 -> {
                storage.version = newVersion
            }
        }
    }
}

fun KClass<*>.isEncodable(): Boolean =
    annotations.firstOrNull { it.annotationClass == StorageEncodable::class } != null