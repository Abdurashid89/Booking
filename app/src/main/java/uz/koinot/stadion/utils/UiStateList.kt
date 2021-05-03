package uz.koinot.stadion.utils

import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.model.Stadium

sealed class UiStateList<out T> {
    data class SUCCESS<out T>(val data: List<T>?) : UiStateList<T>()
    data class ERROR(val message: String, var fromServer: Boolean = false) : UiStateList<Nothing>()
    object LOADING : UiStateList<Nothing>()
    object EMPTY : UiStateList<Nothing>()
}