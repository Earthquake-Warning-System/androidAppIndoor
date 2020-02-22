package com.example.udptest.credibility

import android.content.SharedPreferences
import android.util.Log
import com.example.udptest.MainActivity

var valueOfReliable: Int = 100
var sharedPreferences: SharedPreferences? = null

class Credibility{

    fun dateCount(eqtime : Long):Int{

        if (MainActivity.falseAlarm){
            val eqT = sharedPreferences!!.getString("eqLast", "")

            Log.d("eqget",eqT)
            val Eqtime = eqtime
            Log.d("Eqtime",  eqtime.toString())
            if(eqT==""){
                sharedPreferences!!.edit().putString("eqLast", Eqtime.toString()).apply()
                return 100
            }
            val dif = Eqtime - eqT!!.toLong()
            Log.d("dif", (Eqtime - eqT!!.toLong()).toString())
            if (dif >= 75600000){ valueOfReliable = 100
            }else if(dif <= 75600000 && dif >= 64800000){valueOfReliable = 85
            }else if(dif <= 64800000 && dif >= 43200000){valueOfReliable = 60
            }else if(dif <= 43200000 && dif >= 32400000){valueOfReliable = 40
            }else if(dif <= 32400000 && dif >= 21600000){valueOfReliable = 25
            }else if(dif <= 21600000 && dif >= 10800000){valueOfReliable = 15
            }else if(dif <= 10800000 && dif >= 0){valueOfReliable = 10
            }else{valueOfReliable = 1
                println("credibility error")
            }

            sharedPreferences!!.edit().putString("eqLast", Eqtime.toString()).apply()
            Log.d("eqset",Eqtime.toString())

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