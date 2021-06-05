package uz.koinot.stadion.ui.screens.dashboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import im.dacer.androidcharts.LineView
import ir.farshid_roohi.linegraph.ChartEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.koinot.stadion.BaseFragment
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.DashboardOrderAdapter
import uz.koinot.stadion.adapter.OrderAdapter
import uz.koinot.stadion.data.model.Dashboard
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.databinding.FragmentDashboardBinding
import uz.koinot.stadion.utils.CONSTANTS
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.showMessage

@AndroidEntryPoint
class DashboardFragment : BaseFragment(R.layout.fragment_dashboard) {

    private var _bn: FragmentDashboardBinding? = null
    val bn get() = _bn!!
    private val viewModel: DashboardViewModel by viewModels()
    private var stadiumId = 0L
    private val adapter = DashboardOrderAdapter()
    private var ordersList = ArrayList<Order>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stadiumId = arguments?.getLong(CONSTANTS.STADION_ID)!!
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentDashboardBinding.bind(view)

        bn.rvOrders.adapter = adapter
        viewModel.getDashboard(stadiumId)

        viewModel.archiveAll(stadiumId)
        collects()


    }

    private fun collects() {

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.archiveAllFlow.collect {
                when (it) {
                    is UiStateList.SUCCESS -> {
                        showProgressDialog(false)
                        bn.rvOrders.isVisible = true
                        if (it.data != null && it.data.isNotEmpty()){
                            adapter.submitList(it.data)
                            bn.nothing.visibility = View.GONE
                        }else{
                            bn.nothing.visibility = View.VISIBLE
                        }
                    }
                    is UiStateList.ERROR -> {
                        showProgressDialog(false)
                        bn.rvOrders.isVisible = false
                        showMessage("Xatolik")
                    }
                    is UiStateList.LOADING -> {
                        showProgressDialog(true)
                        bn.nothing.visibility = View.GONE
                        bn.rvOrders.isVisible = false
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.afterCreateFlow.collect {
                when (it) {
                    is UiStateList.SUCCESS -> {
                        bn.rvOrders.isVisible = true
                        val list = ArrayList<Order>()
                        it.data?.let { it1 -> list.addAll(it1) }
                        list.addAll(ordersList)
                        adapter.submitList(list)
                        GlobalScope.launch {
                            viewModel.removeAllOrder()
                            viewModel.setAllOrder(list)
                        }
                    }
                    is UiStateList.ERROR -> {
                        bn.rvOrders.isVisible = false
                        showMessage("Xatolik")
                    }
                    is UiStateList.LOADING -> {
                        bn.rvOrders.isVisible = false
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.dashboardFlow.collect {
                when (it) {
                    is UiStateList.SUCCESS -> {
                        if (it.data != null && it.data.isNotEmpty()){
                            createChart(it.data)
                            bn.nothing.isVisible = false
                        }else{
                            bn.nothing.isVisible = true
                        }

                    }
                    is UiStateList.ERROR -> {
                        showMessage("Xatolik")
                    }
                    is UiStateList.LOADING -> {
                        bn.nothing.isVisible = false
                    }
                    else -> Unit
                }
            }
        }
    }

    @SuppressLint("WrongViewCast")
    private fun createChart(list: List<Dashboard>) {

        val day = java.util.ArrayList<String>()
        val benefit = java.util.ArrayList<Float>()

        list.forEach {
            day.add(it.day)
            benefit.add(it.benefit)
        }
        val view = bn.lineView as LineView
        view.apply {
            setDrawDotLine(false)
            setShowPopup(LineView.SHOW_POPUPS_MAXMIN_ONLY)
            setColorArray(intArrayOf(Color.parseColor("#0E8C30")))

            setFloatDataList(arrayListOf(benefit))
            setBottomTextList(day)
        }
       


    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}