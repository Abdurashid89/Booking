package uz.koinot.stadion.ui.screens.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.DeleteOrderAdapter
import uz.koinot.stadion.adapter.OrderAdapter
import uz.koinot.stadion.databinding.FragmentOderBinding
import uz.koinot.stadion.databinding.FragmentOrderDeleteBinding
import uz.koinot.stadion.ui.screens.home.BaseDialog
import uz.koinot.stadion.ui.screens.home.OrderViewModel
import uz.koinot.stadion.utils.*

@AndroidEntryPoint
class OrderDeleteFragment : Fragment(R.layout.fragment_order_delete) {

    private val viewModel: OrderDeleteViewModel by viewModels()
    private var _bn: FragmentOrderDeleteBinding? = null
    private val bn get() = _bn!!
    private val adapter = DeleteOrderAdapter()
    private  var stadiumId : Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stadiumId = arguments?.getLong(CONSTANTS.STADION_ID,0)!!
        Log.d("AAA","fragment stadiumId: $stadiumId")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentOrderDeleteBinding.bind(view)

        bn.rvDeleteOrders.adapter = adapter
        bn.rvDeleteOrders.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getCancel(stadiumId)

        collects()

        adapter.setOnDeleteListener {
            viewModel.orderDelete(it.id)
        }



    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getCancelFlow.collect {
                when(it){
                    is UiStateList.SUCCESS -> {
                        showProgress(false)
                        if (it.data != null && it.data.isNotEmpty()) {
                            bn.apply {
                                adapter.submitList(it.data)
                            }
                        }
                    }
                    is UiStateList.ERROR ->{
                        showProgress(false)
                       showMessage(it.message)
                    }
                    is UiStateList.LOADING ->{
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.orderDeleteFlow.collect {
                when(it){
                    is UiStateObject.SUCCESS ->{
                        showProgress(false)
                        viewModel.getCancel(stadiumId)
                    }
                    is UiStateObject.ERROR ->{
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