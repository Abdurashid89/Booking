package uz.koinot.stadion.ui.screens.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
//import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentCreateStadiumBinding.bind(view)

        bn.apply {
            inputPhoneNumber.setText(storage.phoneNumber)
            location.setText(adress)
            addImage.setOnClickListener {
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
                    if (img10 == null) {
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
                        when (adapter.itemCount) {
                            0 -> {
                                adapter.addImage(img1!!)
                                viewModel.uploadImage(stadiumId, PathUtil.getPath(requireContext(),img2!!))
                            }
                            1 -> {
                                adapter.addImage(img2!!)
                                viewModel.uploadImage(stadiumId, PathUtil.getPath(requireContext(),img3!!))
                            }
                            2 -> {
                                adapter.addImage(img3!!)
                                viewModel.uploadImage(stadiumId, PathUtil.getPath(requireContext(),img4!!))
                            }
                            3 -> {
                                adapter.addImage(img4!!)
                                viewModel.uploadImage(stadiumId, PathUtil.getPath(requireContext(),img5!!))
                            }
                            4 -> {
                                adapter.addImage(img5!!)
                                viewModel.uploadImage(stadiumId, PathUtil.getPath(requireContext(),img6!!))
                            }
                            5 -> {
                                adapter.addImage(img6!!)
                                viewModel.uploadImage(stadiumId, PathUtil.getPath(requireContext(),img7!!))
                            }
                            6 -> {
                                adapter.addImage(img7!!)
                                viewModel.uploadImage(stadiumId, PathUtil.getPath(requireContext(),img8!!))
                            }
                            7 -> {
                                adapter.addImage(img8!!)
                                viewModel.uploadImage(stadiumId, PathUtil.getPath(requireContext(),img9!!))
                            }
                            8 -> {
                                adapter.addImage(img9!!)
                                viewModel.uploadImage(stadiumId, PathUtil.getPath(requireContext(),img10!!))
                            }
                            9 -> {
                                adapter.addImage(img10!!)
                                showMessage("Successfully created stadium and uploaded images")
                            }
                            else -> Unit
                        }

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

    viewLifecycleOwner.lifecycleScope.launchWhenCreated{
        viewModel.createStadiumFlow.collect {
            when (it) {
                is UiStateObject.SUCCESS -> {
                    stadiumId = it.data
                    viewModel.uploadImage(stadiumId, PathUtil.getPath(requireContext(), img1))
                    showMessage("Successfully created")
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
        val uri = data?.data ?: return
        when {
            img1 == null -> {
                img1 = uri
            }
            img2 == null -> {
                img2 = uri
            }
            img3 == null -> {
                img3 = uri
            }
            img4 == null -> {
                img4 = uri
            }
            img5 == null -> {
                img5 = uri
            }
            img6 == null -> {
                img6 = uri
            }
            img7 == null -> {
                img7 = uri
            }
            img8 == null -> {
                img8 = uri
            }
            img9 == null -> {
                img9 = uri
            }
            img10 == null -> {
                img10 = uri
            }

            else -> Unit
        }
//            val path = PathUtil.getPath(requireContext(),uri)
//            viewModel.uploadImage(7,path)
    }
}

override fun onDestroy() {
    super.onDestroy()
    _bn = null
}
}