package uz.koinot.stadion.adapter

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.koinot.stadion.ui.screens.dashboard.DashboardFragment
import uz.koinot.stadion.ui.screens.home.OderFragment
import uz.koinot.stadion.utils.CONSTANTS

class PagerAdapter(fragment: Fragment,val stadiumId:Long) : FragmentStateAdapter(fragment) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> OderFragment().apply { arguments = bundleOf(CONSTANTS.STADION_ID to stadiumId) }
            1 -> DashboardFragment().apply { arguments = bundleOf(CONSTANTS.STADION_ID to stadiumId) }
            else -> OderFragment().apply { arguments = bundleOf(CONSTANTS.STADION_ID to stadiumId) }
        }

    }
}