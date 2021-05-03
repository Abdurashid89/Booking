package uz.koinot.stadion.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import uz.koinot.stadion.R
import uz.koinot.stadion.databinding.FragmentActiveOrderBinding
import uz.koinot.stadion.databinding.FragmentHomeBinding

@AndroidEntryPoint
class ActiveOrderFragment : Fragment(R.layout.fragment_active_order) {

    private var _bn: FragmentActiveOrderBinding? = null
    val bn get() = _bn!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentActiveOrderBinding.bind(view)




    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}