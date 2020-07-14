package com.example.udptest

import android.media.MediaPlayer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.udptest.credibility.Credibility
import java.net.DatagramSocket
import java.util.ArrayList

object Singleton {
    var serverIp = ""
    var serverPort = 0
    var falseAlarm = true
    var mediaPlayer: MediaPlayer? = null
    val bell = Bell()
    var x = 0.0F
    var y = 0.0F
    var z = 0.0F
    var respond = 0
    var timeCounter = 0
    var ringStatus = 0
    var broadcast : LocalBroadcastManager? = null
    var kpNum = 0
    var evNum = 0
    var lastKp = ""
    var lastEv = ""
    val credit = Credibility()
    var tokenSet = TokenSetting()
    var modelArrayList: ArrayList<Model>? = null
    var detect : SetDetect? = null
    var socket = DatagramSocket()
    var onCreateNum = 0
}