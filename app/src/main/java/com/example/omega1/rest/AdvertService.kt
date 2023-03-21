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
    @Multipart
    @POST(value="/advert/create")
    suspend fun createAdvert(@Part body:ArrayList<MultipartBody.Part>,@Query("token") token:String): Response<Any>
}