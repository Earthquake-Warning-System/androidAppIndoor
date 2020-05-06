package com.example.udptest

import android.content.Intent
import android.util.Log
import com.example.udptest.Singleton.broadcast

class Calculation(private val listener: OnResultListener, val name:String) {

    interface OnResultListener {
        fun onOccur(x:Double)
    }

    private val countOfAccZDiff = 300
    private val thresholdFactor = 0.012
    private val windowSizesForAvgAccZ = 50
    private val windowSizesForTurningPoint = 25

    var accZDiffSample = 0
    var averageAccZDiff = 0.0
    var averageAccZWindow = CircularArray<Double>(windowSizesForAvgAccZ)

    var winOfCurrent = 0.0
    var winOfLast1st = 0.0
    var winOfLast2nd = 0.0

    var turningPointWindow = CircularArray<Double>(windowSizesForTurningPoint)
    var turningPointOfCurrent = 0.0
    var sumOfTurningPoint = 0.0
    var indexOfOldestTurningPoint = 0

    var eqThreshold = 0.0

    val largest = 45.0 //detect hand move threshold

    init {
        eqThreshold = Math.sqrt(thresholdFactor)
    }

    fun computing(oriAccZ: Float) {
        val accZDiff = oriAccZ - 9.80665

        if (isCorrected()) {
            if (averageAccZWindow.size == 0) {
                averageAccZWindow.add(accZDiff - averageAccZDiff)
            } else {
                averageAccZWindow.add((7.0 / 8) * averageAccZWindow[averageAccZWindow.currentIndex] + (1.0 / 8) * (accZDiff - averageAccZDiff))
            }

            if (isTurningPoint()) {
                turningPointOfCurrent = winOfLast1st
                turningPointWindow.add(turningPointOfCurrent)
                sumOfTurningPoint += turningPointOfCurrent
                if (isCircularFull()) {
                    val estimatedValue = getEstimatedValue()
                    if(estimatedValue> largest){
                        val intent = Intent("MyMessage") // "MyMessage" 為自定義的 Intent action 名稱
                        intent.putExtra("message", "not_eq")
                        broadcast?.sendBroadcast(intent)
                    }else if(isEqOccur(estimatedValue)) {
                        Log.d("detect name",name)
                        listener.onOccur(estimatedValue)
                    }
                    indexOfOldestTurningPoint = turningPointWindow.checkAndGetPositionIndex(-windowSizesForTurningPoint + 1)
                    sumOfTurningPoint -= turningPointWindow[indexOfOldestTurningPoint]
                }
            }
        } else {
            calculateAccZDiffFromGravity(accZDiff)
        }
    }

    private fun isCorrected() = accZDiffSample == countOfAccZDiff

    private fun calculateAccZDiffFromGravity(currAccZDiff: Double) {
        when (++accZDiffSample) {
            1 -> {averageAccZDiff = currAccZDiff
                val intent = Intent("MyMessage")
                intent.putExtra("message", "correction")
                broadcast?.sendBroadcast(intent)
            }
            in 2..countOfAccZDiff -> {averageAccZDiff = averageAccZDiff / 2 + currAccZDiff / 2
                if(accZDiffSample == 295){
                    val intent = Intent("MyMessage")
                    intent.putExtra("message", "normal")
                    broadcast?.sendBroadcast(intent)
                }
            }
        }
    }

    private fun isTurningPoint(): Boolean {
        if (averageAccZWindow.size < 3) {
            return false
        }
        winOfCurrent = averageAccZWindow[averageAccZWindow.currentIndex]
        winOfLast1st = averageAccZWindow[averageAccZWindow.checkAndGetPositionIndex(-1)]
        winOfLast2nd = averageAccZWindow[averageAccZWindow.checkAndGetPositionIndex(-2)]
        return isLargerPoint() || isSmallerPoint()
    }

    private fun isLargerPoint() = winOfLast1st > winOfCurrent && winOfLast1st > winOfLast2nd

    private fun isSmallerPoint() = winOfLast1st < winOfCurrent && winOfLast1st < winOfLast2nd

    private fun isCircularFull() = turningPointWindow.size == windowSizesForTurningPoint

    fun getEstimatedValue() = Math.abs(sumOfTurningPoint - windowSizesForTurningPoint * turningPointOfCurrent)

    private fun isEqOccur(estimatedValue: Double) = estimatedValue > windowSizesForTurningPoint * eqThreshold
}