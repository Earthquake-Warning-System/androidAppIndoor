package com.example.udptest

import com.example.udptest.Singleton.mediaPlayer
import com.example.udptest.Singleton.ringStatus


class Bell{
    fun ring(vol:Float,ringTime:Long){

        if (mediaPlayer?.isPlaying==false){
            Thread(Runnable {
                mediaPlayer?.setVolume(vol, vol)
                mediaPlayer?.start()
                Thread.sleep(ringTime)
                mediaPlayer?.pause()
                mediaPlayer?.seekTo(0)
                println("shake ring work sucess")
                if(ringStatus == 1){
                    mediaPlayer?.setVolume(1.0F, 1.0F)
                    mediaPlayer?.start()
                    Thread.sleep(30000)
                    mediaPlayer?.pause()
                    mediaPlayer?.seekTo(0)
                    println("eq ring work sucess")
                    ringStatus = 0
                }
            }).start()
        }
    }
}