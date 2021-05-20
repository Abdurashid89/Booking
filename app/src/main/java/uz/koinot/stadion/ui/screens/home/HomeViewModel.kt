package uz.koinot.stadion.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.UiStateObject
import java.io.File
import javax.inject.Inject
import kotlin.random.Random


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private var _stadiumFlow = MutableStateFlow<UiStateList<Stadium>>(UiStateList.EMPTY)
    val stadiumFlow: StateFlow<UiStateList<Stadium>> get() = _stadiumFlow

    fun getAllStadium() = viewModelScope.launch {
        _stadiumFlow.value = UiStateList.LOADING
        try {
            val res = repository.getStadium()
            if (res.success == 200) {
                _stadiumFlow.value = UiStateList.SUCCESS(res.objectKoinot)
            } else {
                _stadiumFlow.value = UiStateList.ERROR(res.message, true, res.success)
            }
        } catch (e: Exception) {
            _stadiumFlow.value = UiStateList.ERROR(e.localizedMessage)
            e.printStackTrace()
        }
    }

    private var _imageFlow = MutableStateFlow<UiStateObject<Boolean>>(UiStateObject.EMPTY)
    val imageFlow: StateFlow<UiStateObject<Boolean>> get() = _imageFlow

    fun uploadImage(id: Int, path: String) = viewModelScope.launch {
        _imageFlow.value = UiStateObject.LOADING
        try {
            val file = File(path)
            val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
            val image = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val res = repository.attachment(id, image)
            if (res.success == 200) {
                _imageFlow.value = UiStateObject.SUCCESS(true)
            } else {
                _imageFlow.value = UiStateObject.ERROR(res.message, true)
            }
        } catch (e: Exception) {
            _imageFlow.value = UiStateObject.ERROR(e.localizedMessage)
        }
    }
}