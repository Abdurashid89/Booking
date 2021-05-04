package uz.koinot.stadion.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.koinot.stadion.ui.screens.ArchiveOrderFragment
import uz.koinot.stadion.ui.screens.dashboard.DashboardFragment
import uz.koinot.stadion.ui.screens.home.OderFragment

class PagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when(position){
            0 -> fragment = OderFragment()
            1 -> fragment = ArchiveOrderFragment()
            2 -> fragment = DashboardFragment()
        }
        return fragment!!
    }
}