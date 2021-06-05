package uz.koinot.stadion.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import uz.koinot.stadion.data.model.Login
import uz.koinot.stadion.data.model.Register
import uz.koinot.stadion.data.model.ResponseObject
import uz.koinot.stadion.data.model.TokenBody

interface AuthService {

    @POST("koinot/auth/register")
    suspend fun auth(@Body data: Register): ResponseObject<TokenBody>

    @POST("koinot/auth/login")
    suspend fun login(@Body data: Login): ResponseObject<TokenBody>
}