package com.example.udptest

class Model {
    var number: Int = 0
    var token : String? = null

    fun getNumbers(): Int {
        return number
    }

    fun setNumbers(number: Int) {
        this.number = number
    }

    fun getTokens(): String? {
        return token.toString()
    }

    fun setTokens(token: String) {
        this.token = token
    }
}