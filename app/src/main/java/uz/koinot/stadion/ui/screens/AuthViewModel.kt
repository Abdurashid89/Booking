package uz.koinot.stadion.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.model.Register
import uz.koinot.stadion.data.model.TokenBody
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.utils.UiStateList
import uz.koinot.stadion.utils.UiStateObject
import uz.koinot.stadion.utils.userMessage
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    private var _registerFlow = MutableStateFlow<UiStateObject<TokenBody>>(UiStateObject.EMPTY)
    val registerFlow: StateFlow<UiStateObject<TokenBody>> get() = _registerFlow

    fun register(data:Register) = viewModelScope.launch {
        _registerFlow.value = UiStateObject.LOADING
        try {
            val res = repository.register(data)
            if(res.success == 200){
                _registerFlow.value = UiStateObject.SUCCESS(res.objectKoinot!!)
            }else{
                _registerFlow.value = UiStateObject.ERROR(res.message)
            }
        }catch (e:Exception){
            _registerFlow.value = UiStateObject.ERROR(e.userMessage()?:"not found")
            e.printStackTrace()
        }
    }
    fun reRegister(){
        _registerFlow.value = UiStateObject.EMPTY
    }

    private var _acceptFlow = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val acceptFlow: StateFlow<UiStateObject<String>> get() = _acceptFlow

    fun acceptOrder(orderId:Long) = viewModelScope.launch {
        _acceptFlow.value = UiStateObject.LOADING
        try {
            val res = repository.accept(orderId)
            if(res.success == 200){
                _acceptFlow.value = UiStateObject.SUCCESS("Success")
            }else{
                _acceptFlow.value = UiStateObject.ERROR(res.message)
            }
        }catch (e:Exception){
            _acceptFlow.value = UiStateObject.ERROR(e.userMessage()?:"not found")
            e.printStackTrace()
        }
    }


    private var _rejectFlow = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val rejectFlow: StateFlow<UiStateObject<String>> get() = _rejectFlow

    fun rejectOrder(orderId:Long) = viewModelScope.launch {
        _rejectFlow.value = UiStateObject.LOADING
        try {
            val res = repository.reject(orderId)
            if(res.success == 200){
                _rejectFlow.value = UiStateObject.SUCCESS("Success")
            }else{
                _rejectFlow.value = UiStateObject.ERROR(res.message)
            }
        }catch (e:Exception){
            _rejectFlow.value = UiStateObject.ERROR(e.userMessage()?:"not found")
            e.printStackTrace()
        }
    }



}