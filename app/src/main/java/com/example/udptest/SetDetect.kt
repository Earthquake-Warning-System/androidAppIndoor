package com.example.udptest


import android.content.Intent
import android.util.Log
import com.example.udptest.Singleton.broadcast
import com.example.udptest.Singleton.timeCounter
import com.example.udptest.Singleton.x
import com.example.udptest.Singleton.y
import com.example.udptest.Singleton.z
import java.util.*

import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture

import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import kotlin.concurrent.timerTask

import kotlin.math.absoluteValue


class SetDetect() {
    private val timer = Timer()

    private val eqCtx = Calculation(object : Calculation.OnResultListener {
        override fun onOccur(x: Double) {
            Shake(x).detected()
        }
    },"thread pool")

    fun turnOn() {

        val xValue = (x.absoluteValue - 9.80665).absoluteValue
        val yValue = (y.absoluteValue - 9.80665).absoluteValue
        val zValue = (z.absoluteValue - 9.80665).absoluteValue
        val intent = Intent("MyMessage")
        intent.putExtra("message", "normal")
        if(xValue<yValue&&xValue<zValue) {
            future = schedule.scheduleAtFixedRate(Runnable {
                if (timeCounter == 500){
                    broadcast?.sendBroadcast(intent)
                    timeCounter -= 1
                    println("normal")
                }else if (timeCounter != 0){
                    timeCounter -= 1
                }
                eqCtx.computing(x.absoluteValue)
            }, 0 , 20 , TimeUnit.MILLISECONDS)


        }else if(yValue<xValue&&yValue<zValue){

            future = schedule.scheduleAtFixedRate(Runnable {
                if (timeCounter == 500){
                    broadcast?.sendBroadcast(intent)
                    timeCounter -= 1
                    println("normal")
                }else if (timeCounter != 0){
                    timeCounter -= 1
                }
                eqCtx.computing(y)
            }, 0 , 20 , TimeUnit.MILLISECONDS)



        }else if(zValue<xValue&&zValue<yValue){

            future = schedule.scheduleAtFixedRate(Runnable {
                if (timeCounter == 500){
                    broadcast?.sendBroadcast(intent)
                    timeCounter -= 1
                    //println("normal")
                }else if (timeCounter != 0){
                    timeCounter -= 1
                }
                eqCtx.computing(z)
               // Log.d("detectNum",z.toString())
            }, 0 , 20 , TimeUnit.MILLISECONDS)







            /*timer.schedule(0, 20) {
                if (timeCounter == 500){
                    broadcast?.sendBroadcast(intent)
                    timeCounter -= 1
                    println("normal")
                }else if (timeCounter != 0){
                    timeCounter -= 1
                }
                eqCtx.computing(z)

            }*/

        }else{
            println("detect error")
        }
    }

}