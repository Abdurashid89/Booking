package uz.koinot.stadion.ui.screens.phone

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser
import ru.tinkoff.decoro.watchers.MaskFormatWatcher
import uz.koinot.stadion.R
import uz.koinot.stadion.data.api.AuthService
import uz.koinot.stadion.data.model.Register
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentPhoneNumberBinding
import uz.koinot.stadion.ui.screens.auth.register.AuthViewModel
import uz.koinot.stadion.utils.dialogs.GoToTelegramDialog
import uz.koinot.stadion.utils.*
import uz.koinot.stadion.utils.extensions.showMessage
import uz.koinot.stadion.utils.extensions.vibrate
import uz.koinot.stadion.utils.sealed.UiStateObject
import javax.inject.Inject

@AndroidEntryPoint
class PhoneNumberFragment : Fragment(R.layout.fragment_phone_number) {

    @Inject
    lateinit var api: AuthService

    @Inject
    lateinit var storage: LocalStorage
    private var number = ""
    private var password = ""

    private var _bn: FragmentPhoneNumberBinding? = null
    val bn get() = _bn!!

    private val viewModel: AuthViewModel by viewModels()


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

            brbt.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://t.me/brbtbot")
                requireContext().startActivity(intent)
            }

            bn.inputConfirmPassword.addTextChangedListener(object : TextWatcherWrapper(){
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    super.onTextChanged(s, start, before, count)
                    bn.errorConfirm.isVisible = false
                }
            })
            btnPhoneNumber.setOnClickListener {
                number = bn.inputPhoneNumber.text.toString().replace(" ","")


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

            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewModel.registerFlow.collect {
                    when(it){
                        is UiStateObject.SUCCESS->{
                            showProgress(false)
                            storage.accessToken =it.data.accessToken
                            storage.phoneNumber = number
                            findNavController().navigate(
                                R.id.verificationFragment,
                                null,
                                Utils.navOptions()
                            )
                            viewModel.reRegister()
                        }

                        is UiStateObject.ERROR->{
                            showMessage(it.message)
                            showProgress(false)
                        }

                        is UiStateObject.LOADING->{
                            showProgress(true)
                        }
                        else -> Unit
                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewModel.isBotStartedFlow.collect {
                    when(it){
                        is UiStateObject.SUCCESS->{
                            showProgress(false)
                            viewModel.register(Register(number, password))
                            viewModel.reBot()
                        }

                        is UiStateObject.ERROR->{
                            showProgress(false)
                            if(it.fromServer){
                                val dialog = GoToTelegramDialog()
                                dialog.setOnDeleteListener {
                                    dialog.dismiss()
                                    val intent = Intent(Intent.ACTION_VIEW)
                                    intent.data = Uri.parse("https://t.me/brbtbot")
                                    requireContext().startActivity(intent)
                                }
                                dialog.show(childFragmentManager,"ggg")
                            }else{
                                showMessage(it.message)
                            }
                        }

                        is UiStateObject.LOADING->{
                            showProgress(true)
                        }
                        else -> Unit
                    }
                }
            }
        }


    }

    private fun send() {
        Utils.closeKeyboard(requireActivity())
        number = bn.inputPhoneNumber.text.toString().replace(" ","")

        password = bn.inputPassword.text.toString().trim()
        val confirmPassword = bn.inputConfirmPassword.text.toString().trim()

        when{
            number.length != 13 ->{
               bn.inputPhoneNumber.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                vibrate(requireContext())
            }

            password.length < 4 ->{
                bn.inputPassword.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                vibrate(requireContext())
            }

            confirmPassword.isEmpty() ->{
                bn.inputConfirmPassword.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                vibrate(requireContext())
            }
            password != confirmPassword ->{
                vibrate(requireContext())
                bn.inputConfirmPassword.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.shake))
                bn.errorConfirm.isVisible = true
            }

            else ->{
                viewModel.isBotStarted(number)
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