package com.example.omega1.rest

import retrofit2.Response
import retrofit2.http.GET

interface EnumService {
    @GET(value="/enum/price-option")
    suspend fun getPrice():Response<ArrayType>
    @GET(value="/enum/book-condition")
    suspend fun getBookCondition():Response<ArrayType>
}
class ArrayType:ArrayList<String>()
