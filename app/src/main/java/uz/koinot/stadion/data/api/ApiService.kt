package uz.koinot.stadion.data.api

import okhttp3.MultipartBody
import retrofit2.http.*
import retrofit2.http.Body
import uz.koinot.stadion.data.model.*

interface ApiService {

    @POST("koinot/auth/register")
    suspend fun auth(@Body data:Register):ResponseRegister

    @POST("koinot/auth/login")
    suspend fun login(@Body data:Login):ResponseRegister


    @POST("koinot/auth/verify/{number}")
    suspend fun verify(@Path("number") number:String):ResponseObject<Any>

    @GET("koinot/stadium/myStadium")
    suspend fun getStadium() : ResponseList<Stadium>

    @GET("koinot/stadium/myStadiumOrder/{order}")
    suspend fun stadiumOrder(@Path("order") order : Int) : ResponseList<Order>


    @GET("koinot/stadium/activeOrder/{number}")
    suspend fun accept(@Path("number") number:Int):ResponseObject<Any>

    @GET("koinot/stadium/cancelOrder/{number}")
    suspend fun reject(@Path("number") number:Int):ResponseObject<Any>

    @GET("koinot/stadiumDash/graph/{number}")
    suspend fun graph(@Path("number") number:Int):ResponseList<Dashboard>

    @POST("koinot/auth/firebase/{token}")
    suspend fun token(@Path("token") token:String):ResponseList<Any>

    @GET("koinot/stadiumDash/archiveAll/{number}")
    suspend fun archiveAll(@Path("number") number: Int):ResponseList<Order>

    @GET("koinot/stadiumDash/archiveAfterCreateTime/{number}")
    suspend fun archiveAfterCreateTime(
        @Path("number") number: Int,
        @Query("time") time: String
    ):ResponseList<Order>

    @Multipart
    @POST("koinot/stadium/uploadPhotoFile/{id}")
    suspend fun uploadPhoto(
        @Path("id") id:Int,
        @Part image: MultipartBody.Part
        ): ResponseObject<Any>


    @POST("koinot/stadium/saveOrEdit")
    suspend fun createStadium(@Body data:CreateStadium):ResponseObject<Any>




}