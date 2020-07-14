package com.example.udptest

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.tutorial.AddressBookProtos
import com.example.udptest.Singleton.bell
import com.example.udptest.Singleton.broadcast
import com.example.udptest.Singleton.falseAlarm
import com.example.udptest.Singleton.respond
import com.example.udptest.Singleton.ringStatus
import com.example.udptest.Singleton.serverIp
import com.example.udptest.Singleton.serverPort
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket

class UdpServer(private val context: Context, socket: DatagramSocket) : Thread() {
    private var server: DatagramSocket? = null

    init {
        server = socket
        Log.d("User", "set server socket")
    }

    override fun run() {
        val byte1024 = ByteArray(1024)
        val dPacket = DatagramPacket(byte1024, byte1024.size) //val??
        try {
            while (true) {
                server!!.receive(dPacket)
                while (true) {
                    val buf = byte1024.slice(IntRange(0, dPacket.length - 1)).toByteArray()
                    val type = AddressBookProtos.packet_type.parseFrom(buf)
                    println("------udp server state------")
                    when (type.packetType) {
                        "0" -> {//receive server respond
                            println("Type0")
                            println("receive server kp respond")
                            respond = 1
                        }
                        "2" -> {//eq occur
                            println("Type2")
                            println("earthquake occur!!")
                            ringStatus = 1
                            bell.ring(1.0F,30000)
                            falseAlarm = false
                            FcmPush(context).pushFCMmessage("shake", "shake!")
                            val intent = Intent("MyMessage")
                            intent.putExtra("message", "eq")
                            broadcast?.sendBroadcast(intent)
                            sleep(5000)
                        }
                        "3" -> {//get local server ip and port
                            val data3 = AddressBookProtos.Boot_ask.parseFrom(buf)
                            println("Type3")
                            println("get local server ip and port")


                            serverIp = data3.serverIp
                            serverPort = data3.serverPort

                            Log.d("server ip:", serverIp)
                            Log.d("server port:", serverPort.toString())
                        }
                        "5" -> {//local server ack
                            println("Type5")
                            //println("connecting to local server")
                        }
                    }
                    break
                }
                //CloseSocket(client)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

