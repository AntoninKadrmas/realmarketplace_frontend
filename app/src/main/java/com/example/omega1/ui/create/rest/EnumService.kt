package com.example.omega1.ui.create.rest

import okhttp3.Response
import retrofit2.http.GET

interface EnumService {
    @GET(value="/enum/price-option")
    suspend fun getPrice():Response
    @GET(value="/enum/book-condition")
    suspend fun getBookCondition():Response
}