package uz.koinot.stadion.ui.screens.home

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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.BaseFragment
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.OrderAdapter
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.databinding.FragmentOderBinding
import uz.koinot.stadion.utils.*


@AndroidEntryPoint
class OderFragment : BaseFragment(R.layout.fragment_oder) {

    private val viewModel: OrderViewModel by viewModels()
    private var _bn: FragmentOderBinding? = null
    private val bn get() = _bn!!
    private val adapter = OrderAdapter()
    private  var stadiumId:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stadiumId = arguments?.getLong(CONSTANTS.STADION_ID,0)!!
        Log.d("AAA","fragment stadiumId: $stadiumId")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentOderBinding.bind(view)

//        bn.nameStadium.text = stadiumId.name
        bn.rvOrders.adapter = adapter
        bn.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getOder(stadiumId)
        bn.btnAddOrder.setOnClickListener {
//            showMessage("Tez orada ishlab qolishi mumkin")
            findNavController().navigate(R.id.createOrderFragment, bundleOf(CONSTANTS.STADION_ID to stadiumId),Utils.navOptions())
        }

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
        adapter.setOnPhoneNumber1Listener {
            requireActivity().startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${if(it.contains("+")) it else "+$it"}")))
        }

        adapter.setOnPhoneNumber2Listener {
            requireActivity().startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${if(it.contains("+")) it else "+$it"}")))
        }

    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.orderFlow.collect {
                when(it){
                    is UiStateList.SUCCESS ->{
                        showProgressDialog(false)
                        if(it.data != null && it.data.isNotEmpty()){
                            bn.apply {
                                adapter.submitList(it.data)
                                textNoOrder.isVisible = false
                                rvOrders.isVisible = true
                            }
                        }else{
                            bn.apply {
                                textNoOrder.isVisible = true
                                rvOrders.isVisible = false
                            }
                        }

                    }
                    is UiStateList.ERROR ->{
                        showProgressDialog(false)
                        bn.apply {
                            textNoOrder.isVisible = true
                            rvOrders.isVisible = false
                        }
                    }
                    is UiStateList.LOADING ->{
                        showProgressDialog(true)
                        bn.apply {
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
                        showProgressDialog(false)
                        viewModel.getOder(stadiumId)
                    }
                    is UiStateObject.ERROR ->{
                        showProgressDialog(false)
                        showMessage("Xatolik")
                    }
                    is UiStateObject.LOADING ->{
                        showProgressDialog(true)
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.rejectFlow.collect {
                when(it){
                    is UiStateObject.SUCCESS ->{
                        showProgressDialog(false)
                        viewModel.getOder(stadiumId)
                    }
                    is UiStateObject.ERROR ->{
                        showProgressDialog(false)
                        showMessage("Xatolik")
                    }
                    is UiStateObject.LOADING ->{
                        showProgressDialog(true)
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