package uz.koinot.stadion.utils

import android.util.Log
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

fun main(){
   val d =  "2021-05-30T18:40:56.316+05:00"
//    2021-05-30 19:46:57.678
//   2021-05-30 18:40:56.316

   println(d.replace("T"," ").substring(0,d.length-6))
//   print()

}
