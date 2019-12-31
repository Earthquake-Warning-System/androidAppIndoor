package com.example.udptest

import android.content.Intent
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.absoluteValue

class SetDetect {
    private val timer = Timer()
    private var counter = 0

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
            timer.schedule(0, 20) {
                if (MainActivity.timeCounter == 500){
                    MainActivity.broadcast?.sendBroadcast(intent)
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                    println("normal")
                }else if (MainActivity.timeCounter != 0){
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                }
                eqCtx.computing(MainActivity.x.absoluteValue)

            }
        }else if(yValue<xValue&&yValue<zValue){
            timer.schedule(0, 20) {
                if (MainActivity.timeCounter == 500){
                    MainActivity.broadcast?.sendBroadcast(intent)
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                    println("normal")
                }else if (MainActivity.timeCounter != 0){
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                }
                eqCtx.computing(MainActivity.y)

            }
        }else if(zValue<xValue&&zValue<yValue){
            timer.schedule(0, 20) {
                if (MainActivity.timeCounter == 500){
                    MainActivity.broadcast?.sendBroadcast(intent)
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                    println("normal")
                }else if (MainActivity.timeCounter != 0){
                    MainActivity.timeCounter = MainActivity.timeCounter - 1
                }
                eqCtx.computing(MainActivity.z)

            }
        }else{
            println("detect error")
        }
    }
    fun turnOff() {
        timer.cancel()
    }
}