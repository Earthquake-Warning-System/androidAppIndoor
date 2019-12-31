package com.example.udptest

import android.util.Log
import org.junit.Test

import org.junit.Assert.*
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

var pre_i = 0
var pre_pre_i=0
var next_j=0

var pre_j=0
var pre_pre_j=0

val window = 50

val win2 = 25

val threshold = 0.003

var data : FloatArray = FloatArray(50)

var ave : FloatArray = FloatArray(50)

var vari : FloatArray = FloatArray(25)

var peak : FloatArray = FloatArray(25)

var score : FloatArray = FloatArray(25)

var i = 0
var j = 0
var k = 0

var avg = 0.0F
var vara = 0.0F //var
var sum = 0.0F

var avg_sum = 0.0F

var var_sum = 0.0F
@Test
fun addition_isCorrect(accz:Float):Float {
    val z_axis = accz.toFloat()
    Log.d("z value(modified)",  z_axis.toString())
    i = i % window
    j = j % win2

    pre_i = (i + window - 1) % window
    pre_pre_i = (i + window - 2) % window
    pre_j = (j + win2 - 1) % win2
    pre_pre_j = (j + win2 - 2) % win2
    next_j = (j + 1) % win2 //j=49,next_j=0
    data[i] = z_axis

    if (k == 0) { //i=0,aveage[i]= data[i];
        ave[pre_i] = data[i]
    }
    ave[i] = 7 / 8 * ave[pre_i] + data[i] / 8
    if (ave[pre_i] > ave[pre_pre_i] && ave[pre_i] > ave[i]) {
        peak[j] = ave[pre_i]
        //   LOGE("@peak[j]:%f",peak[j]);
    } else if (ave[pre_i] < ave[pre_pre_i] && ave[pre_i] < ave[i]) {
        peak[j] = ave[pre_i]
        //   LOGE("!peak[j]:%f",peak[j]);
    }
    // LOGE("i:%d j:%d data[i]%f ave[i]:%f ",i,j,data[i],ave[i]);
    i++
    if (k < window) { //window not full
        avg_sum += peak[j]
        // LOGE("aaa avg_sum=%f peak[j]=%f",avg_sum,peak[j]);
        avg = avg_sum / (k + 1)
        var_sum += (peak[j] - avg) * (peak[j] - avg)
        //  LOGE("aaa avg=%f peak[j]-avg=%f",avg,peak[j]-avg);
        vara = var_sum / (k + 1)
        // LOGE("aaa var=%f var_sum/(k+1)=%f",var,var_sum/(k+1));
        vari[j] = (peak[j] - avg) * (peak[j] - avg)
        score[j] = vara
        sum += score[j]
        j++
        if (sum /( k + 1 )> threshold) {
            //EQ!!
            Log.d("sum=", sum.toString())
            Log.d( "k=", (k + 1).toString())
            return sum / (++k)
        } else {
            k++
            return -1.0F
        }
    } else {
        avg_sum -= peak[next_j]
        var_sum -= vari[next_j]
        sum -= score[next_j]

        avg_sum += peak[j]
        avg = avg_sum / win2
        var_sum += (peak[j] - avg) * (peak[j] - avg)
        vara = var_sum / win2

        vari[j] = (peak[j] - avg) * (peak[j] - avg)
        score[j] = vara
        sum += score[j]

        /*if((sum/win2)>threshold){
    //EQ!!!
    return sum/win2;
}*/
        if (vari[j] > 0.012) {
            //EQ!!!
            return vari[j]
        }
        j++
    }
    return vari[j-1]
}
}
