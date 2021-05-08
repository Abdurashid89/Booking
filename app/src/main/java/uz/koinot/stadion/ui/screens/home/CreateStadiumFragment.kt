package uz.koinot.stadion.ui.screens.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import uz.koinot.stadion.R
import uz.koinot.stadion.databinding.FragmentCreateStadiumBinding


@AndroidEntryPoint
class CreateStadiumFragment : Fragment(R.layout.fragment_create_stadium) {

    private var _bn: FragmentCreateStadiumBinding? = null
    val bn get() = _bn!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentCreateStadiumBinding.bind(view)

    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}