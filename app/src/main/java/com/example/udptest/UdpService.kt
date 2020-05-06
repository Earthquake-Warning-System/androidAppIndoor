package com.example.udptest

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder

import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.example.udptest.Singleton.serverIp
import com.example.udptest.Singleton.serverPort
import com.example.udptest.Singleton.socket
import org.jetbrains.anko.powerManager
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import com.example.udptest.Singleton.onCreateNum
import java.util.concurrent.ScheduledFuture

lateinit var wakeLock2 : PowerManager.WakeLock
var status2 : Boolean = false
lateinit var future2 : ScheduledFuture<*>
val schedule2 = Executors.newScheduledThreadPool(1)!!

class UdpService : Service() {
    private val mBinder = MyBinder()
    @SuppressLint("InvalidWakeLockTag")
    override fun onCreate() {
        super.onCreate()
        wakeLock2 = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"mytag")
        wakeLock2.acquire()
        status2 = true
        onCreateNum++
        Log.d(TAG, "onCreate() executed")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand() executed")


        Thread(Runnable { UdpServer(this, socket).start()
        }).start()

        Thread(Runnable {
            UdpSender(socket).bootAskSend()
            KpAlive().randomtime()
        }).start()

/*
        Thread(Runnable {


            val schedule = Executors.newScheduledThreadPool(2)
            schedule.scheduleAtFixedRate(Runnable {
                if(serverIp != "" || serverPort != 0) {
                    //println("check ip port in Main")
                    //kpalive with server
                    UdpSender(socket).kpAckSend()
                    //println("ack finish"+ Date())

                }
            }, 1000 , 30000 , TimeUnit.MILLISECONDS)
            */
/*Timer().schedule(timerTask {
                //println("ack start"+ Date())
                if(MainActivity.serverIp != "" || MainActivity.serverPort != 0) {
                    //println("check ip port in Main")
                    //kpalive with server
                    UdpSender(socket).kpAckSend()
                    //println("ack finish"+ Date())

                }
            },30000, 30000)*//*

        }).start()
*/


        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(status2){
            wakeLock.release()
            status2 = false
        }
        future2.cancel(true)
        /*schedule2.shutdown()
        if(!schedule2.awaitTermination(3, TimeUnit.SECONDS)){
            schedule2.shutdownNow()
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