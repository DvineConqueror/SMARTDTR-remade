package com.example.smartdtr_remade

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    // Token
    fun saveToken(token: String) {
        val existingToken = getToken()
        if (existingToken != token) {
            editor.putString("token", token).apply()
        }
    }


    fun getToken(): String? {
        return sharedPreferences.getString("token", null)
    }

    fun clearToken() {
        editor.remove("token").apply()
    }

    // User ID
    fun saveUserId(userId: String) {
        editor.putString("userId", userId).apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString("userId", null)
    }

    // Teacher ID
    fun saveTeacherId(teacherId: String) {
        editor.putString("teacher_id", teacherId).apply()
    }

    fun getTeacherId(): String? {
        return sharedPreferences.getString("teacher_id", null)
    }

    // Student ID
    fun saveStudentId(studentId: String) {
        editor.putString("student_id", studentId).apply()
    }

    fun getStudentId(): String? {
        return sharedPreferences.getString("student_id", null)
    }

    fun saveUserType(userType: String){
        editor.putString("user_type", userType).apply()
    }

    fun getUserType(): String?{
        return sharedPreferences.getString("user_type", null)
    }

    // Clear all preferences (optional)
    fun clear() {
        editor.clear().apply()
    }
}
