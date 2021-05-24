package uz.koinot.stadion.data.api

import okhttp3.MultipartBody
import retrofit2.http.*
import retrofit2.http.Body
import uz.koinot.stadion.data.model.*

interface ApiService {

    @POST("koinot/auth/register")
    suspend fun auth(@Body data:Register):ResponseObject<TokenBody>

    @POST("koinot/auth/login")
    suspend fun login(@Body data:Login):ResponseObject<TokenBody>


    @POST("koinot/auth/verify/{number}")
    suspend fun verify(@Path("number") number:String):ResponseObject<Any>

    @GET("koinot/stadium/myStadium")
    suspend fun getStadium() : ResponseList<Stadium>

    @GET("koinot/stadium/myStadiumOrder/{order}")
    suspend fun stadiumOrder(@Path("order") order : Long) : ResponseList<Order>


    @GET("koinot/stadium/activeOrder/{number}")
    suspend fun accept(@Path("number") number:Int):ResponseObject<Any>

    @GET("koinot/stadium/cancelOrder/{number}")
    suspend fun reject(@Path("number") number:Int):ResponseObject<Any>

    @GET("koinot/stadiumDash/graph/{number}")
    suspend fun graph(@Path("number") number:Long):ResponseList<Dashboard>

    @POST("koinot/auth/firebase/{token}")
    suspend fun token(@Path("token") token:String):ResponseList<Any>

    @GET("koinot/stadiumDash/archiveAll/{number}")
    suspend fun archiveAll(@Path("number") number: Long):ResponseList<Order>

    @GET("koinot/stadiumDash/archiveAfterCreateTime/{number}")
    suspend fun archiveAfterCreateTime(
        @Path("number") number: Int,
        @Query("time") time: String
    ):ResponseList<Order>

    @Multipart
    @POST("koinot/stadium/uploadPhotoFileList/{id}")
    suspend fun uploadPhoto(
        @Path("id") id:Long,
        @Part image: List<MultipartBody.Part>
        ): ResponseObject<Any>


    @POST("koinot/stadium/saveOrEdit")
    suspend fun createStadium(@Body data:CreateStadium):ResponseObject<Int>

    @POST("koinot/order/adminCreateOrder")
    suspend fun createOrder(@Body data:CreateOrder):ResponseObject<Any>

    @GET("koinot/order/searchNumber/{number}")
    suspend fun search(@Path("number") number: String):ResponseList<SearchUser>

    @GET("koinot/order/searchNumber")
    suspend fun getNewStadiumId():ResponseObject<Int>

    @GET("koinot/order/{id}/{startDate}/{endDate}")
    suspend fun orderPrice(
        @Path("id") id: Long,
        @Path("startDate") startDate: String,
        @Path("endDate") endDate: String
    ):ResponseObject<Double>

    @POST("koinot/stadium/delete/{id}")
    suspend fun deleteStadium(@Path("id") id:Long):ResponseObject<Any>
}