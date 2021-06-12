package uz.koinot.stadion.ui.screens

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import uz.koinot.stadion.BaseFragment
import uz.koinot.stadion.R
import uz.koinot.stadion.data.api.ApiService
import uz.koinot.stadion.data.api.AuthService
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
    lateinit var api: AuthService

    @Inject
    lateinit var storage: LocalStorage
    private var number = ""

    private var _bn: FragmentPhoneNumberBinding? = null
    val bn get() = _bn!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentPhoneNumberBinding.bind(view)

        bn.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            val slots = PhoneNumberUnderscoreSlotsParser().parseSlots("+998 __ ___ __ __")
            val format = MaskFormatWatcher(MaskImpl.createTerminated(slots))
            format.installOn(bn.inputPhoneNumber)

            bn.inputPhoneNumber.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if(hasFocus){
                    bn.inputPhoneNumber.setText("+998")
                }
            }
            bn.inputPhoneNumber.addTextChangedListener(textWatcherName)

            btnPhoneNumber.setOnClickListener {
                number = bn.inputPhoneNumber.text.toString().replace(" ","")
                val password = bn.inputPassword.text.toString().trim()
                if (number.length == 13 && password.isNotEmpty()) {
                    lifecycleScope.launchWhenCreated {
                        try {
                            showProgress(true)
                            val res = api.auth(Register(number, password))
                            if (res.success == 200) {
                                showProgress(false)
                                storage.accessToken = res.objectKoinot!!.accessToken
                                customLog("success")
                                storage.phoneNumber = number
                                findNavController().navigate(
                                    R.id.verificationFragment,
                                    null,
                                    Utils.navOptions()
                                )
                            } else {
                                showProgress(false)
                                customLog("error")
                                showMessage(res.message)
                            }

                        } catch (e: Exception) {
                            showProgress(false)
                            customLog(e.userMessage())
                            showMessage(e.userMessage())
                        }

                    }
                } else {
                    showMessage(getString(R.string.enter_fields))
                }
            }
        }


    }

    private val textWatcherName = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (bn.inputPhoneNumber.text.toString().length == 17) {
                bn.inputPassword.requestFocus()
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