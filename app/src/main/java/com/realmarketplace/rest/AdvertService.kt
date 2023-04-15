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
/**
 * A group of *api_services*.
 *
 * Service used to declare API advert endpoints methods.
 */
interface AdvertService {
    /**
     * A group of *api_function*.
     *
     * Function is used to create new advert in database.
     * Data to endpoint is send in form data format because of image files.
     *
     * Endpoint **post(/advert)**
     *
     * @param body contains compressed up to 5 image files
     * @param advertTitle title of the book advert
     * @param advertAuthor author of the book
     * @param advertDescription description of the book advert
     * @param advertGenreName advert genre name
     * @param advertGenreType either fiction or nonfiction
     * @param advertPriceValue price value
     * @param advertPriceType type of the price option
     * @param advertCondition condition of the advert
     * @param token user verification token
     * @return viz. ReturnTypeSuccessAdvert or viz. ReturnTypeError
     */
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
    /**
     * A group of *api_function*.
     *
     * Function is used for delete specific advert in database.
     *
     * Endpoint **delete(/advert)**
     *
     * @param advertId id of advert that should be deleted
     * @param imageUrls image urls that should be deleted
     * @param token user verification token
     * @return viz. ReturnTypeSuccessAdvert or viz. ReturnTypeError
     */
    @DELETE(value="/advert")
    suspend fun deleteAdvert(
        @Query("advertId") advertId:String,
        @Header("deleteUrls") imageUrls:String,
        @Header("Authentication") token:String): Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function is used to update advert data in database.
     * Data to endpoint is send in form data format because of image files.
     *
     * Endpoint **put(/advert)**
     *
     * @param advertId id of advert that should be updated
     * @param body contains compressed up to 5 image files
     * @param advertTitle title of the book advert
     * @param advertAuthor author of the book
     * @param advertDescription description of the book advert
     * @param advertGenreName advert genre name
     * @param advertGenreType either fiction or nonfiction
     * @param advertPriceValue price value
     * @param advertPriceType type of the price option
     * @param advertCondition condition of the advert
     * @param imageUrls combination of urls and position of old not deleted image
     * @param deletedUrls urls of deleted old images
     * @param token user verification token
     * @return viz. ReturnTypeSuccessUris or viz. ReturnTypeError
     */
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
        @Part("imagesUrls")  imageUrls:RequestBody,
        @Part("deletedUrls")  deletedUrls:RequestBody,
        @Header("Authentication") token:String): Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function is used to add advert Id into user favorite advert list in database.
     *
     * Endpoint **post(/advert/favorite)**
     *
     * @param advertId id of advert that should be inserted into favorite list in database.
     * @param token user verification token
     * @return viz. ReturnTypeSuccess or viz. ReturnTypeError
     */
    @POST(value="/advert/favorite")
    suspend fun addFavorite(
        @Query("advertId") advertId:String,
        @Header("Authentication") token:String): Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function is used to delete advert Id from user favorite advert list in database.
     *
     * Endpoint **delete(/advert/favorite)**
     *
     * @param advertId id of advert that should be deleted from favorite list
     * @param token user verification token
     * @return viz. ReturnTypeSuccess or viz. ReturnTypeError
     */
    @DELETE(value="/advert/favorite")
    suspend fun deleteFavorite(
        @Query("advertId") advertId:String,
        @Header("Authentication") token:String): Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function is used to fetch all visible advert from user favorite advert list from database.
     *
     * Endpoint **get(/advert/favorite)**
     *
     * @param token user verification token
     * @return viz. ReturnListFavoriteAdvertModel or viz. ReturnTypeError
     */
    @GET(value="/advert/favorite")
    suspend fun getFavorite(
        @Header("Authentication") token:String): Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function is used to fetch few random visible adverts from database.
     *
     * Endpoint **get(/advert/all)**
     *
     * @param token user verification token
     * @return viz. ReturnTypeSuccessAdvert or viz. ReturnTypeError
     */
    @GET(value = "/advert/all")
    suspend fun getAdvertSample(
        @Header("Authentication") token:String):Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function is used to fetch some xth page searched visible adverts from database.
     *
     * Endpoint **get(/advert/all)**
     *
     * @param search is search query typed by user. You can search by advert title or author.
     * @param page is number that decide which page of adverts you will get
     * @param token user verification token
     * @return viz. AdvertSearchModel or viz. ReturnTypeError
     */
    @GET(value = "/advert/all")
    suspend fun getAdvertSearch(
        @Query("search") search:String,
        @Query("page") page:Int,
        @Header("Authentication") token:String):Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function is used to fetch all adverts created by user from database.
     *
     * Endpoint **get(/advert)**
     *
     * @param token user verification token
     * @return viz. ReturnListAdvertModel or viz. ReturnTypeError
     */
    @GET(value="/advert")
    suspend fun getUserAdvert(
        @Header("Authentication") token:String
    ):Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function is used to fetch all visible adverts created by user from database.
     *
     * Endpoint **get(/advert)**
     * @param email user email address
     * @createdIn time stamp when the user was created
     * @param token user verification token
     * @return viz. ReturnListAdvertModel or viz. ReturnTypeError
     */
    @GET(value="/advert")
    suspend fun getUserPublicAdvert(
        @Header("userEmail") email:String,
        @Header("createdIn") createdIn:String,
        @Header("Authentication") token:String
    ):Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function is used to change visibility od specific advert.
     *
     * Endpoint **put(/advert/visible)**
     *
     * @param advertId advert id of advert that visibility should be changed
     * @param state new state to set on specific address
     * @param token user verification token
     * @return viz. ReturnTypeSuccess or viz. ReturnTypeError
     */
    @PUT(value="/advert/visible")
        suspend fun updateAdvertVisibility(
        @Query("advertId") advertId:String,
        @Query("state") state:Boolean,
        @Header("Authentication") token:String
    ):Response<Any>
}
/**
 * A group of *api_return_class*.
 *
 * Class used to define output of api request for advert model.
 */
class ReturnListAdvertModel: ArrayList<AdvertModel>()
/**
 * A group of *api_return_class*.
 *
 * Class used to define output of api request for advert favorite model.
 */
class ReturnListFavoriteAdvertModel: ArrayList<FavoriteAdvertModel>()

