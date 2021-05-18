package uz.koinot.stadion.ui.screens.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.koinot.stadion.R
import uz.koinot.stadion.data.model.CreateStadium
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.databinding.FragmentCreateStadiumBinding
import uz.koinot.stadion.utils.CONSTANTS
import uz.koinot.stadion.utils.PathUtil
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.showMessage
import javax.inject.Inject


@AndroidEntryPoint
class CreateStadiumFragment : Fragment(R.layout.fragment_create_stadium) {

    private var _bn: FragmentCreateStadiumBinding? = null
    val bn get() = _bn!!
    private var adress = ""
    private lateinit var lan_lat : LatLng
    private val viewModel: CreateStadiumViewModel by viewModels()

    @Inject
    lateinit var storage: LocalStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arg = arguments?.getString(CONSTANTS.LOCATION)
        lan_lat = Gson().fromJson(arg, object : TypeToken<LatLng>(){}.type)
        adress = arguments?.getString(CONSTANTS.ADRESS,"Unknown adress").toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _bn = FragmentCreateStadiumBinding.bind(view)

        bn.apply {
            inputPhoneNumber.setText(storage.phoneNumber)
            location.setText(adress)
            addImage.setOnClickListener {
                imagePicker()
            }
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
                if(number.isNotEmpty() && location.isNotEmpty()&& nameStadium.isNotEmpty()
                    && timeOpen.isNotEmpty()    && timeClose.isNotEmpty()    && priceDay.isNotEmpty()
                    && priceNight.isNotEmpty()    && timeNight.isNotEmpty()    && widht.isNotEmpty() && height.isNotEmpty()
                ){
                    viewModel.createStadium(CreateStadium(7,nameStadium,lan_lat.latitude,number,lan_lat.longitude,
                    location,timeOpen,timeClose,timeNight,priceDay.toInt(),priceNight.toInt(),false,widht.toInt(),height.toInt()))
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.imageFlow.collect {
                when(it){
                    is UiStateObject.SUCCESS ->{
                        showMessage("Success upload")
                    }
                    is UiStateObject.ERROR ->{
                        showMessage("error upload")

                    }
                    is UiStateObject.LOADING ->{

                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.createStadiumFlow.collect {
                when(it){
                    is UiStateObject.SUCCESS ->{
                        showMessage("Successfully created")
                    }
                    is UiStateObject.ERROR ->{
                        showMessage("Error not created")
                    }
                    is UiStateObject.LOADING ->{

                    }
                    else -> Unit
                }
            }
        }

    }

    private fun imagePicker(){
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080,1080)
            .start()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            val uri = data?.data ?: return
            val path = PathUtil.getPath(requireContext(),uri)
            viewModel.uploadImage(7,path)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _bn = null
    }
}