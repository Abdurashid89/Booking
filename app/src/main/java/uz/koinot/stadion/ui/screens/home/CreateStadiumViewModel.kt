package uz.koinot.stadion.ui.screens.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import uz.koinot.stadion.data.model.CreateStadium
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.utils.PathUtil
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.UiStateObject
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class CreateStadiumViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    private var _createStadiumFlow = MutableStateFlow<UiStateObject<Int>>(UiStateObject.EMPTY)
    val createStadiumFlow: StateFlow<UiStateObject<Int>> get() = _createStadiumFlow

    fun createStadium(data: CreateStadium) = viewModelScope.launch {
        _createStadiumFlow.value = UiStateObject.LOADING
        try {
            val res = repository.createStadium(data)

            if(res.success == 200 && res.objectKoinot != null){
                _createStadiumFlow.value = UiStateObject.SUCCESS(res.objectKoinot!!)
            }else{
                _createStadiumFlow.value = UiStateObject.ERROR(res.message,true)
            }
        }catch (e:Exception){
            _createStadiumFlow.value = UiStateObject.ERROR(e.localizedMessage?:"not found")
            e.printStackTrace()
        }
    }

}