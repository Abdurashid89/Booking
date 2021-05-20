package uz.koinot.stadion.ui.screens.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
//import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.R
import uz.koinot.stadion.adapter.AttachmentAdapter
import uz.koinot.stadion.data.model.CreateStadium
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentCreateStadiumBinding
import uz.koinot.stadion.utils.*
import javax.inject.Inject


@AndroidEntryPoint
class CreateStadiumFragment : Fragment(R.layout.fragment_create_stadium) {

    private var _bn: FragmentCreateStadiumBinding? = null
    val bn get() = _bn!!
    private var adress = ""
    private var stadiumId = 0
    private var type = false
    private lateinit var lan_lat: LatLng
    private lateinit var stadium: Stadium
    private val viewModel: CreateStadiumViewModel by viewModels()
    private val adapter = AttachmentAdapter()

    @Inject
    lateinit var storage: LocalStorage

    private var currentUri: Uri? = null
    private var img1: Uri? = null
    private var img2: Uri? = null
    private var img3: Uri? = null
    private var img4: Uri? = null
    private var img5: Uri? = null
    private var img6: Uri? = null
    private var img7: Uri? = null
    private var img8: Uri? = null
    private var img9: Uri? = null
    private var img10: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            if (arguments?.getString(CONSTANTS.STADIUM_TYPE) == CONSTANTS.NEW_STADIUM) {
                type = true
                val arg = arguments?.getString(CONSTANTS.LOCATION)
                lan_lat = Gson().fromJson(arg, LatLng::class.java)
                adress = arguments?.getString(CONSTANTS.ADRESS, "Unknown adress").toString()
                Log.d("AAA", "log:${lan_lat.latitude} ${lan_lat.longitude} $adress")
            } else {
                type = false
                val arg = arguments?.getString(CONSTANTS.STADIUM_DATA)
                stadium = Gson().fromJson(arg, Stadium::class.java)
            }
        }


    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentCreateStadiumBinding.bind(view)

        bn.apply {
            if (type) {
                inputPhoneNumber.setText(storage.phoneNumber)
                location.setText(adress)
            } else {
                inputPhoneNumber.setText(stadium.phone_number)
                location.setText(stadium.address)
                nameStadium.setText(stadium.name)
                timeOpen.setText(stadium.opening_time)
                timeClose.setText(stadium.closing_time)
                priceDay.setText(stadium.price_day_time.toString())
                priceNight.setText(stadium.price_night_time.toString())
                whenStartNightTime.setText(stadium.change_price_time)
                widthStadium.setText(stadium.width.toString())
                heightStadium.setText(stadium.height.toString())
            }

            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            timeOpen.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    timeOpen.setText("${hourOfDay.getString()}:${minute.getString()}")
                }, true).show(childFragmentManager, "qqqq")
            }
            timeClose.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    timeClose.setText("${hourOfDay.getString()}:${minute.getString()}")
                }, true).show(childFragmentManager, "zzz")
            }
            whenStartNightTime.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    whenStartNightTime.setText("${hourOfDay.getString()}:${minute.getString()}")
                }, true).show(childFragmentManager, "mmm")
            }


//            addImage.setOnClickListener {
//                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
//                    if (adapter.itemCount < 10) {
//                        val intent = Intent(Intent.ACTION_PICK)
//                        intent.type = "image/*"
//                        startActivityForResult(intent, 1)
//                    } else {
//                        showMessage("belgilangan limitda rasm yulab bo'ldingiz")
//                    }
//
//                }
////                imagePicker()
//            }
//            rvImages.adapter = adapter

            btnAddStadium.setOnClickListener {
                val number = inputPhoneNumber.text.toString().trim()
                val location = location.text.toString().trim()
                val nameStadium = nameStadium.text.toString().trim()
                val timeOpen = timeOpen.text.toString().trim()
                val timeClose = timeClose.text.toString().trim()
                val priceDay = priceDay.text.toString().trim()
                val priceNight = priceNight.text.toString().trim()
                val timeNight = whenStartNightTime.text.toString().trim()
                val widht = widthStadium.text.toString().trim()
                val height = heightStadium.text.toString().trim()
                if (number.isNotEmpty() && location.isNotEmpty() && nameStadium.isNotEmpty()
                    && timeOpen.isNotEmpty() && timeClose.isNotEmpty() && priceDay.isNotEmpty()
                    && priceNight.isNotEmpty() && timeNight.isNotEmpty() && widht.isNotEmpty() && height.isNotEmpty())
                    { if (type) {
                        viewModel.createStadium(
                            CreateStadium(null, nameStadium, lan_lat.latitude, number, lan_lat.longitude, location, timeOpen.getTimeStamp(), timeClose.getTimeStamp(), timeNight.getTimeStamp(), priceDay.toDouble(), priceNight.toDouble(), true, widht.toInt(), height.toInt()))
                    } else
                        viewModel.createStadium(
                            CreateStadium(stadium.id, nameStadium, stadium.latitude, number, stadium.longitude, location, timeOpen, timeClose, timeNight, priceDay.toDouble(), priceNight.toDouble(), true, widht.toInt(), height.toInt()))
                }else{
                    showMessage("Wrong")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.imageFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        showMessage("Stadium successfully created!")
                        findNavController().popBackStack(R.id.homeFragment, false)
                    }
                    is UiStateObject.ERROR -> {
                        showMessage("error upload")
                    }
                    is UiStateObject.LOADING -> {

                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.createStadiumFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {

                        showMessage("Successfully!")
                        findNavController().popBackStack(R.id.homeFragment, false)
//                        stadiumId = it.data
//                        showMessage("Successfully created")
//                        val ls = adapter.getList()
//                        val list = ArrayList<String>()
//                        ls.forEach {
//                            list.add(PathUtil.getPath(requireContext(), it))
//                        }
//                        viewModel.uploadImage(stadiumId, list)
//                        findNavController().popBackStack(R.id.homeFragment,false)
                    }
                    is UiStateObject.ERROR -> {
                        showMessage("Error not created")
                    }
                    is UiStateObject.LOADING -> {

                    }
                    else -> Unit
                }
            }
        }

    }

//    private fun imagePicker(){
//        ImagePicker.with(this)
//            .crop()
//            .compress(1024)
//            .maxResultSize(1080,1080)
//            .start()
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
//            currentUri = data?.data ?: return
//            adapter.addImage(currentUri!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}