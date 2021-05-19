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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
class HomeFragment : Fragment(R.layout.fragment_home), SwipeRefreshLayout.OnRefreshListener {

    private val viewModel: HomeViewModel by viewModels()
    private var _bn: FragmentHomeBinding? = null
    private val bn get() = _bn!!
    private val adapter = StadiumAdapter()
    private lateinit var navController: NavController

    @Inject
    lateinit var storage: LocalStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()

        viewModel.getAllStadium()

        lifecycleScope.launchWhenCreated {
            viewModel.stadiumFlow.collect {
                when(it){
                    is UiStateList.SUCCESS ->{
                        it.data?.let { it1 -> adapter.submitList(it1) }
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentHomeBinding.bind(view)
        bn.swipeRefresh.setOnRefreshListener(this)

        bn.homeRv.adapter = adapter
        bn.homeRv.layoutManager = LinearLayoutManager(requireContext())
        collects()

        adapter.setOnClickListener {
            navController.navigate(R.id.pagerFragment, bundleOf(CONSTANTS.STADION to Gson().toJson(it)),Utils.navOptions())
        }

        adapter.setOnImageClickListener { stadium, position ->
            val dialog = ImageDialog(stadium.photos,position)
            dialog.show(childFragmentManager,"image")
        }
        bn.logOut.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("Exit")
            dialog.setMessage("Do you want to exit!")
            dialog.setNegativeButton("No",{dialog, which -> dialog.dismiss() })
            dialog.setPositiveButton("Yes") { dialog, which ->
                storage.hasAccount = false
                requireActivity().startActivity(Intent(requireContext(),AuthActivity::class.java))
                requireActivity().finish()
            }
            dialog.show()

        }

        bn.addStadium.setOnClickListener {
            navController.navigate(R.id.mapFragment2,null,Utils.navOptions())
        }
    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.stadiumFlow.collect {
                when(it){
                    is UiStateList.SUCCESS ->{
                        bn.swipeRefresh.isRefreshing = false
                        if(it.data != null && it.data.isNotEmpty()){
                            bn.apply {
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
                            textNotStadium.isVisible = true
                            if(it.fromServer)
                                textNotStadium.text = it.message
                            if(it.code == 400)
                                storage.hasAccount = false
                            requireActivity().startActivity(Intent(requireContext(),AuthActivity::class.java))
                            requireActivity().finish()

                            swipeRefresh.isRefreshing = false
                            progress.isVisible = false
                            homeRv.isVisible = false
                        }
                    }
                    is UiStateList.LOADING ->{
                        bn.apply {
                            swipeRefresh.isRefreshing = false
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

    override fun onRefresh() {
        viewModel.getAllStadium()
    }
}