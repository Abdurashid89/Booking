package uz.koinot.stadion.ui.screens.dashboard

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dagger.hilt.android.AndroidEntryPoint
import ir.farshid_roohi.linegraph.ChartEntity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.DashboardOrderAdapter
import uz.koinot.stadion.data.model.Dashboard
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.databinding.FragmentDashboardBinding
import uz.koinot.stadion.utils.CONSTANTS
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.showMessage
import uz.koinot.stadion.utils.toNeedDate
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _bn: FragmentDashboardBinding? = null
    val bn get() = _bn!!
    private val viewModel: DashboardViewModel by viewModels()
    private var stadiumId = 0L
    private val adapter = DashboardOrderAdapter()
    var type = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stadiumId = arguments?.getLong(CONSTANTS.STADION_ID,0L) ?: 0L
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentDashboardBinding.bind(view)

        bn.rvOrders.adapter = adapter
        collects()

        try {
            val startDate = Timestamp(System.currentTimeMillis() - 2592000000)
            val endDate = Timestamp(System.currentTimeMillis())
            viewModel.getDashboard(stadiumId, startDate.toString(), endDate.toString())
        }catch (e:Exception){
        }

    }

    private fun collects() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getAllOrder(stadiumId).collect {
                if(it.isEmpty()){
                    viewModel.archiveAll(stadiumId)
                }else{
                    adapter.submitList(it)
                    if (!type) viewModel.afterCreateFlow(stadiumId,it[0].createdAt.toNeedDate())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.archiveAllFlow.collect {
                when (it) {
                    is UiStateList.SUCCESS -> {
                        showProgress(false)
                        if (it.data != null && it.data.isNotEmpty()){
                            addToDb(it.data)
                        }
                    }
                    is UiStateList.ERROR -> {
                        showProgress(false)
                        showMessage(it.message)
                    }
                    is UiStateList.LOADING -> {
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.afterCreateFlow.collect {
                when (it) {
                    is UiStateList.SUCCESS -> {
                        showProgress(false)
                        if (it.data != null && it.data.isNotEmpty()){
                            addToDb(it.data)
                        }
                    }
                    is UiStateList.ERROR -> {
                        showProgress(false)
                        showMessage(getString(R.string.error))
                    }
                    is UiStateList.LOADING -> {
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.dashboardFlow.collect {
                when (it) {
                    is UiStateList.SUCCESS -> {
                        Log.d("AAAA","SUCCESS")
                        showProgress(false)

                        if (it.data != null && it.data.isNotEmpty()){
                            createChart(it.data)
                        }

                    }
                    is UiStateList.ERROR -> {
//                        bn.lineChart.isVisible = false
                        Log.d("AAAA","ERROR ${it.message}")
                        showProgress(false)
                        showMessage(getString(R.string.error))
                    }
                    is UiStateList.LOADING -> {
//                        bn.lineChart.isVisible = false
                        Log.d("AAAA","LOADING")
                        showProgress(true)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun addToDb(data: List<Order>) {
        type = true
        data.forEach {
            it.stadiumId = stadiumId
        }
        GlobalScope.launch {
            viewModel.setAllOrder(data)
        }
    }

    private fun lineGraph(entry: ArrayList<Entry>) {

        bn.lineChart.setNoDataText("No Balance yet!")

        bn.lineChart.isAutoScaleMinMaxEnabled = true
        bn.lineChart.legend.isEnabled = true
        bn.lineChart.animateX(1500)

        val vl = LineDataSet(entry, "Ежедневные деньги")
        vl.setDrawFilled(true)

        vl.setDrawValues(false)
        vl.mode = LineDataSet.Mode.HORIZONTAL_BEZIER

        vl.lineWidth = 5f
        vl.setDrawCircles(true)

        val paint = bn.lineChart.renderer.paintRender
        val height = bn.lineChart.height.toFloat()
        val width = bn.lineChart.width.toFloat()

        val lindGrad = LinearGradient(
            0f,
            0f,
            width,
            height,
            Color.BLUE,
            Color.parseColor("#E45CF4"),
            Shader.TileMode.REPEAT
        )
        paint.shader = lindGrad
        vl.fillColor = R.color.green
        vl.fillAlpha = R.color.red



        bn.lineChart.axisRight.isEnabled = false
        bn.lineChart.axisRight.setDrawAxisLine(false)
        bn.lineChart.axisRight.setDrawLabels(false)
        bn.lineChart.axisRight.setDrawGridLines(false)
        bn.lineChart.description.isEnabled = false
        bn.lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM


        bn.lineChart.setTouchEnabled(false)

        bn.lineChart.data = LineData(vl)
        bn.lineChart.invalidate()
        bn.lineChart.resetZoom()


    }

    private fun createChart(list: List<Dashboard>) {
        bn.lineChart.isVisible = true
        val ls = ArrayList<Entry>()
        for (i in list.indices){
            ls.add(Entry(list[i].day.toFloat(), list[i].benefit.toFloat()))
        }
        lineGraph(ls)
    }

    private fun showProgress(status:Boolean){
        bn.progressBar.isVisible = status
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}