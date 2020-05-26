package com.example.udptest.credibility

import android.content.SharedPreferences
import android.util.Log
import com.example.udptest.Singleton.falseAlarm
import com.example.udptest.Singleton.serverIp
import com.example.udptest.Singleton.serverPort

var valueOfReliable: Int = 100
var sharedPreferences: SharedPreferences? = null

class Credibility{

    fun dateCount(eqTime : Long):Int{

        if (falseAlarm){
            val eqT = sharedPreferences!!.getString("eqLast", "")

            Log.d("eqget",eqT)
            Log.d("Eqtime",  eqTime.toString())
            if(eqT==""){
                sharedPreferences!!.edit().putString("eqLast", eqTime.toString()).apply()
                return 100
            }

            if(serverIp != "" || serverPort != 0) {
                val dif = eqTime - eqT!!.toLong()
                Log.d("dif", (eqTime - eqT!!.toLong()).toString())

                //for demo test

                when {
                    dif >= 75600000 -> {
                        valueOfReliable = 100
                    }
                    dif in 64800000..75600000 -> {
                        valueOfReliable = 85
                    }
                    dif in 43200000..64800000 -> {
                        valueOfReliable = 60
                    }
                    dif in 32400000..43200000 -> {
                        valueOfReliable = 40
                    }
                    dif in 21600000..32400000 -> {
                        valueOfReliable = 25
                    }
                    dif in 10800000..21600000 -> {
                        valueOfReliable = 15
                    }
                    dif in 0..10800000 -> {
                        valueOfReliable = 10
                    }
                    else -> {
                        valueOfReliable = 1
                        println("credibility error")
                    }
                }
                sharedPreferences!!.edit().putString("eqLast", eqTime.toString()).apply()
                Log.d("eqset", eqTime.toString())
            }
            return  valueOfReliable
        }else{
            valueOfReliable =100
            return  valueOfReliable
        }
    }

    fun setShare(shared:SharedPreferences){
        sharedPreferences = shared
    }
}