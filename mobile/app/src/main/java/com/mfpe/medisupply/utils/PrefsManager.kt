package com.mfpe.medisupply.utils

import android.content.Context

class PrefsManager private constructor(private val mCtx: Context) {

    fun saveUserId(id: Int) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_id", id)
        editor.apply()
    }

    val getuserId: Int
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getInt("user_id", 0)
        }

    fun saveAuthToken(token: String) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.apply()
    }

    val getAuthToken: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString("auth_token", "")
        }

    fun saveUserFullName(fullName: String) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_full_name", fullName)
        editor.apply()
    }

    val getUserFullName: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString("user_full_name", "")
        }

    fun saveUserEmail(email: String) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_email", email)
        editor.apply()
    }

    val getUserEmail: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString("user_email", "")
        }

    fun saveUserRole(role: String) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_role", role)
        editor.apply()
    }

    val getUserRole: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString("user_role", "")
        }

    fun clear() {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val SHARED_PREF_NAME = "my_shared_prefs"
        private var mInstance: PrefsManager? = null
        @Synchronized
        fun getInstance(mCtx: Context): PrefsManager {
            if (mInstance == null) {
                mInstance = PrefsManager(mCtx)
            }
            return mInstance as PrefsManager
        }
    }
}