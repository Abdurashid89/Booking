package uz.koinot.stadion.ui.screens.dashboard

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import ir.farshid_roohi.linegraph.ChartEntity
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.Dashboard
import uz.koinot.stadion.databinding.FragmentDashboardBinding
import uz.koinot.stadion.utils.CONSTANTS
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.showMessage

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _bn: FragmentDashboardBinding? = null
    val bn get() = _bn!!
    private val viewModel: DashboardViewModel by viewModels()
    private var stadiumId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stadiumId = arguments?.getInt(CONSTANTS.STADION_ID)!!
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentDashboardBinding.bind(view)

        viewModel.getDashboard(stadiumId)

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.dashboardFlow.collect {
                when(it){
                    is UiStateList.SUCCESS ->{
                        showMessage("Muvaffaqiyatli")
                        if(it.data != null && it.data.isNotEmpty()) createChart(it.data)
                    }
                    is UiStateList.ERROR ->{
                        showMessage("Xatolik")
                    }
                    is UiStateList.LOADING ->{

                    }
                    else -> Unit
                }
            }
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
        bn.lineChart.legendArray = day.toArray() as Array<String>
        bn.lineChart.setList(list)
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}