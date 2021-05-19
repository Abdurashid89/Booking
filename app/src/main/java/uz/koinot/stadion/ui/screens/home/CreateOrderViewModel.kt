package uz.koinot.stadion.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.utils.UiStateList
import javax.inject.Inject

@HiltViewModel
class CreateOrderViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    private var _ordersFlow = MutableStateFlow<UiStateList<Order>>(UiStateList.EMPTY)
    val orderFlow: StateFlow<UiStateList<Order>> get() = _ordersFlow

    fun getOder(orderId: Int) = viewModelScope.launch {
        _ordersFlow.value = UiStateList.LOADING
        try {
            val res = repository.getOder(orderId)
            if (res.success == 200) {
                _ordersFlow.value = UiStateList.SUCCESS(res.objectKoinot)
            } else {
                _ordersFlow.value = UiStateList.ERROR(res.message)
            }
        } catch (e: Exception) {
            _ordersFlow.value = UiStateList.ERROR(e.localizedMessage)
            e.printStackTrace()
        }
    }
}