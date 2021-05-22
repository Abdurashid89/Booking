package uz.koinot.stadion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.ActivityMainBinding
import uz.koinot.stadion.databinding.ActivitySplashBinding
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var storage: LocalStorage

    private var _bn: ActivitySplashBinding? = null
    private val bn get() = _bn!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(R.color.teal_200)
        window.navigationBarColor = resources.getColor(R.color.teal_200)
        _bn = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(bn.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        MainScope().launch {
            delay(1500)
            if (!storage.hasAccount) {
                startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}