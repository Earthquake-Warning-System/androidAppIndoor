package com.example.udptest

import android.util.Log
import java.util.*
import kotlin.concurrent.timerTask

class KpAlive {
    /*fun randomtime (time : Long){
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
        },time)
    }*/
    fun randomtime (time : Long){
        Timer().schedule(timerTask {
            if(MainActivity.serverIp != "" || MainActivity.serverPort != 0) {
                //kpalive with server
                UdpSender(MainActivity.socket).kpAliveSend()
                Log.d("kp time",time.toString())
            }else{
                UdpSender(MainActivity.socket).bootAskSend()
                Log.d("ask bs for cs time",time.toString())
            }
        },time, 3600000)
    }
}