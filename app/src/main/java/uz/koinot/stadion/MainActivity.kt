package uz.koinot.stadion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import uz.koinot.stadion.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

//    7 277 22 71

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

        Firebase.messaging.subscribeToTopic("koinot2").addOnCompleteListener {task->
            Log.e("AAA","subscribe: ${task.isSuccessful}")
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