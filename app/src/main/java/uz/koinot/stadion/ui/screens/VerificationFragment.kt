package uz.koinot.stadion.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
import uz.koinot.stadion.utils.Utils
import uz.koinot.stadion.utils.showMessage
import javax.inject.Inject

@AndroidEntryPoint
class VerificationFragment : Fragment(R.layout.fragment_verification) {

    @Inject
    lateinit var api: ApiService

    @Inject
    lateinit var storage: LocalStorage

    private var _bn: FragmentVerificationBinding? = null
    val bn get() = _bn!!


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentVerificationBinding.bind(view)

        startTimer()
        bn.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        bn.tryAgainText.setOnClickListener {
            bn.tryAgainText.visibility = View.GONE
           startTimer()


            showProgress(true)
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
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
        bn.inputVerificationNumber.addTextChangedListener(textWatcherName)
        bn.btnVerification.setOnClickListener {
            btnClick()
        }

    }

    private fun btnClick() {
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
                        requireActivity().finishAffinity()
                        requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
                    } else {
                        showProgress(false)
                        showMessage(getString(R.string.error))
                    }


                } catch (e: Exception) {
                    showProgress(false)
                    showMessage(getString(R.string.error))

                }
            }

        }else{
            showMessage("error")
        }
    }




    private fun startTimer() {
        bn.countDownTimerLinear.visibility = View.VISIBLE
        bn.coutdownView.start(60000)
        bn.coutdownView.setOnCountdownEndListener {
            bn.tryAgainText.visibility = View.VISIBLE
            bn.countDownTimerLinear.visibility = View.GONE
        }
    }

    private val textWatcherName = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (bn.inputVerificationNumber.text!!.length > 5) {
                Utils.closeKeyboard(requireActivity())
                bn.btnVerification.isEnabled = true
                btnClick()
            }
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