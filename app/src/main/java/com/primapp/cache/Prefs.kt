package com.primapp.cache

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class Prefs @SuppressLint("CommitPrefEdits") internal constructor(context: Context) {
    fun save(key: String?, value: Boolean) {
        editor.putBoolean(key, value).apply()
    }

    fun save(key: String?, value: String?) {
        editor.putString(key, value).apply()
    }

    fun save(key: String?, value: Int) {
        editor.putInt(key, value).apply()
    }

    fun save(key: String?, value: Float) {
        editor.putFloat(key, value).apply()
    }

    fun save(key: String?, value: Long) {
        editor.putLong(key, value).apply()
    }

    fun save(key: String?, value: Set<String?>?) {
        editor.putStringSet(key, value).apply()
    }

    fun save(key: String?, value: Double) {
        editor.putLong(key, java.lang.Double.doubleToRawLongBits(value)).apply()
    }

    // to save object in prefrence
    fun save(key: String?, `object`: Any?) {
        requireNotNull(`object`) { "object is null" }
        require(!(key == null || key == "")) { "key is empty or null" }
        editor.putString(key, GSON.toJson(`object`)).apply()
    }

    // To get object from prefrences
    fun <T> getObject(key: String, a: Class<T>?): T? {
        val gson = preferences.getString(key, null)
        return if (gson == null) {
            null
        } else {
            try {
                GSON.fromJson(gson, a)
            } catch (e: Exception) {
                throw IllegalArgumentException(
                    "Object storaged with key "
                            + key + " is instanceof other class"
                )
            }
        }
    }

    fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return preferences.getBoolean(key, defValue)
    }

    fun getString(key: String?, defValue: String?): String? {
        return preferences.getString(key, defValue)
    }

    fun getInt(key: String?, defValue: Int): Int {
        return preferences.getInt(key, defValue)
    }

    fun getFloat(key: String?, defValue: Float): Float {
        return preferences.getFloat(key, defValue)
    }

    fun getDouble(key: String?, defaultValue: Double): Double {
        return java.lang.Double.longBitsToDouble(
            preferences.getLong(
                key,
                java.lang.Double.doubleToLongBits(defaultValue)
            )
        )
    }

    fun getLong(key: String?, defValue: Long): Long {
        return preferences.getLong(key, defValue)
    }

    fun getStringSet(
        key: String?,
        defValue: Set<String?>?
    ): Set<String>? {
        return preferences.getStringSet(key, defValue)
    }

    val all: Map<String, *>
        get() = preferences.all

    fun remove(key: String?) {
        editor.remove(key).apply()
    }

    fun removeAll() {
        editor.clear()
        editor.apply()
    }

    fun <T> getList(name: String, type:Type): List<T>? {
        return GSON.fromJson(
            preferences.getString(name, ""),
            type
        )
            ?: return ArrayList<T>()
    }

    fun <T> setList(key: String?, list: List<T>?) {
        val gson = Gson()
        val json = gson.toJson(list)
        editor.putString(key, json).apply()
    }

    private class Builder(context: Context?) {
        private val context: Context

        /**
         * Method that creates an instance of Prefs
         *
         * @return an instance of Prefs
         */
        fun build(): Prefs {
            return Prefs(context)
        }

        init {
            requireNotNull(context) { "Context must not be null." }
            this.context = context.applicationContext
        }
    }

    fun getPreferences(): SharedPreferences {
        return preferences
    }

    companion object {
        private const val TAG = "Prefs"
        var singleton: Prefs? = null
        lateinit var preferences: SharedPreferences
        lateinit var editor: SharedPreferences.Editor
        private val GSON = Gson()
        fun with(context: Context?): Prefs? {
            if (singleton == null) {
                singleton =
                    Builder(context).build()
            }
            return singleton
        }
    }

    init {
        preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE)
        editor = preferences.edit()
    }
}