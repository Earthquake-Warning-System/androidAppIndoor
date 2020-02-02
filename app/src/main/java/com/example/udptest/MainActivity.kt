package com.example.udptest

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import java.net.DatagramSocket
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.next2
import kotlin.math.absoluteValue
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.view.Menu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.integration.android.IntentIntegrator


class MainActivity : AppCompatActivity() {

    companion object {
        var serverIp = ""
        var serverPort = 0
        var falseAlarm = true
        var mediaPlayer: MediaPlayer? = null
        var socket = DatagramSocket(8080)
        val bell = Bell()
        var x = 0.0F
        var y = 0.0F
        var z = 0.0F
        var respond = 0
        var timeCounter = 0
        var ringStatus = 0
        var broadcast : LocalBroadcastManager? = null
        var eqLast : Long = 0
        var kpNum = 0
        var evNum = 0
        var lastKp = ""
        var lastEv = ""
    }
    var textContent :String? = null
    private var detect : SetDetect? = null
    private var layoutStatus = 0
    //Sensor
    lateinit var sensorManager: SensorManager
    var sensor: Sensor? = null
    var xValue : Double = 0.0
    var yValue : Double = 0.0
    var zValue : Double = 0.0

    private val eventListener = object : SensorEventListener {
        // 當感測器的 精確度改變時，就會觸發
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}
        // 當感測器的 測量值改變時，就會觸發
        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null) Log.d("sensor", "事件為空")
            when (event!!.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    x = event.values[0]
                    y = event.values[1]
                    z = event.values[2]
                }
                else -> {
                    Log.d("sensor", "未知的感測器觸發")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayout1.visibility = View.INVISIBLE
        linearLayout3.visibility = View.INVISIBLE
        linearLayout4.visibility = View.INVISIBLE
        LocalBroadcastManager.getInstance(this).registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val message = intent!!.getStringExtra("message")
                    if (message == "detect_shake") {
                        textContent = "偵測到震動"
                        layoutStatus = 1
                        detectstatus.text = textContent
                        QRiv!!.setImageResource(R.drawable.shake)
                    }else if (message == "eq") {
                        textContent = "發生地震"
                        detectstatus.text = textContent
                        layoutStatus = 1
                        QRiv!!.setImageResource(R.drawable.eq)
                    }else if (message == "normal") {
                        println("normal")
                        textContent = "start detecting"
                        detectstatus.text = textContent
                        QRiv!!.setImageBitmap(null)
                    }else if (message == "correction") {
                        println("校正中")
                        textContent = "校正中，請勿移動手機"
                        detectstatus.text = textContent
                        QRiv!!.setImageBitmap(null)
                    }else if (message == "log") {
                        textContent = "kp num :" + kpNum +"\n" + lastKp +"\n"+ "event num :" + evNum +"\n"+ lastEv
                        debuglog.text = textContent
                        QRiv!!.setImageBitmap(null)
                    }else if (message == "not_eq") {
                        switch1.isChecked = false
                    }
            }
        }, IntentFilter("MyMessage"))
        broadcast= LocalBroadcastManager.getInstance(this)
        //setLayout
        setView()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            println("ask permission")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),1 )
        }
        //Sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(eventListener, sensor, SensorManager.SENSOR_DELAY_FASTEST)
        //set ring
        mediaPlayer = MediaPlayer.create(this, R.raw.eq_warning_sound)
        mediaPlayer?.setOnPreparedListener { println("sound ready") }
        //start udp server
        UdpServer(this, socket).start()
        //connecting to country server
        Thread(Runnable {
            UdpSender(socket).bootAskSend()
            KpAlive().randomtime(150)
        }).start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        switch1.text = "偵測"
        switch1.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { btnView, isChecked ->
            xValue = (x.absoluteValue - 9.80665).absoluteValue
            yValue = (y.absoluteValue - 9.80665).absoluteValue
            zValue = (z.absoluteValue - 9.80665).absoluteValue
            if (isChecked) {
                println(xValue)
                if(xValue<0.3||yValue<0.3||zValue<0.3){
                    switch1.text = "偵測"
                    if(detect == null){
                        detect = SetDetect()
                        detect?.turnOn()
                        textContent = "start detecting"
                        println("start")
                        detectstatus.setText(textContent)
                    }
                }else{
                    textContent = "請擺正手機"
                    detectstatus.setText(textContent)
                    switch1.isChecked = false
                }
            } else {
                switch1.text = "偵測"
                if(detect!=null){
                    detect?.turnOff()
                    textContent = "end detecting"
                    println("end")
                    detect = null
                    detectstatus.setText(textContent)
                }
            }
        })
        return super.onCreateOptionsMenu(menu)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) run { Toast.makeText(this, "Scan failed!", Toast.LENGTH_LONG).show() }
            else {
                println(result.getContents().toString())
                val tool = Tool(this)
                tool.addID(result.getContents().toString())
                Thread(Runnable {
                    FirebaseSender.pushFCMNotification(result.getContents().toString(), "Pair", "配對成功")
                }).start()
            }
        }
    }
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


    private fun setView(){
        val constrantview : ConstraintLayout = linearLayout1
        constrantview.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                if(layoutStatus == 1){
                    QRiv!!.setImageBitmap(null)
                    layoutStatus = 0
                }
                return true
            }
        } )
        howto!!.setOnClickListener {
            linearLayout1.visibility = View.INVISIBLE
            linearLayout2.visibility = View.VISIBLE
            linearLayout3.visibility = View.INVISIBLE
            linearLayout4.visibility = View.INVISIBLE
        }
        next1!!.setOnClickListener {
            linearLayout1.visibility = View.INVISIBLE
            linearLayout2.visibility = View.INVISIBLE
            linearLayout3.visibility = View.VISIBLE
            linearLayout4.visibility = View.INVISIBLE
        }
        next2!!.setOnClickListener {
            linearLayout1.visibility = View.VISIBLE
            linearLayout2.visibility = View.INVISIBLE
            linearLayout3.visibility = View.INVISIBLE
            linearLayout4.visibility = View.INVISIBLE
        }
        pair_setting!!.setOnClickListener {
            linearLayout1.visibility = View.INVISIBLE
            linearLayout2.visibility = View.INVISIBLE
            linearLayout3.visibility = View.INVISIBLE
            linearLayout4.visibility = View.VISIBLE
            textView4.text = null
            Thread(Runnable {
                val tokenText = TokenSetting(this).listToken()
                runOnUiThread {
                    textView4.text = tokenText
                }
            }).start()
        }

        camera!!.setOnClickListener {
            val thread = Thread(Runnable {
                try {
                    val intentIntegrator = IntentIntegrator(this@MainActivity)
                    intentIntegrator.setCameraId(0)
                    intentIntegrator.initiateScan()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            thread.start()
        }
        go_back!!.setOnClickListener{
            linearLayout1.visibility = View.VISIBLE
            linearLayout2.visibility = View.INVISIBLE
            linearLayout3.visibility = View.INVISIBLE
            linearLayout4.visibility = View.INVISIBLE
        }
        test_not!!.setOnClickListener{
            if(input!!.text!= null){
                Thread(Runnable {
                    TokenSetting(this).testToken(input!!.text.toString())
                }).start()
            }
            input!!.text = null
        }
        token_del!!.setOnClickListener{
            Thread(Runnable {
                if(input!!.text!= null){
                    TokenSetting(this).deleteToken(input!!.text.toString())
                    Log.d("clear","success")
                    val tokenText = TokenSetting(this).listToken()
                    Log.d("after delete",tokenText )
                    runOnUiThread {
                        textView4.text = tokenText
                    }
                }
            }).start()
            input!!.text = null
        }




    }
}
