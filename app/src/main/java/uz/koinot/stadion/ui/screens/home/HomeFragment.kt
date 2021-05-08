package uz.koinot.stadion.ui.screens.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.AuthActivity
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.StadiumAdapter
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentHomeBinding
import uz.koinot.stadion.utils.CONSTANTS
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.Utils
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModels()
    private var _bn: FragmentHomeBinding? = null
    private val bn get() = _bn!!
    private val adapter = StadiumAdapter()

    @Inject
    lateinit var storage: LocalStorage


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentHomeBinding.bind(view)

        bn.homeRv.adapter = adapter
        bn.homeRv.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getAllStadium()
        collects()

        adapter.setOnClickListener {
            findNavController().navigate(R.id.pagerFragment, bundleOf(CONSTANTS.STADION to Gson().toJson(it)),Utils.navOptions())
        }

        bn.logOut.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("Exit")
            dialog.setMessage("Do you want to exit!")
            dialog.setNegativeButton("No",{dialog, which -> dialog.dismiss() })
            dialog.setPositiveButton("Yes",{dialog, which ->
                storage.hasAccount = false
                requireActivity().startActivity(Intent(requireContext(),AuthActivity::class.java))
                requireActivity().finish()
            })
            dialog.show()

        }
    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.stadiumFlow.collect {
                when(it){
                    is UiStateList.SUCCESS ->{
                        if(it.data != null && it.data.isNotEmpty()){
                            bn.apply {
                                adapter.submitList(it.data)
                                progress.isVisible = false
                                textNotStadium.isVisible = false
                                homeRv.isVisible = true
                            }
                        }else{
                            bn.apply {
                                progress.isVisible = false
                                textNotStadium.isVisible = true
                                homeRv.isVisible = false
                            }
                        }

                    }
                    is UiStateList.ERROR ->{
                        bn.apply {
                            progress.isVisible = false
                            textNotStadium.isVisible = true
                            homeRv.isVisible = false
                        }
                    }
                    is UiStateList.LOADING ->{
                        bn.apply {
                            progress.isVisible = true
                            textNotStadium.isVisible = false
                            homeRv.isVisible = false
                        }
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}