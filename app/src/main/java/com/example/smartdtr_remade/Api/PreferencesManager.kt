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

    fun saveTeacherId(teacherId: String?) {
        val editor = sharedPreferences.edit()
        editor.putString("teacher_id", teacherId)
        editor.apply()
    }

    fun getTeacherId(): String? {
        return sharedPreferences.getString("teacher_id", null)
    }

    fun saveStudentId(studentId: String) {
        val editor = sharedPreferences.edit()
        editor.putString("student_id", studentId)
        editor.apply()
    }

    fun getStudentId(): String? {
        return sharedPreferences.getString("student_id", null)
    }

    fun saveUserId(userId: String?) {
        val editor = sharedPreferences.edit()
        editor.putString("user_id", userId)
        editor.apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }

    fun clearUserId() {
        val editor = sharedPreferences.edit()
        editor.remove("user_id")
        editor.apply()
    }
}
