package com.example.udptest

import android.content.Intent
import android.util.Log
import com.example.udptest.MainActivity.Singleton.socket
import kotlin.math.absoluteValue

class Shake(private val eqValue :Double) {
    fun detected(){
        if(MainActivity.timeCounter == 0){
            if (MainActivity.serverIp != "" || MainActivity.serverPort != 0) {
                UdpSender(socket).eqEventSend()
            }
            MainActivity.timeCounter = 3000
        }
        Log.d("shakedata",eqValue.toString())
        //ring
        if(MainActivity.timeCounter == 3000) {
            val intent = Intent("MyMessage")
            intent.putExtra("message", "detect_shake")
            MainActivity.broadcast?.sendBroadcast(intent)
        }
        val maxVolume = 15.0
        var eqSound = eqValue.absoluteValue
        if(eqSound>15){eqSound=15.0}
        val volume =(1- (Math.log(maxVolume - eqSound) / Math.log(maxVolume))).toFloat()
        MainActivity.bell.ring(volume,6000 )
    }
}