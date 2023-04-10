package com.realmarketplace.rest

import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.FavoriteAdvertModel
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
        @Header("Authentication") token:String): Response<Any>
    @DELETE(value="/advert")
    suspend fun deleteAdvert(
        @Query("advertId") advertId:String,
        @Header("Authentication") token:String): Response<Any>
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
        @Part("mainImageUrl")  mainImageUrl:RequestBody,
        @Part("imagesUrls")  imageUrls:RequestBody,
        @Part("deletedUrls")  deletedUrls:RequestBody,
        @Header("Authentication") token:String): Response<Any>
    @POST(value="/advert/favorite")
    suspend fun addFavorite(
        @Query("advertId") advertId:String,
        @Header("Authentication") token:String): Response<Any>
    @DELETE(value="/advert/favorite")
    suspend fun deleteFavorite(
        @Query("advertId") advertId:String,
        @Header("Authentication") token:String): Response<Any>
    @GET(value="/advert/favorite")
    suspend fun getFavorite(
        @Header("Authentication") token:String): Response<Any>
    @GET(value = "/advert/all")
    suspend fun getAdvert(
        @Header("Authentication") token:String):Response<ReturnListAdvertModel>
    @GET(value="/advert")
    suspend fun getUserAdvert(
        @Header("Authentication") token:String
    ):Response<ReturnListAdvertModel>
    @GET(value="/advert")
    suspend fun getUserPublicAdvert(
        @Header("userEmail") email:String,
        @Header("createdIn") createdIn:String,
        @Header("Authentication") token:String
    ):Response<ReturnListAdvertModel>
    @PUT(value="/advert/visible")
        suspend fun updateAdvertVisibility(
        @Query("advertId") advertId:String,
        @Query("state") state:Boolean,
        @Header("Authentication") token:String
    ):Response<Any>
}
class ReturnListAdvertModel: ArrayList<AdvertModel>()
class ReturnListFavoriteAdvertModel: ArrayList<FavoriteAdvertModel>()

