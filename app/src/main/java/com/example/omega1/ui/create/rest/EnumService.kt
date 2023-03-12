package com.example.omega1.ui.create.rest

import retrofit2.Response
import retrofit2.http.GET

interface EnumService {
    @GET(value="/enum/price-option")
    suspend fun getPrice():Response<PriceType>
}
class PriceType:ArrayList<ArrayList<String>>()