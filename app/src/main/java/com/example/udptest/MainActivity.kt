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
import android.view.KeyEvent
import android.view.Menu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.udptest.credibility.Credibility
import com.google.zxing.integration.android.IntentIntegrator
import java.util.*

class MainActivity : AppCompatActivity() {


    object Singleton{
        var socket = DatagramSocket(8080)
    }

    companion object {
        var serverIp = ""
        var serverPort = 0
        var falseAlarm = true
        var mediaPlayer: MediaPlayer? = null
        //var socket = DatagramSocket(8080)
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
    }
    var textContent :String? = null

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


    private var lv: ListView? = null
    private var customAdapter: CustomAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearLayout1.visibility = View.INVISIBLE
        linearLayout3.visibility = View.INVISIBLE
        linearLayout6.visibility = View.INVISIBLE
        LocalBroadcastManager.getInstance(this).registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val message = intent!!.getStringExtra("message")
                    if (message == "detect_shake") {
                        textContent = "detected shaking"
                        layoutStatus = 1
                        detectstatus.text = textContent
                        QRiv!!.setImageResource(R.drawable.shake)
                    }else if (message == "eq") {
                        textContent = "Earthquake occur!!"
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
                        textContent = "correcting, don't move"
                        detectstatus.text = textContent
                        QRiv!!.setImageBitmap(null)
                    }else if (message == "log") {
                        textContent = "kp num :$kpNum\n$lastKp\nevent num :$evNum\n$lastEv"
                        debuglog.text = textContent
                        QRiv!!.setImageBitmap(null)
                        println("refresh UI finish :　"+ Date())
                    }else if (message == "not_eq") {
                        switch1.isChecked = false
                    }
            }
        }, IntentFilter("MyMessage"))
        broadcast= LocalBroadcastManager.getInstance(this)
        //setLayout
        setView()
        tokenSet.setShare(this.getSharedPreferences("DATA", 0))
        credit.setShare(this.getSharedPreferences("DATA", 0))

        lv = findViewById<ListView>(R.id.lv)
        // modelArrayList = model
        setArray()
        //if(customAdapter != null)
        customAdapter = CustomAdapter(this, modelArrayList )
        lv!!.adapter = customAdapter

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
        //UdpServer(this, socket).start()
        //connecting to country server

        val intent =  Intent(this,UdpService::class.java)
        startService(intent)
        /*Thread(Runnable {
            UdpSender(socket).bootAskSend()
            KpAlive().randomtime(150)

            /*Timer().schedule(timerTask {

            *//*Timer().schedule(timerTask {

                val date = Date()
                val calendar =  Calendar.getInstance()
                calendar.setTime(date)
                val df =  SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                Log.d("kp start time", df.format(calendar.time))
                if(serverIp != "" || serverPort != 0) {
                    println("check ip port in main : "+ Date())
                    //kpalive with server
                    UdpSender(socket).kpAliveSend()
                    println("kp finish : "+ Date())
                }else{

                    println("try to reconnect to cs : "+ Date())
                    UdpSender(socket).bootAskSend()
                    println("reconnect finish : "+ Date())
                }
            },300000, 300000)*/
        }).start()
        Thread(Runnable {

                println("try to reconnect to cs : "+ Date())
                UdpSender(socket).bootAskSend()
                println("reconnect finish : "+ Date())
            }
            },300000, 300000)*//*
        }).start()*/
        /*Thread(Runnable {

            Timer().schedule(timerTask {
                //println("ack start"+ Date())
                if(serverIp != "" || serverPort != 0) {
                    //println("check ip port in Main")
                    //kpalive with server
                    UdpSender(socket).kpAckSend()
                    //println("ack finish"+ Date())
                    println("simple ack")
                }
            },30000, 30000)
        }).start()*/



    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if ((keyCode == KeyEvent.KEYCODE_BACK)&&linearLayout1.visibility != View.VISIBLE) {
            linearLayout1.visibility = View.VISIBLE
            linearLayout2.visibility = View.INVISIBLE
            linearLayout3.visibility = View.INVISIBLE
           // linearLayout4.visibility = View.INVISIBLE
            linearLayout6.visibility = View.INVISIBLE
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        switch1.text = "detection"
        switch1.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { btnView, isChecked ->
            xValue = (x.absoluteValue - 9.80665).absoluteValue
            yValue = (y.absoluteValue - 9.80665).absoluteValue
            zValue = (z.absoluteValue - 9.80665).absoluteValue
            val intent2 =  Intent(this,DetectionService::class.java)
            if (isChecked) {
                println(xValue)
                if(xValue<0.3||yValue<0.3||zValue<0.3){

                    if(detect == null){

                        startService(intent2)
                        textContent = "start detecting"
                        detectstatus.text = textContent
                    }
                }else{
                    textContent = "put the phone horizontally\n"
                    detectstatus.text = textContent
                    switch1.isChecked = false
                }
            } else {

                if(detect!=null){
                    detect?.turnOff()
                    stopService(intent2)
                    textContent = "end detecting"
                    println("end")
                    detect = null
                    detectstatus.text = textContent
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
                println(result.contents.toString())
                val tool = Tool(this)
                val tokenArray = result.contents.split(",")
                if(tokenArray.size==2){
                    if(tokenArray[0]!=""&&tokenArray[1]!=""){
                        // wait for check safe
                        Log.d("token", tokenArray[0])
                        Log.d("Device name", tokenArray[1])

                        tool.addID(result.contents)
                        Thread(Runnable {
                            FirebaseSender.pushFCMNotification(tokenArray[0], "Pair", "配對成功")
                        }).start()
                    }
                }
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


    private fun setArray(){
        val list = ArrayList<Model>()
        val msg  = tokenSet.listToken()
            for (i in 0..3) {
                println(i.toString()+msg[i].number)
                if(msg[i].number!=-1) {
                    val model = Model()
                    model.setNumbers(msg[i].number)
                    model.setTokens(msg[i].name)
                    list.add(model)
                }
            }
            modelArrayList = list
    }
        // thread problem


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
            linearLayout6.visibility = View.INVISIBLE
        }
        next1!!.setOnClickListener {
            linearLayout1.visibility = View.INVISIBLE
            linearLayout2.visibility = View.INVISIBLE
            linearLayout3.visibility = View.VISIBLE
            linearLayout6.visibility = View.INVISIBLE
        }
        next2!!.setOnClickListener {
            linearLayout1.visibility = View.VISIBLE
            linearLayout2.visibility = View.INVISIBLE
            linearLayout3.visibility = View.INVISIBLE
            linearLayout6.visibility = View.INVISIBLE
        }
        pair_setting!!.setOnClickListener {
            linearLayout1.visibility = View.INVISIBLE
            linearLayout2.visibility = View.INVISIBLE
            linearLayout3.visibility = View.INVISIBLE
            linearLayout6.visibility = View.VISIBLE
            customAdapter!!.refreshView()
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




    }

}
