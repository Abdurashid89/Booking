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
    private lateinit var lan_lat: LatLng
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
        val arg = arguments?.getString(CONSTANTS.LOCATION)
        lan_lat = Gson().fromJson(arg, LatLng::class.java)
        adress = arguments?.getString(CONSTANTS.ADRESS, "Unknown adress").toString()
        Log.d("AAA", "log:${lan_lat.latitude} ${lan_lat.longitude} $adress")

        viewModel.newStadiumId()
        lifecycleScope.launchWhenCreated {
            viewModel.newStadiumIdFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        stadiumId = it.data
                    }
                    else -> Unit
                }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentCreateStadiumBinding.bind(view)

        bn.apply {
            layoutTimeOpen.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    timeOpen.setText("$hourOfDay:$minute")
                }, true).show(childFragmentManager, "qqqq")
            }
            layoutTimeClose.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    timeClose.setText("$hourOfDay:$minute")
                }, true).show(childFragmentManager, "zzz")
            }
            layoutStartNightTime.setOnClickListener {
                TimePickerDialog.newInstance({ _, hourOfDay, minute, _ ->
                    whenStartNightTime.setText("$hourOfDay:$minute")
                }, true).show(childFragmentManager, "mmm")
            }

            inputPhoneNumber.setText(storage.phoneNumber)
            location.setText(adress)
            addImage.setOnClickListener {
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
                    if (adapter.itemCount < 10) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, 1)
                    } else {
                        showMessage("belgilangan limitda rasm yulab bo'ldingiz")
                    }

                }
//                imagePicker()
            }
            rvImages.adapter = adapter

            btnAddStadium.setOnClickListener {
                val number = inputPhoneNumber.text.toString().trim()
                val location = location.text.toString().trim()
                val nameStadium = nameStadium.text.toString().trim()
                val timeOpen = timeOpen.text.toString().trim()
                val timeClose = timeClose.text.toString().trim()
                val priceDay = priceDay.text.toString().trim()
                val priceNight = priceNight.text.toString().trim()
                val timeNight = inputPhoneNumber.text.toString().trim()
                val widht = widthStadium.text.toString().trim()
                val height = heightStadium.text.toString().trim()
                if (number.isNotEmpty() && location.isNotEmpty() && nameStadium.isNotEmpty()
                    && timeOpen.isNotEmpty() && timeClose.isNotEmpty() && priceDay.isNotEmpty()
                    && priceNight.isNotEmpty() && timeNight.isNotEmpty() && widht.isNotEmpty() && height.isNotEmpty()
                ) {
                    viewModel.createStadium(
                        CreateStadium(
                            null,
                            nameStadium,
                            lan_lat.latitude,
                            number,
                            lan_lat.longitude,
                            location,
                            timeOpen,
                            timeClose,
                            timeNight,
                            priceDay.toInt(),
                            priceNight.toInt(),
                            false,
                            widht.toInt(),
                            height.toInt()
                        )
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.imageFlow.collect {
                when (it) {
                    is UiStateObject.SUCCESS -> {
                        adapter.addImage(currentUri!!)
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
                        stadiumId = it.data
                        showMessage("Successfully created")
                        findNavController().navigateUp()
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
            currentUri = data?.data ?: return
            val path = PathUtil.getPath(requireContext(), currentUri)
            viewModel.uploadImage(stadiumId, path)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}