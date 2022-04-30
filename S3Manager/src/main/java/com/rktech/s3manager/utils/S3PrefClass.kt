package com.rktech.s3manager.utils

import android.content.Context
import android.content.SharedPreferences

class S3PrefClass(context: Context) {

    var sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        const val PREF_NAME = "s3PrefClass"
    }


    fun putString(name: String, value: String?) {
        sharedPreferences.edit().apply {
            putString(name, value)
            apply()
        }
    }

    fun getString(key: String): String =
        sharedPreferences.getString(key, "") ?: ""

    fun clearAll() {
        sharedPreferences.edit()
            .clear()
            .apply()
    }
}