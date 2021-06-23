package uz.koinot.stadion.ui.screens.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
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
import uz.koinot.stadion.databinding.FragmentMapBinding
import uz.koinot.stadion.utils.*

class MapFragment : DialogFragment() {

    private var _bn: FragmentMapBinding? = null
    private val bn get() = _bn!!
    private var mMap:GoogleMap? = null
    private var location:LatLng? = null
    private var marker: Marker? = null
    private lateinit var tracker:GPSTracker
    private var adress = ""

    private var listener : ((LatLng,String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialog2)
    }

    @SuppressLint("PotentialBehaviorOverride")
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
                    marker?.remove()
                    marker = mMap?.addMarker(
                        MarkerOptions().title("My Location")
                            .position(LatLng(tracker.getLatitude(),tracker.getLongitude()))
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker())
                    )
                    adress = getCompleteAddressString(tracker.getLatitude(),tracker.getLongitude()).toString()
                    bn.button.text = adress
                    location =  LatLng(
                        tracker.getLatitude(),
                        tracker.getLongitude()
                    )
                    mMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                           location, 13f
                        )
                    )


                    mMap?.setOnMapClickListener { latLng ->
                        location = latLng
                        adress = getCompleteAddressString(latLng.latitude,latLng.longitude).toString()
                        bn.button.text = adress
                        marker?.apply {
                            position = location
                            title = adress
                        }
//                        marker = mMap?.addMarker(MarkerOptions().position(latLng).title(adress).icon(BitmapDescriptorFactory.defaultMarker()))
                    }
                    mMap?.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener{
                        override fun onMarkerDragStart(p0: Marker) {}
                        override fun onMarkerDrag(p0: Marker) {}
                        override fun onMarkerDragEnd(marker: Marker) {
                            adress = getCompleteAddressString(marker.position.latitude,marker.position.longitude).toString()
                            location = LatLng(marker.position.latitude,marker.position.longitude)
                            bn.button.text = adress
                        }
                    })


                }else{
                    createLocationRequest()
                }

            }
        }


        return bn.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        bn.btnBack.setOnClickListener {
            dismiss()
        }

        bn.btnMyLocation.setOnClickListener {
            marker?.apply {
                location = LatLng(tracker.getLatitude(),tracker.getLongitude())
                position = location
                isVisible = true
                adress = getCompleteAddressString(location!!.latitude,location!!.longitude).toString()
                bn.button.text = adress
            }
            mMap?.animateCamera(CameraUpdateFactory.newLatLng(LatLng(tracker.getLatitude(),tracker.getLongitude())))
        }

        bn.btnChooseLocation.setOnClickListener {
            if(location != null){
                listener?.invoke(location!!,adress)
                dismiss()
            }else{
                Toast.makeText(requireContext(), getString(R.string.please_choose_location), Toast.LENGTH_SHORT).show()
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

    fun setOnClickListener(block: (LatLng,String) -> Unit){
        listener = block
    }

    override fun onDestroy() {
        super.onDestroy()
        listener?.invoke(location!!,adress)
        _bn = null
    }
}