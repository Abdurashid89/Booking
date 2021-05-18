package uz.koinot.stadion.data.repository

import okhttp3.MultipartBody
import uz.koinot.stadion.data.api.ApiService
import uz.koinot.stadion.data.model.CreateStadium
import uz.koinot.stadion.data.model.Login
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.room.OrderDao
import uz.koinot.stadion.data.storage.LocalStorage
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val api:ApiService,
    private val storage:LocalStorage,
    private val orderDao: OrderDao

) {

    suspend fun getStadium() = api.getStadium()
    suspend fun getOder(orderId:Int) = api.stadiumOrder(orderId)
    suspend fun accept(orderId:Int) = api.accept(orderId)
    suspend fun reject(orderId:Int) = api.reject(orderId)
    suspend fun dashboard(stadiumId:Int) = api.graph(stadiumId)
    suspend fun archiveAll(number:Int) = api.archiveAll(number)
    suspend fun archiveAfterCreateTime(number: Int,time:String) = api.archiveAfterCreateTime(number,time)
    suspend fun login(data:Login) = api.login(data)
    suspend fun attachment(id:Int,image: MultipartBody.Part) = api.uploadPhoto(id,image)
    suspend fun createStadium(data: CreateStadium) = api.createStadium(data)



    suspend fun setAllOrder(list: List<Order>) = orderDao.setAllOrder(list)
    suspend fun getAllOrder() = orderDao.getAllOrders()
    suspend fun removeAllOrder() = orderDao.removeAllOrders()
}