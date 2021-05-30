package uz.koinot.stadion.utils

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException

fun Fragment.showMessage(message:Any){
    Toast.makeText(requireContext(),message.toString(),Toast.LENGTH_SHORT).show()
}
fun FragmentActivity.showMessage(message:Any){
    Toast.makeText(this,message.toString(),Toast.LENGTH_SHORT).show()
}
fun Fragment.customLog(message: Any){
    Log.e("AAA",message.toString())
}

fun String.toNeedDate():String{
    return  this.replace("T"," ").substring(0,this.length-6)
}

fun Int.getString():String{
    return if (this.toString().length == 1) "0$this" else this.toString()
}
fun String.getTimeStamp():String{
    return "2021-05-16T$this:15.050000"
}

fun Throwable.userMessage() = when (this) {
    is ConnectException -> "Не интернет"
    is HttpException -> when (this.code()) {
        304 -> "Not Modified"
        400 -> "Bad Request"
        401 -> "Unauthorized"
        403 -> "Forbidden"
        404 -> "Not Found"
        405 -> "Method Not Allowed"
        409 -> "Драйвер не найден"
        422 -> "Unprocessable"
        500 -> "Server Error"
        else -> "Something went wrong"
    }
    is IOException -> "Network error"
    else -> "Unknown error"
}
typealias SingleBlock<T> = (T) -> Unit