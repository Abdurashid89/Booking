package uz.koinot.stadion.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.model.Login
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.data.storage.LocalStorage
import uz.koinot.stadion.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: MainRepository,
    private val storage: LocalStorage
): ViewModel() {

    private var _loginFlow = MutableStateFlow<UiStateObject<String>>(UiStateObject.EMPTY)
    val loginFlow: StateFlow<UiStateObject<String>> get() = _loginFlow

    fun login(data: Login) = viewModelScope.launch {
        _loginFlow.value = UiStateObject.LOADING
        try {
            val res = repository.login(data)
            if (res.success == 200) {
                storage.accessToken = res.objectKoinot!!.accessToken
                _loginFlow.value = UiStateObject.SUCCESS(res.message)
            } else {
                _loginFlow.value = UiStateObject.ERROR(res.message,true)
            }
        } catch (e: Exception) {
            _loginFlow.value = UiStateObject.ERROR(e.localizedMessage)
            e.printStackTrace()
        }
    }
}