package uz.koinot.stadion.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.koinot.stadion.ui.screens.ArchiveOrderFragment
import uz.koinot.stadion.ui.screens.dashboard.DashboardFragment
import uz.koinot.stadion.ui.screens.home.OderFragment
import uz.koinot.stadion.utils.CONSTANTS

class PagerAdapter(fragment: Fragment,val stadiumId:Int) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when(position){
            0 -> fragment = OderFragment().apply { arguments?.putInt(CONSTANTS.STADION_ID,stadiumId) }
            1 -> fragment = DashboardFragment().apply { arguments?.putInt(CONSTANTS.STADION_ID,stadiumId) }
//            1 -> fragment = ArchiveOrderFragment().apply { arguments?.putInt(CONSTANTS.STADION_ID,stadiumId) }
        }
        return fragment!!
    }
}