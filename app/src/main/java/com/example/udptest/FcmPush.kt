package com.example.udptest

import android.content.Context
import android.util.Log

class FcmPush (private val context: Context){

     fun pushFCMmessage(title: String, body: String){
         Thread(Runnable {
            try {
                val sharedPreferences =  context.getSharedPreferences("DATA", 0)
                val ids = sharedPreferences.getString("Pair", "")
                val tokens = ids!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (id in tokens) {
                    if (id.length > 0) {
                        Log.v("FCM", "Notification(id):$id")
                        FirebaseSender.pushFCMNotification(id, title, body)
                    } else {
                        Log.v("FCM", "Notification(id): empty")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }
}