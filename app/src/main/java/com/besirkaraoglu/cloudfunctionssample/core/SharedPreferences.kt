package com.besirkaraoglu.cloudfunctionssample.core

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

class SharedPreferences
@Inject constructor(
    context: Context
){
    companion object{
        const val KEY_UID = "uid"
    }
    private val sharedPref: SharedPreferences = context.getSharedPreferences("sp", Context.MODE_PRIVATE)

    fun setUid(uid: String){
        sharedPref.edit().putString(KEY_UID,uid).apply()
    }

    fun getUid(): String?{
        return sharedPref.getString(KEY_UID,null)
    }
}