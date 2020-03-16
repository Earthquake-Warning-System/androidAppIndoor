package com.example.udptest

import android.content.Intent
import android.util.Log
import com.example.tutorial.AddressBookProtos
import com.example.udptest.credibility.Credibility
import java.io.IOException
import java.net.*
import java.util.*
import java.text.SimpleDateFormat

class UdpSender(private val socket : DatagramSocket){

    private val bootstrapServerIp = "140.115.153.209"
    private val macAddress = Macaddress.getMacAddr()
    private val country = Locale.getDefault().displayCountry

    fun logTime(type:Int){
        val date = Date()
        val calendar =  Calendar.getInstance()
        calendar.setTime(date)
        val df =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        if (type == 0){ MainActivity.lastEv = df.format(calendar.time)
        }else if (type == 1){MainActivity.lastKp = df.format(calendar.time)}
        val intent = Intent("MyMessage")
        intent.putExtra("message", "log")
        Log.d("kp time:",df.format(calendar.time))
        MainActivity.broadcast?.sendBroadcast(intent)
    }

    fun bootAskSend(){
        val bootAsk = AddressBookProtos.Boot_ask.newBuilder()
        bootAsk.setPacketType("3")
        bootAsk.setVersion("1.0.0")
        bootAsk.setServerIp("0")
        bootAsk.setServerPort(0)
        val bootaskPack = bootAsk.build().toByteArray()
        println("bs ask")
        Sender(bootstrapServerIp, bootaskPack, 7777, bootaskPack.size, socket).start()
        Thread.sleep(100)
        for (i in 1..5) {
            if (MainActivity.serverIp == "" || MainActivity.serverPort == 0) {
                println("bs ask again")
               Sender(bootstrapServerIp, bootaskPack, 7777, bootaskPack.size, socket).start()
                Thread.sleep(100)
            }else if(i > 4){println("connect to server error")}
            else{break}
        }
        Thread.sleep(100)
        if(MainActivity.serverIp != "" || MainActivity.serverPort != 0){
            kpAckSend()

            kpAliveSend()
        }
    }
    fun kpAckSend(){
        Thread.sleep(5000)
        //connect to nearest server
        if (MainActivity.serverIp != "" || MainActivity.serverPort != 0) {
            val kpAck = AddressBookProtos.kp_alive_ack.newBuilder()
            kpAck.setPacketType("5")
            kpAck.setVersion("1.0.0")
            val kpAckPack = kpAck.build().toByteArray()
            println("cs ack")
            Sender(MainActivity.serverIp, kpAckPack, MainActivity.serverPort, kpAckPack.size, socket).start()
        }
    }
    fun kpAliveSend(){
        MainActivity.respond = 0
        val kpAlive = AddressBookProtos.kp_alive.newBuilder()
        kpAlive.setPacketType("0")
        kpAlive.setAliveFlag(1)
        kpAlive.setCityCode(country)
        kpAlive.setCountryCode(country)
        kpAlive.setSensorId(macAddress)
        val kpAlivePack = kpAlive.build().toByteArray()
        println("cs kp")
        Sender(MainActivity.serverIp, kpAlivePack, MainActivity.serverPort, kpAlivePack.size, socket).start()
        MainActivity.kpNum++
        logTime(1)

        for(i in 1..3){
            Thread.sleep(5000)
            if(MainActivity.respond == 0) {
                println("cs kp again")
                Sender(MainActivity.serverIp, kpAlivePack, MainActivity.serverPort, kpAlivePack.size, socket).start()
            }else{break}
        }
        if(MainActivity.respond == 0) {
            println("local server not respond, reconnect to bootstrap")
            MainActivity.serverIp = ""
            MainActivity.serverPort = 0
            UdpSender(socket).bootAskSend()
        }
    }
    fun eqEventSend(){
        val eqEvent = AddressBookProtos.EQ_event.newBuilder()
        val date = Date()
        val ts = date.getTime()
        val tsSec = (ts.toDouble() / 1000).toInt()
        val tsUsec = ts % 1000
        MainActivity.evNum++
        logTime(0)
        val credit = MainActivity.credit.dateCount(ts)
        Log.d("credit" , credit.toString() )
        eqEvent.setPacketType("1")
        eqEvent.setEventSec(tsSec.toLong())
        eqEvent.setEventOutput(credit)
        eqEvent.setEventUsec(tsUsec)
        eqEvent.setSensorId(macAddress)
        eqEvent.setVersion("1.0.0")
        val eqEventPack = eqEvent.build().toByteArray()
        println("eq send!!")
        Sender(MainActivity.serverIp, eqEventPack, MainActivity.serverPort, eqEventPack.size, socket).start()
        MainActivity.falseAlarm = true
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
