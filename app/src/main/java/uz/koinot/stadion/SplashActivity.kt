package uz.koinot.stadion

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.ActivityMainBinding
import uz.koinot.stadion.databinding.ActivitySplashBinding
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var storage: LocalStorage

    private var _bn: ActivitySplashBinding? = null
    private val bn get() = _bn!!

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _bn = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(bn.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        lifecycleScope.launchWhenCreated {
            delay(1500)
            if (!storage.hasAccount) {
                startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}