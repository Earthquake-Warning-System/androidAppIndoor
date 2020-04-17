package com.example.udptest

import android.app.Service
import android.content.Intent
import android.os.Binder

import android.os.IBinder
import android.util.Log
import com.example.udptest.MainActivity.Singleton.socket
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit



class UdpService : Service() {
    private val mBinder = MyBinder()
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate() executed")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand() executed")


        Thread(Runnable { UdpServer(this, socket).start()
        }).start()

        Thread(Runnable {
            UdpSender(socket).bootAskSend()
            KpAlive().randomtime(150)
        }).start()

        Thread(Runnable {


            val schedule = Executors.newScheduledThreadPool(2)
            schedule.scheduleAtFixedRate(Runnable {
                if(MainActivity.serverIp != "" || MainActivity.serverPort != 0) {
                    //println("check ip port in Main")
                    //kpalive with server
                    UdpSender(socket).kpAckSend()
                    //println("ack finish"+ Date())
                    println("simple ack")
                }
            }, 1000 , 30000 , TimeUnit.MILLISECONDS)
            /*Timer().schedule(timerTask {
                //println("ack start"+ Date())
                if(MainActivity.serverIp != "" || MainActivity.serverPort != 0) {
                    //println("check ip port in Main")
                    //kpalive with server
                    UdpSender(socket).kpAckSend()
                    //println("ack finish"+ Date())
                    println("simple ack")
                }
            },30000, 30000)*/
        }).start()


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