package uz.koinot.stadion.ui.screens.home

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.MainActivity
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.CreateOrder
import uz.koinot.stadion.data.model.SmsReceiver
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentForgotBinding
import uz.koinot.stadion.databinding.FragmentPasswordBinding
import uz.koinot.stadion.ui.screens.LoginViewModel
import uz.koinot.stadion.utils.TextWatcherWrapper
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.Utils
import uz.koinot.stadion.utils.vibrate
import javax.inject.Inject

@AndroidEntryPoint
class PasswordFragment : Fragment(R.layout.fragment_password) {

    private var _bind: FragmentPasswordBinding? = null
    private val bind get() = _bind!!

    private var phoneNumber:String? = null

    private val viewmodel : LoginViewModel by viewModels()

    @Inject
    lateinit var pref: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null){
            phoneNumber = arguments?.getString("number","")?:""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      _bind = FragmentPasswordBinding.bind(view)

        SmsReceiver.setReceiveCodeListener {
            bind.inputVerificationNumber.setText(it)
        }

        bind.inputVerificationNumber.addTextChangedListener(object : TextWatcherWrapper(){
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                if(bind.inputVerificationNumber.text.toString().length == 6){
                   bind.inputPassword.requestFocus()
                }
            }
        })

        bind.btnPhoneNumber.setOnClickListener {
            val code = bind.inputVerificationNumber.text.toString()
            val password = bind.inputPassword.text.toString().trim()
            val confirm = bind.inputConfirmPassword.text.toString().trim()

            when{
                code.length != 6 ->{
                    bind.inputVerificationNumber.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                    vibrate(requireContext())
                }

                password.length < 4 ->{
                    bind.inputPassword.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                    vibrate(requireContext())
                }

                confirm.isEmpty() ->{
                    bind.inputConfirmPassword.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                    vibrate(requireContext())

                }

                password != confirm ->{
                    bind.inputConfirmPassword.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                    vibrate(requireContext())
                }

                else ->{
                    phoneNumber?.let { it1 -> viewmodel.sendForgotPassword(code,password, it1) }
                }
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewmodel.createPasswordFlow.collect{
                when(it){
                    is UiStateObject.SUCCESS ->{
                        pref.accessToken = it.data
                        pref.hasAccount = true
                        pref.phoneNumber = phoneNumber.toString()
                        pref.firebaseToken = ""
                        requireActivity().startActivity(
                            Intent(requireContext(),
                                MainActivity::class.java)
                        )
                        requireActivity().finish()
                        showProgress(false)
                    }
                    is UiStateObject.ERROR ->{
                        showProgress(false)
                    }
                    is UiStateObject.LOADING ->{
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }

    }


    private fun showProgress(status:Boolean){
        bind.progressBar.isVisible = status
    }

}