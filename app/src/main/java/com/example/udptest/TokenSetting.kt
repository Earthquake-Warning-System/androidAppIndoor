package com.example.udptest

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

var sharedPreferences: SharedPreferences? = null

class TokenSetting{


    fun listToken():String?{

        var allTokens : String? = ""
        for (i in 0 ..3){
            val key = "Pair"+i
            val ids = sharedPreferences!!.getString(key , "")
            if (ids!=""){
                allTokens += "Pair " + i.toString()  + ","
                Log.d("token"+i, ids)
            }
        }
        return allTokens
    }
    fun testToken(number : String?){
        val key = "Pair"+number
        Log.d("test token: ",key)
        val ids = sharedPreferences!!.getString(key, "")
        if(ids!=""){
            FirebaseSender.pushFCMNotification(ids , "TEST", "Hello")
        }
    }
    fun deleteToken(number : String?){
        val key = "Pair"+number
        Log.d("delete token: ",key)
        val ids = sharedPreferences!!.getString(key, "")
        if(ids!=""){
            sharedPreferences!!.edit().remove(key).apply()
        }
    }
    fun setShare(shared:SharedPreferences){
         sharedPreferences = shared
    }
}