package uz.koinot.stadion.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import uz.koinot.stadion.data.model.*

interface AuthService {

    @POST("koinot/auth/register")
    suspend fun auth(@Body data: Register): ResponseObject<TokenBody>

    @POST("koinot/auth/login")
    suspend fun login(@Body data: Login): ResponseObject<TokenBody>

    @GET("koinot/auth/isBrbtStart/{number}")
    suspend fun isBotStarted(@Path("number") number: String): ResponseObject<Boolean>
}