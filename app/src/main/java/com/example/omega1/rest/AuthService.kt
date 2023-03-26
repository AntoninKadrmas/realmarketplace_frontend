package com.example.omega1.rest

import com.example.omega1.model.AdvertModel
import com.example.omega1.model.UserModel
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Body
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
    @SerializedName("success")
    val success:String
)
data class ReturnTypeSuccessAdvert(
    @SerializedName("success")
    val success:String,
    @SerializedName("advert")
    val advert: AdvertModel
)
data class ReturnTypeSuccessUris(
    @SerializedName("success")
    val success:String,
    @SerializedName("imageUrls")
    val imageUrls: ArrayList<String>
)