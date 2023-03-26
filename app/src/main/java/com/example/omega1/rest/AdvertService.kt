package com.example.omega1.rest

import com.example.omega1.model.AdvertModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Query

interface AdvertService {
    @Multipart
    @POST(value="/advert")
    suspend fun createAdvert(
        @Part body:ArrayList<MultipartBody.Part>,
        @Part("title")  advertTitle:RequestBody,
        @Part("author")  advertAuthor:RequestBody,
        @Part("description")  advertDescription:RequestBody,
        @Part("genreName")  advertGenreName:RequestBody,
        @Part("genreType")  advertGenreType:RequestBody,
        @Part("price")  advertPriceValue:RequestBody,
        @Part("priceOption")  advertPriceType:RequestBody,
        @Part("condition")  advertCondition:RequestBody,
        @Header("Authorization") token:String): Response<Any>
    @DELETE(value="/advert")
    suspend fun deleteAdvert(
        @Query("advertId") advertId:String,
        @Header("Authorization") token:String): Response<Any>
    @Multipart
    @PUT(value="/advert")
    suspend fun updateAdvert(
        @Part body:ArrayList<MultipartBody.Part>,
        @Part("_id")  advertId:RequestBody,
        @Part("title")  advertTitle:RequestBody,
        @Part("author")  advertAuthor:RequestBody,
        @Part("description")  advertDescription:RequestBody,
        @Part("genreName")  advertGenreName:RequestBody,
        @Part("genreType")  advertGenreType:RequestBody,
        @Part("price")  advertPriceValue:RequestBody,
        @Part("priceOption")  advertPriceType:RequestBody,
        @Part("condition")  advertCondition:RequestBody,
        @Part("mainImage")  mainImage:RequestBody,
        @Part("imageUrls")  imageUrls:RequestBody,
        @Part("deletedUrls")  deletedUrls:RequestBody,
        @Header("Authorization") token:String): Response<Any>
    @POST(value="/advert/favorite")
    suspend fun addFavorite(
        @Query("advertId") advertId:String,
        @Header("Authorization") token:String): Response<Any>
    @GET(value = "/advert/all")
    suspend fun getAdvert():Response<ReturnListAdvertModel>
    @GET(value="/advert")
    suspend fun getUserAdvert(
        @Header("Authorization") token:String
    ):Response<ReturnListAdvertModel>
}
class ReturnListAdvertModel: ArrayList<AdvertModel>()