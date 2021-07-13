package uz.koinot.stadion.ui.screens.home

import android.Manifest
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.R
import uz.koinot.stadion.databinding.FragmentForgotBinding
import uz.koinot.stadion.ui.screens.LoginViewModel
import uz.koinot.stadion.utils.*

@AndroidEntryPoint
class ForgotFragment : Fragment(R.layout.fragment_forgot){

    private var _bind:FragmentForgotBinding? = null
    private val bind get() = _bind!!

    private var number = ""
    private val viewmodel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bind = FragmentForgotBinding.bind(view)

        bind.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        bind.forgotBtn.setOnClickListener {
            checkPermissionState(Manifest.permission.RECEIVE_SMS,{
                checkPermissionState(Manifest.permission.READ_SMS,{
                    send()
                },{
                    send()
                })
            },{
                send()
            })
        }

        bind.inputPhoneNumber.addTextChangedListener(object : TextWatcherWrapper(){
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
               if(bind.inputPhoneNumber.rawText.length == 9){
                   Utils.closeKeyboard(requireActivity())
                   bind.forgotBtn.isEnabled = true
               }else{
                   bind.forgotBtn.isEnabled = false
               }
            }
        })

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewmodel.forgotPhoneFlow.collect{
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        showProgress(false)
                        viewmodel.reCode()
                        findNavController().navigate(
                            R.id.passwordFragment,
                            bundleOf("number" to number),
                            Utils.navOptions()
                        )
                    }
                    is UiStateObject.ERROR -> {
                        showProgress(false)
                        viewmodel.reCode()
                        showMessage(it.message)
                    }
                    is UiStateObject.LOADING -> {
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun send() {
        number = "+998" + bind.inputPhoneNumber.rawText.toString()
        if(number.length == 13)viewmodel.getVerificationCode((number))
        else{
            bind.inputPhoneNumber.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
            vibrate(requireContext())
        }
    }


    private fun showProgress(status:Boolean){
        bind.progressBar.isVisible = status
    }

}