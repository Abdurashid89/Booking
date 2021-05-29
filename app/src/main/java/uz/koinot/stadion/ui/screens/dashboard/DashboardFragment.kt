package uz.koinot.stadion.ui.screens.dashboard

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
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
        stadiumId = arguments?.getLong(CONSTANTS.STADION_ID,0L) ?: 0L
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentDashboardBinding.bind(view)

        bn.rvOrders.adapter = adapter
        viewModel.getDashboard(stadiumId)
        collects()


    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getAllOrder(stadiumId).collect {
                if(it.isEmpty()){
                    viewModel.archiveAll(stadiumId)
                }else{
                    val time = "${it[0].time} ${it[0].startDate}:30.000000"
                    viewModel.afterCreateFlow(stadiumId,time)
                    adapter.submitList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.archiveAllFlow.collect {
                when (it) {
                    is UiStateList.SUCCESS -> {
                        showProgressDialog(false)
                        bn.rvOrders.isVisible = true
                        if (it.data != null && it.data.isNotEmpty()){
                            addToDb(it.data)
                            bn.nothing.visibility = View.GONE
                        }else{
//                            bn.nothing.visibility = View.VISIBLE
                        }
                    }
                    is UiStateList.ERROR -> {
                        showProgressDialog(false)
                        bn.rvOrders.isVisible = false
                        showMessage(getString(R.string.error))
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

                        if (it.data != null && it.data.isNotEmpty()){
                            addToDb(it.data)
                        }
                    }
                    is UiStateList.ERROR -> {
                        bn.rvOrders.isVisible = false
                        showMessage(getString(R.string.error))
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
                        showMessage(getString(R.string.error))
                    }
                    is UiStateList.LOADING -> {
                        bn.nothing.isVisible = false
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun addToDb(data: List<Order>) {
        val ls = ArrayList<Order>()

        data.forEach { order->
            order.stadiumId = stadiumId
            ls.add(order)
        }

        GlobalScope.launch {
            viewModel.setAllOrder(ls)
        }
    }

    private fun createChart(list: List<Dashboard>) {

        val day = ArrayList<String>()
        val benefit = ArrayList<Float>()
        val count = ArrayList<Float>()
        list.forEach {
            day.add(it.day)
            benefit.add(it.benefit)
            count.add(it.count.toFloat())
        }

        val one = ChartEntity(Color.WHITE,benefit.toFloatArray())
        val two = ChartEntity(Color.YELLOW,count.toFloatArray())
        val list = ArrayList<ChartEntity>()
        list.add(one)
        list.add(two)
        bn.lineChart.isVisible = true
        bn.lineChart.legendArray = day.toArray() as Array<String>
        bn.lineChart.setList(list)
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}