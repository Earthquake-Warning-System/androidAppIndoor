package com.example.udptest


import android.content.SharedPreferences
import android.util.Log

var sharedPreferences: SharedPreferences? = null

class TokenSetting{


    fun listToken():ArrayList<TokenData>{
        val allData = ArrayList<TokenData>(5)
        for (i in 0..3){
            val key = "Pair$i"
            val ids = sharedPreferences!!.getString(key , "")
            // var data = TokenData()
            if (ids!=""){
                val tokenArray = ids?.split(",")
                when {
                    tokenArray?.size == 2 -> allData.add(TokenData(i,tokenArray[0],tokenArray[1]))
                    tokenArray?.size == 3 -> allData.add(TokenData(i,tokenArray[0],tokenArray[1]))
                    else -> allData.add(TokenData(i,tokenArray!![0],""))
                }

            }else{
                allData.add(TokenData(-1,"",""))
            }
        }
        return allData
    }
    fun testToken(number : String?){
        val key = "Pair$number"
        Log.d("test token: ",key)
        val ids = sharedPreferences!!.getString(key, "")

        if(ids!=""){
            val tokenArray = ids?.split(",")
            FirebaseSender.pushFCMNotification(tokenArray!![0] , "TEST", "Hello")
        }
    }
    fun deleteToken(number : String?){
        val key = "Pair$number"
        Log.d("delete token: ",key)
        val ids = sharedPreferences!!.getString(key, "")
        if(ids!=""){
            sharedPreferences!!.edit().remove(key).apply()
        }
    }
    fun setShare(shared:SharedPreferences){
         sharedPreferences = shared
    }

    data class TokenData(val number : Int  ,val token:String? ,val name : String )
}