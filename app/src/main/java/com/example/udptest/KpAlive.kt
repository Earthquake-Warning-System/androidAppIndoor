package com.example.udptest

import android.util.Log
import com.example.udptest.MainActivity.Singleton.socket
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class KpAlive {
    fun randomtime (time : Long) {


        val schedule = Executors.newScheduledThreadPool(2)

        schedule.schedule(Runnable {

            if (MainActivity.serverIp != "" || MainActivity.serverPort != 0) {
                //kpalive with server
                UdpSender(socket).kpAliveSend()
                //Log.d("kp time",time.toString())
                randomtime((300000..600000).random().toLong())
            } else {
                UdpSender(socket).bootAskSend()
                Log.d("ask bs for cs time", time.toString())
                randomtime((300000..600000).random().toLong())
            }

        }, time, TimeUnit.MILLISECONDS)
    }





        /*

        Timer().schedule(timerTask {
            if(MainActivity.serverIp != "" || MainActivity.serverPort != 0) {
                //kpalive with server
                UdpSender(MainActivity.socket).kpAliveSend()
                Log.d("kp time",time.toString())
                randomtime( (1800000..5400000).random().toLong())
            }else{
                UdpSender(MainActivity.socket).bootAskSend()
                Log.d("ask bs for cs time",time.toString())
                randomtime( (1800000..5400000).random().toLong())
            }

        },time, 3600000)
    }*/

}