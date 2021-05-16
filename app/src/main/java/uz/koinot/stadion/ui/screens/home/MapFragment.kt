package uz.koinot.stadion.ui.screens.home

import android.Manifest
import android.content.IntentSender
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import uz.koinot.stadion.R
import uz.koinot.stadion.databinding.FragmentHomeBinding
import uz.koinot.stadion.databinding.FragmentMapBinding
import uz.koinot.stadion.utils.CONSTANTS
import uz.koinot.stadion.utils.GPSTracker
import uz.koinot.stadion.utils.Utils
import uz.koinot.stadion.utils.checkPermission

class MapFragment : Fragment(R.layout.fragment_map) {

    private var _bn: FragmentMapBinding? = null
    private val bn get() = _bn!!
    private var mMap:GoogleMap? = null
    private var location:LatLng? = null
    private var marker: Marker? = null
    private lateinit var tracker:GPSTracker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _bn = FragmentMapBinding.inflate(inflater,container,false)
        val map = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        map.getMapAsync {
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
                mMap = it
                tracker = GPSTracker(requireContext())
                if(tracker.canGetLocation()){
                    tracker.getLocation()
                    marker = mMap?.addMarker(
                        MarkerOptions().title("My Location")
                            .position(LatLng(tracker.getLatitude(),tracker.getLongitude()))
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker())
                    )
                    mMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                tracker.getLatitude(),
                                tracker.getLongitude()
                            ), 13f
                        )
                    )
                }else{
                    createLocationRequest()
                }

            }
        }
        mMap?.setOnMapClickListener { latLng ->
            location = latLng
            marker?.apply {
                position = latLng
            }
        }

        return bn.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        bn.btnChooseLocation.setOnClickListener {
            Toast.makeText(requireContext(), "choose", Toast.LENGTH_SHORT).show()
            if(location != null){
                findNavController().navigate(R.id.createStadiumFragment, bundleOf(CONSTANTS.LOCATION to Gson().toJson(location)),Utils.navOptions())
            }
        }


    }

    private fun createLocationRequest() {
        try {
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
                val locationRequest = LocationRequest.create().apply {
                    interval = 5000
                    fastestInterval = 2000
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                builder.setAlwaysShow(true)

                val client: SettingsClient = LocationServices.getSettingsClient(requireContext())
                val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
                try {
                    task.addOnCompleteListener {
                        try {
                            it.getResult(ApiException::class.java)
                        } catch (e: ApiException) {
                            when (e.statusCode) {
                                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                                    try {
                                        val resolvableApiException = e as ResolvableApiException
                                        try {
                                            resolvableApiException.startResolutionForResult(activity, 101)
                                        }catch (e:Exception){
                                            e.printStackTrace()
                                        }
                                    } catch (e: IntentSender.SendIntentException) {
                                        e.printStackTrace()
                                    }
                                }
                                else -> Unit
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}