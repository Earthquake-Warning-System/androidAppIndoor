package com.example.udptest

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.PARTIAL_WAKE_LOCK
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.example.udptest.Singleton.detect
import com.example.udptest.Singleton.onCreateNum
import org.jetbrains.anko.powerManager
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

val schedule = Executors.newScheduledThreadPool(2)!!

lateinit var wakeLock : PowerManager.WakeLock
var status : Boolean = false
var future : ScheduledFuture<*>? = null
var startFlag = false

class DetectionService : Service() {
    private val mBinder = MyBinder()

    @SuppressLint("InvalidWakeLockTag")
    override fun onCreate() {
        super.onCreate()
        //val pm = getSystemService(Context.POWER_SERVICE)
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"mytag")
        wakeLock.acquire()
        status = true
        onCreateNum ++

        Log.d(TAG, "onCreate() executed")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if(startFlag){
            Log.d(TAG, "repeat start")
            return super.onStartCommand(intent, flags, startId)
        }


        Log.d(TAG, "onStartCommand() executed")
        Thread(Runnable {
            detect = SetDetect()
            detect?.turnOn()
        }).start()
        startFlag = true
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(status){
            wakeLock.release()
            status = false
            startFlag = false
        }
        if(future!=null){
            future?.cancel(true)
        }



        //schedule.shutdown()
        /*if (!schedule.isShutdown){
            Thread.sleep(1000)
        }*/
        /*if(!schedule.awaitTermination(3, TimeUnit.SECONDS)){
            schedule.shutdownNow()
        }*/
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