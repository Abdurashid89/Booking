package uz.koinot.stadion.ui.screens.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import uz.koinot.stadion.R
import uz.koinot.stadion.databinding.FragmentHomeBinding
import uz.koinot.stadion.databinding.FragmentMapBinding

class MapFragment : Fragment(R.layout.fragment_map) {

    private var _bn: FragmentMapBinding? = null
    private val bn get() = _bn!!
    private var mMap:GoogleMap? = null
    private var location:LatLng? = null
    private var marker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _bn = FragmentMapBinding.inflate(inflater,container,false)
        val map = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        map.getMapAsync {
            mMap = it
            marker = mMap?.addMarker(MarkerOptions().title("My Location").position(it.cameraPosition.target).draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker()))
        }

        return bn.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mMap?.setOnMapClickListener { latLng ->
            location = latLng
            marker?.apply {
                position = latLng
            }
        }
        bn.btnChooseLocation.setOnClickListener {
            if(location != null){

            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}