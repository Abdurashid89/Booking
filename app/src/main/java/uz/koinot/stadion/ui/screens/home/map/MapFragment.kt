package uz.koinot.stadion.ui.screens.home.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.Task
import uz.koinot.stadion.R
import uz.koinot.stadion.databinding.FragmentMapBinding
import uz.koinot.stadion.utils.*
import uz.koinot.stadion.utils.extensions.getCompleteAddressString
import uz.koinot.stadion.utils.gps.GPSTracker

class MapFragment : DialogFragment() {

    private var _bn: FragmentMapBinding? = null
    private val bn get() = _bn!!
    private var mMap:GoogleMap? = null
    private var location:LatLng? = null
    private var marker: Marker? = null
    private lateinit var tracker: GPSTracker
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

                if(tracker.canGetLocation()) {
                    location = LatLng(tracker.getLatitude(),tracker.getLongitude())
                    adress = getCompleteAddressString(location!!.latitude, location!!.longitude).toString()
                    bn.textManzil.text = adress
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location!!, 13f))

                    mMap?.setOnCameraMoveStartedListener {
                        bn.shadow.isVisible = true
                        bn.imageMarker.animate()
                            .translationY( -75f)
                            .setInterpolator(OvershootInterpolator())
                            .setDuration(250)
                            .start()
                    }


                    mMap?.setOnCameraIdleListener {
                        bn.shadow.isVisible = false
                        bn.imageMarker.animate()
                            .translationY( 0f)
                            .setInterpolator(OvershootInterpolator())
                            .setDuration(250)
                            .start()

                        location = mMap?.cameraPosition?.target
                        adress = getCompleteAddressString(location!!.latitude, location!!.longitude).toString()
                        bn.textManzil.text = adress
                    }

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

        bn.btnZoomIncrement.setOnClickListener {
            var zoom = mMap?.cameraPosition?.zoom!!
            mMap?.animateCamera(CameraUpdateFactory.zoomTo(++zoom))
        }

        bn.btnZoomDecriment.setOnClickListener {
            var zoom = mMap?.cameraPosition?.zoom!!
            mMap?.animateCamera(CameraUpdateFactory.zoomTo(--zoom))
        }

        bn.btnMyLocation.setOnClickListener {
            location = LatLng(tracker.getLatitude(),tracker.getLongitude())
            marker?.apply {
                position = location!!
                isVisible = true
            }
            adress = getCompleteAddressString(location!!.latitude, location!!.longitude).toString()
            bn.textManzil.text = adress
            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(tracker.getLatitude(),tracker.getLongitude()),13f))
        }

        bn.btnChooseLocation.setOnClickListener {
            if(location != null){
                listener?.invoke(location!!,adress)
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
        listener?.invoke(location!!,adress)
        _bn = null
        super.onDestroy()
    }
}