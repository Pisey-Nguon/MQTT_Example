package com.pisey.mqttexample

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(private val context: Context) {

    private fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
        return context.getSharedPreferences( name, mode)
    }

    fun putString(key: String, value: String) {
        val sharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String): String {
        val sharedPreferences = getSharedPreferences(key, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, defaultValue)!!
    }
}
