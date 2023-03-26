package com.example.omega1.rest

import retrofit2.Response
import retrofit2.http.GET

interface EnumService {
    @GET(value="/enum/price-option")
    suspend fun getPrice():Response<ArrayType>
    @GET(value="/enum/book-condition")
    suspend fun getBookCondition():Response<ArrayType>
    @GET(value="/enum/genre-type")
    suspend fun getGenres():Response<ArrayList<GenreItem>>

}
class ArrayType:ArrayList<String>()
data class GenreItem(
    val name:String,
    val type:String
):java.io.Serializable