package uz.koinot.stadion.data.repository

import okhttp3.MultipartBody
import uz.koinot.stadion.data.api.ApiService
import uz.koinot.stadion.data.model.CreateOrder
import uz.koinot.stadion.data.model.CreateStadium
import uz.koinot.stadion.data.model.Login
import uz.koinot.stadion.data.model.Order
import uz.koinot.stadion.data.room.OrderDao
import uz.koinot.stadion.data.storage.LocalStorage
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val api:ApiService,
    private val orderDao: OrderDao
) {

    fun setAllOrder(list: List<Order>) = orderDao.setAllOrder(list)
    fun getAllOrder() = orderDao.getAllOrders()
    fun removeAllOrder() = orderDao.removeAllOrders()

    suspend fun getStadium() = api.getStadium()
    suspend fun getOder(orderId:Long) = api.stadiumOrder(orderId)
    suspend fun accept(orderId:Int) = api.accept(orderId)
    suspend fun reject(orderId:Int) = api.reject(orderId)
    suspend fun dashboard(stadiumId:Long) = api.graph(stadiumId)
    suspend fun archiveAll(number:Long) = api.archiveAll(number)
    suspend fun archiveAfterCreateTime(number: Int,time:String) = api.archiveAfterCreateTime(number,time)
    suspend fun login(data:Login) = api.login(data)
    suspend fun attachment(id:Long,image: MultipartBody.Part) = api.uploadPhoto(id, listOf(image))
    suspend fun createStadium(data: CreateStadium) = api.createStadium(data)
    suspend fun createOrder(data: CreateOrder) = api.createOrder(data)
    suspend fun searchUser(data: String) = api.search(data)
    suspend fun getNewStadiumId() = api.getNewStadiumId()
    suspend fun orderPrice(id:Int, startDate:String,endDate:String) = api.orderPrice(id, startDate, endDate)




}