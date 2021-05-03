package uz.koinot.stadion.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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


    private var _bn: FragmentVerificationBinding? = null
    val bn get() = _bn!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentVerificationBinding.bind(view)

        bn.btnVerification.setOnClickListener {
            val number = bn.inputVerificationNumber.text.toString().trim()
            if(number.isNotEmpty()){
                GlobalScope.launch {
                    try {
                        val res = api.verify(number)
                        MainScope().launch {
                            if(res.success == 200){
                                showMessage("Siz muvaffaqiyatli ro'yhatdan o'tdingiz!")
                                storage.hasAccount = true
                                requireActivity().startActivity(Intent(requireContext(),MainActivity::class.java))
                                requireActivity().finish()
                            }else{
                                showMessage("Xatolik")
                            }
                        }

                    }catch (e:Exception){
                        MainScope().launch {
                            showMessage("Xatolik")
                        }
                    }
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}