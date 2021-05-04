package uz.koinot.stadion.data.repository

import uz.koinot.stadion.data.api.ApiService
import uz.koinot.stadion.data.storage.LocalStorage
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val api:ApiService,
    private val storage:LocalStorage
) {

    suspend fun getStadium() = api.getStadium()
    suspend fun getOder(orderId:Int) = api.stadiumOrder(orderId)
    suspend fun accept(orderId:Int) = api.accept(orderId)
    suspend fun reject(orderId:Int) = api.reject(orderId)
    suspend fun dashboard(stadiumId:Int) = api.graph(stadiumId)
}