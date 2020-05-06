package com.example.udptest

import android.util.Log
import com.example.udptest.Singleton.serverIp
import com.example.udptest.Singleton.serverPort
import com.example.udptest.Singleton.socket
import com.example.udptest.Singleton.onCreateNum
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class KpAlive {
    fun randomtime () {

        //val schedule = Executors.newScheduledThreadPool(1)
        var counter = 0
        var nextKp = 5

        future2 = schedule2.scheduleAtFixedRate(Runnable {
            Log.d("onCreateNum : ", onCreateNum.toString())

            if(nextKp != counter){
                counter++
                println("counter number : $counter")
                if(serverIp != "" || serverPort != 0) {
                    //println("check ip port in Main")
                    //kpalive with server
                    UdpSender(socket).kpAckSend()
                    //println("ack finish"+ Date())
                    println("simple kp")
                }

            }else{
                nextKp = (60..180).random()
                //nextKp = 6
                counter = 0

                if (serverIp != "" || serverPort != 0) {
                    //kpalive with server
                    UdpSender(socket).kpAliveSend()
                } else {
                    //ask bs for cs again
                    UdpSender(socket).bootAskSend()
                }

            }

        }, 0 , 30000, TimeUnit.MILLISECONDS)

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