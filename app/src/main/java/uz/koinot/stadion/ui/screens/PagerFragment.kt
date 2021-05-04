package uz.koinot.stadion.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.PagerAdapter
import uz.koinot.stadion.databinding.FragmentDashboardBinding
import uz.koinot.stadion.databinding.FragmentPagerBinding
import uz.koinot.stadion.ui.screens.dashboard.DashboardViewModel


@AndroidEntryPoint
class PagerFragment : Fragment(R.layout.fragment_pager) {

    private var _bn: FragmentPagerBinding? = null
    val bn get() = _bn!!
    private lateinit var adapter : PagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentPagerBinding.bind(view)
        adapter = PagerAdapter(this)
        bn.viewPager.adapter = adapter

        TabLayoutMediator(bn.tabLayout,bn.viewPager){tab, pager->
            
        }.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}
