package uz.koinot.stadion.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.koinot.stadion.data.model.Dashboard
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

    fun getDashboard(stadiumId:Int) = viewModelScope.launch {
        _dashboardFlow.value = UiStateList.LOADING
        try {
            val res = repository.dashboard(stadiumId)
            if(res.success == 200){
                _dashboardFlow.value = UiStateList.SUCCESS(res.objectKoinot)
            }else{
                _dashboardFlow.value = UiStateList.ERROR(res.message)
            }
        }catch (e:Exception){
            _dashboardFlow.value = UiStateList.ERROR(e.localizedMessage)
            e.printStackTrace()
        }
    }
}