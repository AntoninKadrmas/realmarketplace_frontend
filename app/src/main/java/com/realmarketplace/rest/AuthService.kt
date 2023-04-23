package com.realmarketplace.rest

import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.LightUser
import com.realmarketplace.model.UserModel
import retrofit2.Response
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
/**
 * A group of *api_services*.
 *
 * Service used to declare API User and Auth endpoint method.
 */
interface AuthService {
    /**
     * A group of *api_function*.
     *
     * Function is used for register user account.
     *
     * Endpoint **post(/user/register)**
     *
     * @param credential string contains email password string pair in basic format
     * @param user viz. UserModel
     * @return viz. UserTokenAuth or viz. ReturnTypeError
     */
    @POST(value="/user/register")
    suspend fun registerUser(
        @Header("Authorization") credential:String,
        @Body user: UserModel):Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function is used for login user account.
     *
     * Endpoint **post(/user/login)**
     *
     * @param credential string contains email password string pair in basic format
     * @return viz. UserTokenAuth or viz. ReturnTypeError
     */
    @POST(value="/user/login")
    suspend fun loginUser(
        @Header("Authorization") credential:String):Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function is used for updating user profile image.
     * Data to endpoint is send in form data format because of image file.
     *
     * Endpoint **post(/user/image)**
     *
     * @param body contains image profile picture file
     * @param deletedUrls contains old user image url
     * @param token user verification token
     * @return viz. ReturnTypeSuccessUris or viz. ReturnTypeError
     */
    @Multipart
    @POST(value="/user/image")
    suspend fun uploadProfileImage(
        @Part body:MultipartBody.Part,
        @Header("Authentication") token:String): Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function used for getting specific user information's.
     *
     * Endpoint **get(/user)**
     *
     * @param token user verification token
     * @return viz. LightUser or viz. ReturnTypeError
     */
    @GET(value="/user")
    suspend fun getMyUser(
        @Header("Authentication") token: String):Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function used for updating specific user password.
     *
     * Endpoint **post(/user)**
     *
     *
     * @param credential string contains old and new password string pair in basic format
     * @param token user verification token
     * @return viz. ReturnTypeSuccess or viz. ReturnTypeError
     */
    @POST(value="/user")
    suspend fun updateUserPassword(
        @Header("Authorization") credential:String,
        @Header("Authentication") token:String):Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function used for updating specific user profile information's.
     *
     * Endpoint **put(/user)**
     *
     *
     * @param body contains update user information's
     * @param token user verification token
     * @return viz. ReturnTypeSuccess or viz. ReturnTypeError
     */
    @PUT(value="/user")
    suspend fun updateUser(
        @Body body: LightUser,
        @Header("Authentication") token:String):Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function used for deleting all specific user information's.
     *
     * Endpoint **delete(/user)**
     *
     * @param credential string contains actual password string in basic format
     * @param token user verification token
     * @return viz. ReturnTypeSuccess or viz. ReturnTypeError
     */
    @DELETE(value="/user")
    suspend fun deleteUser(
        @Header("Authorization") credential:String,
        @Header("Authentication") token:String):Response<Any>
    /**
     * A group of *api_function*.
     *
     * Function is used for reset user password.
     *
     * Endpoint **post(/email/passwordRecovery)**
     *
     * @param email email which temporary password would be generated
     * @return viz. ReturnTypeSuccess or viz. ReturnTypeError
     */
    @POST(value="/email/passwordRecovery")
    suspend fun resetPassword(
        @Query("email") email:String):Response<Any>
}
/**
 * A group of *api_return_class*.
 *
 * Class used to define error output of api request.
 */
data class ReturnTypeError(
    @SerializedName("error")
    val error:String
)
/**
 * A group of *api_return_class*.
 *
 * Class used to define success output of api request.
 */
data class ReturnTypeSuccess(
    @SerializedName("success")
    val success:String
)
/**
 * A group of *api_return_class*.
 *
 * Class used to define success output with advert Model of api request.
 */
data class ReturnTypeSuccessAdvert(
    @SerializedName("success")
    val success:String,
    @SerializedName("advert")
    val advert: AdvertModel
)
/**
 * A group of *api_return_class*.
 *
 * Class used to define success output with list of urls of api request.
 */
data class ReturnTypeSuccessUris(
    @SerializedName("success")
    val success:String,
    @SerializedName("imageUrls")
    val imageUrls: ArrayList<String>
)