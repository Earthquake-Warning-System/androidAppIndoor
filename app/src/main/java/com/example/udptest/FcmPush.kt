package com.example.udptest

import android.content.Context
import android.util.Log

class FcmPush (private val context: Context){

     fun pushFCMmessage(title: String, body: String){
         Thread(Runnable {
            try {
                val sharedPreferences =  context.getSharedPreferences("DATA", 0)
                for (i in 0 ..3){
                    val key = "Pair"+i
                    val ids = sharedPreferences.getString(key , "")
                    if (ids!=null){
                        FirebaseSender.pushFCMNotification(ids , title, body)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }
}