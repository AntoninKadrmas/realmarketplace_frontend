package com.realmarketplace.rest

import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.LightUser
import com.realmarketplace.model.UserModel
import retrofit2.Response
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface AuthService {
    @POST(value="/user/register")
    suspend fun registerUser(@Body user: UserModel):Response<Any>
    @POST(value="/user/login")
    suspend fun loginUser(
        @Header("Authorization") credential:String):Response<Any>
    @Multipart
    @POST(value="/user/image")
    suspend fun uploadProfileImage(
        @Part body:MultipartBody.Part,
        @Part("oldUrl")  deletedUrls: RequestBody,
        @Header("Authentication") token:String): Response<Any>
    @GET(value="/user")
    suspend fun getMyUser(
        @Header("Authentication") token: String):Response<Any>
    @POST(value="/user")
    suspend fun updateUserPassword(
        @Header("Authorization") credential:String,
        @Header("Authentication") token:String):Response<Any>
    @PUT(value="/user")
    suspend fun updateUser(
        @Body body: LightUser,
        @Header("Authentication") token:String):Response<Any>
    @DELETE(value="/user")
    suspend fun deleteUser(
        @Header("Authorization") credential:String,
        @Header("Authentication") token:String):Response<Any>
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