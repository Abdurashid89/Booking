package uz.koinot.stadion.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import uz.koinot.stadion.BaseFragment
import uz.koinot.stadion.MainActivity
import uz.koinot.stadion.R
import uz.koinot.stadion.data.api.ApiService
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentVerificationBinding
import uz.koinot.stadion.utils.showMessage
import javax.inject.Inject

@AndroidEntryPoint
class VerificationFragment : Fragment(R.layout.fragment_verification) {

    @Inject
    lateinit var api: ApiService

    @Inject
    lateinit var storage: LocalStorage

    private var time = 0L


    private var _bn: FragmentVerificationBinding? = null
    val bn get() = _bn!!


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentVerificationBinding.bind(view)

        bn.btnVerification.setOnClickListener {
            val number = bn.inputVerificationNumber.text.toString().trim()
            if (number.isNotEmpty()) {
                lifecycleScope.launchWhenCreated {
                    try {
                        showProgress(true)
                        val res = api.verify(number)

                        if (res.success == 200) {
                            showProgress(false)
                            showMessage(getString(R.string.success_register))
                            storage.hasAccount = true
                            storage.firebaseToken = ""
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        } else {
                            showProgress(false)
                            showMessage(getString(R.string.error))
                        }


                    } catch (e: Exception) {
                        showProgress(false)
                        showMessage(getString(R.string.error))

                    }
                }

            }
        }
        bn.sentAgain.setOnClickListener {
            if (time + 60000 < System.currentTimeMillis()) {
                showProgress(true)
                lifecycleScope.launchWhenCreated {
                    try {
                     val res = api.recode()
                        showProgress(false)
                        if(res.success == 200){
                            showMessage("Мы отправили проверочный код")
                        }else{
                            showMessage(getString(R.string.error))
                        }

                    } catch (e: Exception) {
                        showProgress(false)
                        showMessage(getString(R.string.error))
                    }
                }
            }
            time = System.currentTimeMillis()
        }
    }

    private fun showProgress(status:Boolean){
        bn.progressBar.isVisible = status
    }
    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}