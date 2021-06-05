package uz.koinot.stadion.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.BaseFragment
import uz.koinot.stadion.MainActivity
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.Login
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentLoginBinding
import uz.koinot.stadion.ui.screens.home.HomeViewModel
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.Utils
import uz.koinot.stadion.utils.showMessage
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    @Inject
    lateinit var storage: LocalStorage
    private var number = ""

    private var _bn: FragmentLoginBinding? = null
    val bn get() = _bn!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentLoginBinding.bind(view)

        bn.apply {
            btnLogin.setOnClickListener {
                number = inputPhoneNumber.text.toString().trim()
                val password = inputPassword.text.toString().trim()
                if(number.length == 13 && password.length > 2){
                    viewModel.login(Login(number,password))
                }else{
                    showMessage(getString(R.string.enter_fields))
                }

            }
            createAccount.setOnClickListener {
                findNavController().navigate(R.id.phoneNumberFragment,null,Utils.navOptions())
            }


        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.loginFlow.collect {
                when(it){
                    is UiStateObject.SUCCESS ->{
                        showProgress(false)
                        storage.hasAccount = true
                        storage.phoneNumber = number
                        storage.firebaseToken = ""
                        requireActivity().startActivity(Intent(requireContext(),MainActivity::class.java))
                        requireActivity().finish()
                        viewModel.reLogin()
                    }
                    is UiStateObject.ERROR ->{
                        storage.hasAccount = false
                        storage.phoneNumber = ""
                        storage.firebaseToken = ""
                        showProgress(false)
                        showMessage(it.message)
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
        bn.progressBar.isVisible = status
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}