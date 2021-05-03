package uz.koinot.stadion.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.utils.UiStateList
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    private var _stadiumFlow = MutableStateFlow<UiStateList<Stadium>>(UiStateList.EMPTY)
    val stadiumFlow:StateFlow<UiStateList<Stadium>> get() = _stadiumFlow

    fun getAllStadium() = viewModelScope.launch {
        _stadiumFlow.value = UiStateList.LOADING
        try {
            val res = repository.getStadium()
            if(res.success == 200){
                _stadiumFlow.value = UiStateList.SUCCESS(res.objectKoinot)
            }else{
                _stadiumFlow.value = UiStateList.ERROR(res.message)
            }
        }catch (e:Exception){
            _stadiumFlow.value = UiStateList.ERROR(e.localizedMessage)
            e.printStackTrace()
        }
    }
}