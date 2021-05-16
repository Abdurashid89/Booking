package uz.koinot.stadion.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.fragment.app.Fragment
import java.util.*


@SuppressLint("LongLogTag")
fun Activity.getCompleteAddressString(
    LATITUDE: Double,
    LONGITUDE: Double
): String? {
    var strAdd = ""
    val geocoder = Geocoder(this, Locale.getDefault())
    try {
        val addresses: List<Address>? =
            geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
        if (addresses != null) {
            val returnedAddress: Address = addresses[0]
            val strReturnedAddress = StringBuilder("")
            for (i in 0..returnedAddress.getMaxAddressLineIndex()) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
            }
            strAdd = strReturnedAddress.toString()
            Log.w("My Current loction address", strReturnedAddress.toString())
        } else {
            Log.w("My Current loction address", "No Address returned!")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Log.w("My Current loction address", "Canont get Address!")
    }
    return strAdd
}


@SuppressLint("LongLogTag")
fun Fragment.getCompleteAddressString(
    LATITUDE: Double,
    LONGITUDE: Double
): String? {
    var strAdd = ""
    val geocoder = Geocoder(requireContext(), Locale.getDefault())
    try {
        val addresses: List<Address>? =
            geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
        if (addresses != null) {
            val returnedAddress: Address = addresses[0]
            val strReturnedAddress = StringBuilder("")
            for (i in 0..returnedAddress.getMaxAddressLineIndex()) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
            }
            strAdd = strReturnedAddress.toString()
            Log.w("My Current loction address", strReturnedAddress.toString())
        } else {
            Log.w("My Current loction address", "No Address returned!")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Log.w("My Current loction address", "Canont get Address!")
    }
    return strAdd
}