package uz.koinot.stadion.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.model.Dashboard
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.model.Stadium
import uz.koinot.stadion.data.repository.MainRepository
import uz.koinot.stadion.utils.UiStateList
import javax.inject.Inject


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: MainRepository
): ViewModel() {

    private var _dashboardFlow = MutableStateFlow<UiStateList<Dashboard>>(UiStateList.EMPTY)
    val dashboardFlow: StateFlow<UiStateList<Dashboard>> get() = _dashboardFlow

    fun getDashboard(stadiumId:Long) = viewModelScope.launch {
        _dashboardFlow.value = UiStateList.LOADING
        try {
            val res = repository.dashboard(stadiumId)
            if(res.success == 200){
                _dashboardFlow.value = UiStateList.SUCCESS(res.objectKoinot)
            }else{
                _dashboardFlow.value = UiStateList.ERROR(res.message)
            }
        }catch (e:Exception){
            _dashboardFlow.value = UiStateList.ERROR(e.localizedMessage?:"not found")
            e.printStackTrace()
        }
    }

    private var _archiveAllFlow = MutableStateFlow<UiStateList<Order>>(UiStateList.EMPTY)
    val archiveAllFlow: StateFlow<UiStateList<Order>> get() = _archiveAllFlow

    fun archiveAll(number:Long) = viewModelScope.launch {
        _archiveAllFlow.value = UiStateList.LOADING
        try {
            val res = repository.archiveAll(number)
            if(res.success == 200){
                _archiveAllFlow.value = UiStateList.SUCCESS(res.objectKoinot)
            }else{
                _archiveAllFlow.value = UiStateList.ERROR(res.message)
            }
        }catch (e:Exception){
            _archiveAllFlow.value = UiStateList.ERROR(e.localizedMessage?:"not found")
            e.printStackTrace()
        }
    }

    private var _afterCreateFlow = MutableStateFlow<UiStateList<Order>>(UiStateList.EMPTY)
    val afterCreateFlow: StateFlow<UiStateList<Order>> get() = _afterCreateFlow

    fun afterCreateFlow(number:Int,time:String) = viewModelScope.launch {
        _afterCreateFlow.value = UiStateList.LOADING
        try {
            val res = repository.archiveAfterCreateTime(number,time)
            if(res.success == 200){
                _afterCreateFlow.value = UiStateList.SUCCESS(res.objectKoinot)
            }else{
                _afterCreateFlow.value = UiStateList.ERROR(res.message)
            }
        }catch (e:Exception){
            _afterCreateFlow.value = UiStateList.ERROR(e.localizedMessage?:"not found")
            e.printStackTrace()
        }
    }

    suspend fun setAllOrder(list: List<Order>) = repository.setAllOrder(list)
    suspend fun getAllOrder() = repository.getAllOrder()
    suspend fun removeAllOrder() = repository.removeAllOrder()


}