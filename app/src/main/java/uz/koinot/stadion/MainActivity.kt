package uz.koinot.stadion

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import uz.koinot.stadion.data.api.ApiService
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.ActivityMainBinding
import uz.koinot.stadion.utils.CONSTANTS
import uz.koinot.stadion.utils.Utils
import uz.koinot.stadion.utils.showMessage
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    @Inject
    lateinit var api:ApiService

    @Inject
    lateinit var storage: LocalStorage

    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private var _bn: ActivityMainBinding? = null
    private val bn get() = _bn!!
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bn = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bn.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        navController = findNavController(R.id.nav_host_fragment_container)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(true)

        Firebase.messaging.subscribeToTopic("koinot").addOnCompleteListener {task->
            Log.e("AAA","subscribe: ${task.isSuccessful}")
//            Log.e("AAA","subscribe: ${task.result}")
        }
        Firebase.messaging.isAutoInitEnabled = true

        if(!storage.firebaseToken.isEmpty()){
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener{task ->
                if(!task.isSuccessful) return@OnCompleteListener

                storage.firebaseToken = task.result.toString()

                lifecycleScope.launchWhenCreated {
                    try {
                        val res = api.token(task.result.toString())
//                        if(res.success == 200){
////                            showMessage(res.message)
//                        }else{
////                            showMessage(
////                                res.message
////                            )
//                        }
                    }catch (e:Exception){
                        showMessage(e.localizedMessage)
                        e.printStackTrace()
                    }
                }
                Log.e("AAA","token is: "+task.result.toString())
            })
        }


        val text = intent.getStringExtra("koinot")
        val id = intent.getIntExtra("id",0)
        if(text == "main"){
            navController.navigate(R.id.pagerFragment, bundleOf(CONSTANTS.STADION to Gson().toJson(Stadium("stadium",id,0,0,
                emptyList()))),
                Utils.navOptions())
        }


//        navController.addOnDestinationChangedListener { controller, destination, arguments ->
//            when(destination.id){
//                R.id.homeFragment -> bn.bottomNav.selectedItemId = R.id.homeFragment
//                R.id.activeOrderFragment -> bn.bottomNav.selectedItemId = R.id.activeOrderFragment
//                R.id.archiveOrderkFragment -> bn.bottomNav.selectedItemId = R.id.archiveOrderkFragment
//            }
//        }


//        bn.bottomNav.setOnNavigationItemSelectedListener{
//            when(it.itemId){
//                R.id.homeFragment ->{
//                    if (navController.currentDestination?.id != R.id.homeFragment)
//                    navController.navigate(R.id.homeFragment)
//                }
//                R.id.activeOrderFragment ->{
//                    if (navController.currentDestination?.id != R.id.activeOrderFragment)
//                    navController.navigate(R.id.activeOrderFragment)
//                }
//                R.id.archiveOrderkFragment ->{
//                    if (navController.currentDestination?.id != R.id.archiveOrderkFragment)
//                    navController.navigate(R.id.archiveOrderkFragment)
//                }
//               else ->{
//                   navController.navigate(R.id.homeFragment)
//               }
//            }
//           false
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
    
}