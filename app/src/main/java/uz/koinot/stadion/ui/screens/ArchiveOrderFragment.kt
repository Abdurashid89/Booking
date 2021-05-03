package uz.koinot.stadion.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import uz.koinot.stadion.R
import uz.koinot.stadion.databinding.FragmentArchiveOrderkBinding
import uz.koinot.stadion.databinding.FragmentHomeBinding

@AndroidEntryPoint
class ArchiveOrderFragment : Fragment(R.layout.fragment_archive_orderk) {

    private var _bn: FragmentArchiveOrderkBinding? = null
    val bn get() = _bn!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentArchiveOrderkBinding.bind(view)




    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}