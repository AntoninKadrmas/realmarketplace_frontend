package com.example.omega1.rest

import com.example.omega1.model.AdvertModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface AdvertService {
    @POST(value="/advert/create")
    suspend fun createAdvert(@Body advertModel:AdvertModel,@Query("token") token:String): Response<Any>
    @Multipart
    @POST(value = "/image/public")
    suspend fun uploadAdvertImage(@Part uploaded_file:MultipartBody.Part):Response<Any>
}