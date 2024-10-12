package com.example.smartdtr_remade

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString("user_token", token)
        editor.apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("user_token", null)
    }

    fun clearToken() {
        val editor = sharedPreferences.edit()
        editor.remove("user_token")
        editor.apply()
    }
}
