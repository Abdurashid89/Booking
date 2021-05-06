package uz.koinot.stadion.utils

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import retrofit2.HttpException

fun Fragment.showMessage(message:Any){
    Toast.makeText(requireContext(),message.toString(),Toast.LENGTH_SHORT).show()
}
fun FragmentActivity.showMessage(message:Any){
    Toast.makeText(this,message.toString(),Toast.LENGTH_SHORT).show()
}
fun Exception.userMessage(): String{
   return when(this){
        is HttpException ->{
            if(this.code() == 401) "Foydalanuvchi ro'yhatdan o'tmagan"
            else this.message()
        }
        else -> "Aniqlanmagan xatolik"
    }
}
fun Fragment.customLog(message: Any){
    Log.e("AAA",message.toString())
}
typealias SingleBlock<T> = (T) -> Unit