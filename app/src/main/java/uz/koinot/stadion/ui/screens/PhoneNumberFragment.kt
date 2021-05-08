package uz.koinot.stadion.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import uz.koinot.stadion.R
import uz.koinot.stadion.data.api.ApiService
import uz.koinot.stadion.data.model.Register
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentPhoneNumberBinding
import uz.koinot.stadion.utils.Utils
import uz.koinot.stadion.utils.customLog
import uz.koinot.stadion.utils.showMessage
import uz.koinot.stadion.utils.userMessage
import javax.inject.Inject

@AndroidEntryPoint
class PhoneNumberFragment : Fragment(R.layout.fragment_phone_number) {

    @Inject
    lateinit var api: ApiService

    @Inject
    lateinit var storage: LocalStorage

    private var _bn: FragmentPhoneNumberBinding? = null
    val bn get() = _bn!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentPhoneNumberBinding.bind(view)

        bn.btnPhoneNumber.setOnClickListener {
            val number = bn.inputPhoneNumber.text.toString().trim()
            val password = bn.inputPassword.text.toString().trim()
            if (number.length == 13 && password.isNotEmpty()) {
                lifecycleScope.launchWhenCreated {
                    try {
                        val res = api.auth(Register(number, password))
                            if (res.statusCodeValue == 200) {
                                storage.accessToken = res.body.accessToken
                                customLog("success")
                                findNavController().navigate(
                                    R.id.verificationFragment,
                                    null,
                                    Utils.navOptions()
                                )
                            } else {
                                customLog("error")
                                showMessage(res.statusCode)
                            }

                    } catch (e: Exception) {
                            customLog(e.userMessage())
                            showMessage(e.userMessage())
                    }

                }
            } else {
                showMessage("Iltimos malumotlarni to'ldiring")
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}