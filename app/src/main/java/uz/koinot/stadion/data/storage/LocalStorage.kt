package uz.koinot.stadion.data.storage

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import uz.koinot.stadion.utils.CONSTANTS
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStorage @Inject constructor(@ApplicationContext context: Context){

    private var sharedPref = context.getSharedPreferences("stadium",Context.MODE_PRIVATE)

    var accessToken:String
    set(value) = sharedPref.edit().putString(CONSTANTS.TOKEN,value).apply()
    get() = sharedPref.getString(CONSTANTS.TOKEN,"")!!

    var firebaseToken:String
        set(value) = sharedPref.edit().putString(CONSTANTS.FIREBASE_TOKEN,value).apply()
        get() = sharedPref.getString(CONSTANTS.FIREBASE_TOKEN,"")!!

    var hasAccount:Boolean
    set(value) = sharedPref.edit().putBoolean(CONSTANTS.HAS_ACCOUNT,value).apply()
    get() = sharedPref.getBoolean(CONSTANTS.HAS_ACCOUNT,false)
}