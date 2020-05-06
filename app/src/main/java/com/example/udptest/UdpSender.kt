package com.example.udptest

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import com.example.tutorial.AddressBookProtos
import com.example.udptest.Singleton.broadcast
import com.example.udptest.Singleton.credit
import com.example.udptest.Singleton.evNum
import com.example.udptest.Singleton.falseAlarm
import com.example.udptest.Singleton.kpNum
import com.example.udptest.Singleton.lastEv
import com.example.udptest.Singleton.lastKp
import com.example.udptest.Singleton.respond
import com.example.udptest.Singleton.serverIp
import com.example.udptest.Singleton.serverPort

import java.io.IOException
import java.net.*
import java.util.*
import java.text.SimpleDateFormat

class UdpSender(private val socket : DatagramSocket){

    private val bootstrapServerIp = "140.115.153.209"
    private val macAddress = Macaddress.getMacAddr()
    private val country = Locale.getDefault().displayCountry

    @SuppressLint("SimpleDateFormat")
    fun logTime(type:Int){
        val date = Date()
        val calendar =  Calendar.getInstance()
        calendar.time = date
        val df =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        if (type == 0){ lastEv = df.format(calendar.time)
        }else if (type == 1){lastKp = df.format(calendar.time)}
        val intent = Intent("MyMessage")
        intent.putExtra("message", "log")
        Log.d("send broadcast:",df.format(calendar.time))
        broadcast?.sendBroadcast(intent)
    }

    fun bootAskSend(){
        val bootAsk = AddressBookProtos.Boot_ask.newBuilder()
        bootAsk.packetType = "3"
        bootAsk.version = "1.0.0"
        bootAsk.serverIp = "0"
        bootAsk.serverPort = 0
        val bootaskPack = bootAsk.build().toByteArray()
        println("bs ask : "+Date())
        Sender(bootstrapServerIp, bootaskPack, 7777, bootaskPack.size, socket).start()
        Thread.sleep(100)
        for (i in 1..5) {
            if (serverIp == "" || serverPort == 0) {
                println("bs ask again : "+Date())
                Sender(bootstrapServerIp, bootaskPack, 7777, bootaskPack.size, socket).start()
                Thread.sleep(100)
            }else if(i > 4){println("connect to server error : "+Date())}
            else{break}
        }
        Thread.sleep(100)
        if(serverIp != "" || serverPort != 0){
            println("connect to cs success : "+ Date())
            kpAckSend()
            kpAliveSend()
        }else{
            println("boot ask fail : "+ Date())
        }
    }
    fun kpAckSend(){
        Thread.sleep(5000)
        //connect to nearest server
        if (serverIp != "" || serverPort != 0) {
            val kpAck = AddressBookProtos.kp_alive_ack.newBuilder()
            kpAck.packetType = "5"
            kpAck.version = "1.0.0"
            val kpAckPack = kpAck.build().toByteArray()
            println("cs ack")
            Sender(serverIp, kpAckPack, serverPort, kpAckPack.size, socket).start()
        }
    }
    fun kpAliveSend(){
        respond = 0
        val kpAlive = AddressBookProtos.kp_alive.newBuilder()
        kpAlive.packetType = "0"
        kpAlive.aliveFlag = 1
        kpAlive.cityCode = country
        kpAlive.countryCode = country
        kpAlive.sensorId = macAddress
        val kpAlivePack = kpAlive.build().toByteArray()
        Sender(serverIp, kpAlivePack, serverPort, kpAlivePack.size, socket).start()
        println("send cs kp : "+ Date() + "kp number " + kpNum+1)
        kpNum++
        logTime(1)
        for(i in 1..6){
            Thread.sleep(3000)
            if(respond == 0) {
                println("kp fail ,try again : "+ Date())
                Sender(serverIp, kpAlivePack, serverPort, kpAlivePack.size, socket).start()
            }else if(i == 6&&respond == 0){
                println("local server not respond, reconnect to bootstrap : "+ Date())
                serverIp = ""
                serverPort = 0
                UdpSender(socket).bootAskSend()
            }else{break}
        }
    }
    fun eqEventSend(){
        val eqEvent = AddressBookProtos.EQ_event.newBuilder()
        val date = Date()
        val ts = date.getTime()
        val tsSec = (ts.toDouble() / 1000).toInt()
        val tsUsec = ts % 1000
        evNum++
        logTime(0)
        val credit = credit.dateCount(ts)
        Log.d("credit" , credit.toString() )
        eqEvent.packetType = "1"
        eqEvent.eventSec = tsSec.toLong()
        eqEvent.eventOutput = credit
        eqEvent.eventUsec = tsUsec
        eqEvent.sensorId = macAddress
        eqEvent.version = "1.0.0"
        val eqEventPack = eqEvent.build().toByteArray()
        println("eq send!!")
        Sender(serverIp, eqEventPack,serverPort, eqEventPack.size, socket).start()
        falseAlarm = true
    }


    private class Sender(strIp: String, str: ByteArray, port: Int, len: Int, socket1: DatagramSocket) : Thread() {

        var sendStrIp = strIp
        var sendStr = str
        var sendLen = len
        var sendPort = port
        val sendSocket = socket1
        override fun run() {
            try {
                val serverAddress = InetAddress.getByName(sendStrIp)
                //Log.d("IP Address", serverAddress.toString())
                val packet = DatagramPacket(sendStr, sendLen, serverAddress, sendPort)
                sendSocket.send(packet)
            } catch (e: SocketException) {
                e.printStackTrace()
                val error = e.toString()
                Log.e("Error by Sender", error)
            } catch (e: UnknownHostException) {
                e.printStackTrace()
                val error = e.toString()
                Log.e("Error by Sender", error)
            } catch (e: IOException) {
                e.printStackTrace()
                val error = e.toString()
                Log.e("Error by Sender", error)
            } catch (e: Exception) {
                e.printStackTrace()
                val error = e.toString()
                Log.e("Error by Sender", error)
            } finally {
                // socket?.close()
            }
        }
    }

}
