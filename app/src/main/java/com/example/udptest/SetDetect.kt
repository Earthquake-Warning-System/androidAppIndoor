package com.example.udptest


import android.content.Intent

import java.util.concurrent.Executors

import java.util.concurrent.TimeUnit

import kotlin.math.absoluteValue

class SetDetect {
    private val schedule = Executors.newScheduledThreadPool(2)
    private val eqCtx = Calculation(object : Calculation.OnResultListener {
        override fun onOccur(x: Double) {
            Shake(x).detected()
        }
    })
    fun turnOn() {

        val xValue = (MainActivity.x.absoluteValue - 9.80665).absoluteValue
        val yValue = (MainActivity.y.absoluteValue - 9.80665).absoluteValue
        val zValue = (MainActivity.z.absoluteValue - 9.80665).absoluteValue
        val intent = Intent("MyMessage")
        intent.putExtra("message", "normal")
        if(xValue<yValue&&xValue<zValue) {
            schedule.scheduleAtFixedRate(Runnable {
                if (MainActivity.timeCounter == 500){
                    MainActivity.broadcast?.sendBroadcast(intent)
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                    println("normal")
                }else if (MainActivity.timeCounter != 0){
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                }
                eqCtx.computing(MainActivity.x.absoluteValue)
            }, 0 , 20 , TimeUnit.MILLISECONDS)

            /*timer.schedule(0, 20) {
                if (MainActivity.timeCounter == 500){
                    MainActivity.broadcast?.sendBroadcast(intent)
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                    println("normal")
                }else if (MainActivity.timeCounter != 0){
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                }
                eqCtx.computing(MainActivity.x.absoluteValue)

            }*/
        }else if(yValue<xValue&&yValue<zValue){

            schedule.scheduleAtFixedRate(Runnable {
                if (MainActivity.timeCounter == 500){
                    MainActivity.broadcast?.sendBroadcast(intent)
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                    println("normal")
                }else if (MainActivity.timeCounter != 0){
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                }
                eqCtx.computing(MainActivity.y)
            }, 0 , 20 , TimeUnit.MILLISECONDS)



            /*timer.schedule(0, 20) {
                if (MainActivity.timeCounter == 500){
                    MainActivity.broadcast?.sendBroadcast(intent)
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                    println("normal")
                }else if (MainActivity.timeCounter != 0){
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                }
                eqCtx.computing(MainActivity.y)

            }*/
        }else if(zValue<xValue&&zValue<yValue){

            schedule.scheduleAtFixedRate(Runnable {
                if (MainActivity.timeCounter == 500){
                    MainActivity.broadcast?.sendBroadcast(intent)
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                    println("normal")
                }else if (MainActivity.timeCounter != 0){
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                }
                eqCtx.computing(MainActivity.z)

            }, 0 , 20 , TimeUnit.MILLISECONDS)



            /*timer.schedule(0, 20) {
                if (MainActivity.timeCounter == 500){
                    MainActivity.broadcast?.sendBroadcast(intent)
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                    println("normal")
                }else if (MainActivity.timeCounter != 0){
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                }
                eqCtx.computing(MainActivity.z)

            }*/
        }else{
            println("detect error")
        }
    }
    fun turnOff() {
        schedule.shutdown()
        if(!schedule.awaitTermination(3,TimeUnit.SECONDS)){
            schedule.shutdownNow()
        }
    }
}