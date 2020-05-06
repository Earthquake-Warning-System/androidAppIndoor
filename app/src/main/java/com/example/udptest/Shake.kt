package com.example.udptest

import android.content.Intent
import android.util.Log
import com.example.udptest.Singleton.bell
import com.example.udptest.Singleton.broadcast
import com.example.udptest.Singleton.serverIp
import com.example.udptest.Singleton.serverPort
import com.example.udptest.Singleton.timeCounter
import com.example.udptest.Singleton.socket
import kotlin.math.absoluteValue

class Shake(private val eqValue :Double) {
    fun detected(){
        if(timeCounter == 0){
            if (serverIp != "" || serverPort != 0) {
                UdpSender(socket).eqEventSend()
            }
            timeCounter = 3000
        }
        Log.d("shakedata",eqValue.toString())
        //ring
        if(timeCounter == 3000) {
            val intent = Intent("MyMessage")
            intent.putExtra("message", "detect_shake")
            broadcast?.sendBroadcast(intent)
        }
        val maxVolume = 15.0
        var eqSound = eqValue.absoluteValue
        if(eqSound>15){eqSound=15.0}
        val volume =(1- (Math.log(maxVolume - eqSound) / Math.log(maxVolume))).toFloat()
        bell.ring(volume,6000 )
    }
}