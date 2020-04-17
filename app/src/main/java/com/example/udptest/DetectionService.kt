package com.example.udptest

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.udptest.MainActivity.Companion.detect
import kotlinx.android.synthetic.main.activity_main.*


class DetectionService : Service() {
    private val mBinder = MyBinder()
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() executed")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand() executed")
        detect = SetDetect()
        detect?.turnOn()
        println("start")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy() executed")
    }

    override fun onBind(intent: Intent?): IBinder {
        return mBinder
    }

    internal inner class MyBinder : Binder() {
        fun startDownload() {
            Log.d("TAG", "startDownload() executed")
            // 執行任務
        }
    }

    companion object {
        const val TAG = "MyService"
    }
}