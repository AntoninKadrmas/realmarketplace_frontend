package com.example.omega1.rest

import com.example.omega1.model.UserModel
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Query
import com.google.gson.annotations.SerializedName
import retrofit2.http.Header

interface AuthService {
    @POST(value="/user/register")
    suspend fun registerUser(@Body user:UserModel):Response<Any>
    @POST(value="/user/login")
    suspend fun loginUser(
        @Header("Authorization") credential:String):Response<Any>
}

data class ReturnTypeError(
    @SerializedName("error")
    val error:String
)
data class ReturnTypeSuccess(
    @SerializedName("error")
    val success:String
)