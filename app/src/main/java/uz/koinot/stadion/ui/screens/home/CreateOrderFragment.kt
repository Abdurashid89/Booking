package uz.koinot.stadion.ui.screens.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import uz.koinot.stadion.R
import uz.koinot.stadion.databinding.FragmentCreateOrderBinding
import uz.koinot.stadion.databinding.FragmentOderBinding


@AndroidEntryPoint
class CreateOrderFragment : Fragment(R.layout.fragment_create_order) {

    private val viewModel: OrderViewModel by viewModels()
    private var _bn: FragmentCreateOrderBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentCreateOrderBinding.bind(view)

    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}