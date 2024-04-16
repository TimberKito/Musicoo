package com.player.musicoo.sp

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharedPreferencesHelper(context: Context) {
    companion object {
        const val CURRENT_PLAYING_AUDIO = "current_playing_audio"
    }

    private val preferences: SharedPreferences =  context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)

    var currentPlayingAudio: Boolean by preferences.boolean(
        key = CURRENT_PLAYING_AUDIO,
        defaultValue = false
    )

    private inline fun <reified T : Any> SharedPreferences.boolean(
        key: String,
        defaultValue: T
    ): ReadWriteProperty<Any, T> {
        return object : ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T {
                return when (T::class) {
                    Boolean::class -> getBoolean(key, defaultValue as Boolean) as T
                    else -> throw IllegalArgumentException("Unsupported type")
                }
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
                edit {
                    when (T::class) {
                        Boolean::class -> putBoolean(key, value as Boolean)
                        else -> throw IllegalArgumentException("Unsupported type")
                    }
                }
            }
        }
    }

    private inline fun <reified T : Any> SharedPreferences.string(
        key: String,
        defaultValue: T
    ): ReadWriteProperty<Any, T> {
        return object : ReadWriteProperty<Any, T> {
            override fun getValue(thisRef: Any, property: KProperty<*>): T {
                return when (T::class) {
                    String::class -> getString(key, defaultValue as String) as T
                    else -> throw IllegalArgumentException("Unsupported type")
                }
            }

            override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
                edit {
                    when (T::class) {
                        String::class -> putString(key, value as String)
                        else -> throw IllegalArgumentException("Unsupported type")
                    }
                }
            }
        }
    }
}
