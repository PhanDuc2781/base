package com.example.base_project.data.remote

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> =
    mapNotNull { (key, value) -> value?.let { key to it } }.toMap()

fun <K, V> Map<K, V>.toHashMap(): HashMap<K, V> = HashMap(this)

fun <T : Any> toMap(obj: T): Map<String, Any?> {
    return (obj::class as KClass<T>).memberProperties.associate { prop ->
        prop.name to prop.get(obj)?.let { value ->
            if (value::class.isData) {
                toMap(value)
            } else {
                value
            }
        }
    }
}

interface Decodable

fun Decodable.decode(): HashMap<String, Any> {
    return toMap(this).filterNotNullValues().toHashMap()
}