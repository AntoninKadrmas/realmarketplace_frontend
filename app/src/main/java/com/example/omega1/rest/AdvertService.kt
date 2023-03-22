package com.example.omega1.rest

import com.example.omega1.model.AdvertModel
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface AdvertService {
    @Multipart
    @POST(value="/advert/create")
    suspend fun createAdvert(
        @Part body:ArrayList<MultipartBody.Part>,
        @Part("title")  advertTitle:RequestBody,
        @Part("description")  advertDescription:RequestBody,
        @Part("genreName")  advertGenreName:RequestBody,
        @Part("genreType")  advertGenreType:RequestBody,
        @Part("price")  advertPriceValue:RequestBody,
        @Part("priceOption")  advertPriceType:RequestBody,
        @Part("condition")  advertCondition:RequestBody,
        @Header("AuthToken") token:String): Response<Any>
    @GET(value = "/advert/all")
    suspend fun getAdvert():Response<ReturnListAdvertModel>
}
class ReturnListAdvertModel: ArrayList<AdvertModel>()