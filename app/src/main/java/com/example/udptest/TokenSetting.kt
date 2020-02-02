package com.example.udptest

import android.content.Context
import android.util.Log

class TokenSetting(val context : Context) {
    fun listToken():String?{
        val sharedPreferences =  context.getSharedPreferences("DATA", 0)

        var allTokens : String? = ""
        for (i in 0 ..3){
            val key = "Pair"+i
            val ids = sharedPreferences.getString(key , "")
            if (ids!=""){
                allTokens += "Pair " + i.toString() + "\n"
                Log.d("token"+i, ids)
            }
        }
        return allTokens
    }
    fun testToken(number : String?){
        val sharedPreferences =  context.getSharedPreferences("DATA", 0)
        val key = "Pair"+number
        Log.d("test token: ",key)
        val ids = sharedPreferences.getString(key, "")
        if(ids!=""){
            FirebaseSender.pushFCMNotification(ids , "TEST", "Hello")
        }
    }
    fun deleteToken(number : String?){
        val sharedPreferences =  context.getSharedPreferences("DATA", 0)
        val key = "Pair"+number
        Log.d("test token: ",key)
        val ids = sharedPreferences.getString(key, "")
        if(ids!=""){
            sharedPreferences.edit().remove(key).apply()
        }
    }
}