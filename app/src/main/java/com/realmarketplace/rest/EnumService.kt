package com.realmarketplace.rest

import retrofit2.Response
import retrofit2.http.GET
/**
 * A group of *api_services*.
 *
 * Service used to declare API ENUM endpoint method.
 */
interface EnumService {
    /**
     * A group of *api_function*.
     *
     * Function is used for getting price enum.
     *
     * Endpoint **get(/enum/price-option)**
     *
     * @return Array of price options
     */
    @GET(value="/enum/price-option")
    suspend fun getPrice():Response<ArrayType>
    /**
     * A group of *api_function*.
     *
     * Function is used for getting book condition enum.
     *
     * Endpoint **get(/enum/book-condition)**
     *
     * @return Array of book condition options
     */
    @GET(value="/enum/book-condition")
    suspend fun getBookCondition():Response<ArrayType>
    /**
     * A group of *api_function*.
     *
     * Function is used for getting book genre type enum.
     *
     * Endpoint **get(/enum/genre-type)**
     *
     * @return Array of viz. GenreItem
     */
    @GET(value="/enum/genre-type")
    suspend fun getGenres():Response<ArrayList<GenreItem>>

}
/**
 * A group of *api_return_class*.
 *
 * Class used to define List of Options api request.
 */
class ArrayType:ArrayList<String>()
/**
 * A group of *api_return_class*.
 *
 * Class used to define Genre Item.
 *
 * @param name name of the specific genre
 * @param type either fiction or nonfiction
 */
data class GenreItem(
    val name:String,
    val type:String
):java.io.Serializable