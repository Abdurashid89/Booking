package uz.koinot.stadion.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import uz.koinot.stadion.data.model.*

interface ApiService {

    @POST("koinot/auth/register")
    suspend fun auth(@Body data:Register):ResponseRegister


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

    @GET("koinot/auth/firebase/{token}")
    suspend fun token(@Path("token") token:String):ResponseList<Any>

}