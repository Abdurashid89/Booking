package uz.koinot.stadion.ui.screens.home.competition

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.PagerAdapter
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.databinding.FragmentDashboardBinding
import uz.koinot.stadion.databinding.FragmentPagerBinding
import uz.koinot.stadion.ui.screens.dashboard.DashboardViewModel
import uz.koinot.stadion.utils.CONSTANTS


@AndroidEntryPoint
class CompetitionFragment : Fragment(R.layout.fragment_pager) {

    private var _bn: FragmentPagerBinding? = null
    val bn get() = _bn!!
    private lateinit var adapter: PagerAdapter
    private lateinit var stadium: Stadium
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arg = arguments?.getString(CONSTANTS.STADION, "")
        stadium = Gson().fromJson(arg, Stadium::class.java)
        Log.d("AAA", "pager stadiumId: ${stadium.id}")
        navController = findNavController()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentPagerBinding.bind(view)

        bn.toolbar.apply {
            title = stadium.name
            setNavigationOnClickListener {
                navController.navigateUp()
            }
        }
        adapter = PagerAdapter(this, stadium.id)
        bn.viewPager.adapter = adapter
        bn.tabLayout.setTabTextColors(
            resources.getColor(R.color.lightGray),
            resources.getColor(R.color.colorPrimaryDarkE)
        )
        TabLayoutMediator(bn.tabLayout, bn.viewPager) { tab, position ->
            if (position == 0) {
                tab.text = getString(R.string.orders)
            } else if (position == 1) {
                tab.text = getString(R.string.dashboard)
            } else {
                tab.text = getString(R.string.cancel)
            }
        }.attach()
        bn.ivCompetition.setOnClickListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}
