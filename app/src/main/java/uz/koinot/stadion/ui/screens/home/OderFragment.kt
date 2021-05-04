package uz.koinot.stadion.ui.screens.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.OrderAdapter
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.databinding.FragmentOderBinding
import uz.koinot.stadion.utils.CONSTANTS
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.showMessage


@AndroidEntryPoint
class OderFragment : Fragment(R.layout.fragment_oder) {

    private val viewModel: OrderViewModel by viewModels()
    private var _bn: FragmentOderBinding? = null
    private val bn get() = _bn!!
    private val adapter = OrderAdapter()
    private lateinit var stadium:Stadium

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arg = arguments?.getString(CONSTANTS.STADION,"")
        stadium = Gson().fromJson(arg, object : TypeToken<Stadium>() {}.type)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentOderBinding.bind(view)

        bn.nameStadium.text = stadium.name
        bn.rvOrders.adapter = adapter
        bn.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getOder(stadium.id)

        collects()

        adapter.setOnAcceptListener {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewModel.acceptOrder(it.id)
            }
        }

        adapter.setOnRejectListener {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewModel.rejectOrder(it.id)
            }
        }

    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.orderFlow.collect {
                when(it){
                    is UiStateList.SUCCESS ->{
                        if(it.data != null && it.data.isNotEmpty()){
                            bn.apply {
                                adapter.submitList(it.data)
                                progress.isVisible = false
                                textNoOrder.isVisible = false
                                rvOrders.isVisible = true
                            }
                        }else{
                            bn.apply {
                                progress.isVisible = false
                                textNoOrder.isVisible = true
                                rvOrders.isVisible = false
                            }
                        }

                    }
                    is UiStateList.ERROR ->{
                        bn.apply {
                            progress.isVisible = false
                            textNoOrder.isVisible = true
                            rvOrders.isVisible = false
                        }
                    }
                    is UiStateList.LOADING ->{
                        bn.apply {
                            progress.isVisible = true
                            textNoOrder.isVisible = false
                            rvOrders.isVisible = false
                        }
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.acceptFlow.collect {
                when(it){
                    is UiStateObject.SUCCESS ->{
                        showMessage("Muvaffaqiyatli")
                        viewModel.getOder(stadium.id)
                    }
                    is UiStateObject.ERROR ->{
                        showMessage("Xatolik")
                    }
                    is UiStateObject.LOADING ->{

                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.rejectFlow.collect {
                when(it){
                    is UiStateObject.SUCCESS ->{
                        showMessage("Muvaffaqiyatli")
                        viewModel.getOder(stadium.id)
                    }
                    is UiStateObject.ERROR ->{
                        showMessage("Xatolik")
                    }
                    is UiStateObject.LOADING ->{

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