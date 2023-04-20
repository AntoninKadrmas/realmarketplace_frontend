package com.realmarketplace.ui.user

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.LightUser
import com.realmarketplace.model.UserTokenAuth
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.ui.create.crud.CrudAdvertTools
import com.realmarketplace.ui.favorite.FavoriteViewModel
import com.google.gson.Gson
import com.realmarketplace.model.UserModelLogin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import com.realmarketplace.rest.*
import com.realmarketplace.ui.auth.AuthViewModel
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.IOException

/**
 * A group of *view_model*.
 *
 * Object hold user information's and his adverts.
 * Operates over some of the auth user endpoints.
 */
class UserViewModel:ViewModel() {
    private var crudTools= CrudAdvertTools()
    private val userAdvertsMutable = MutableLiveData<ArrayList<AdvertModel>>()
    val userAdverts: LiveData<ArrayList<AdvertModel>> get() = userAdvertsMutable
    private val userMutable = MutableLiveData<LightUser>()
    val user: LiveData<LightUser> get() = userMutable
    var endSettings:MutableLiveData<Boolean> = MutableLiveData()
    var buttonEnables = MutableLiveData<Boolean>()
    private var retroServiceAdvert: AdvertService = RetrofitInstance.getRetroFitInstance().create(
        AdvertService::class.java
    )
    private var retroServiceUser: AuthService = RetrofitInstance.getRetroFitInstance().create(
        AuthService::class.java
    )
    /**
     * A group of *view_model_function*.
     *
     * Function used to update user variable with new user.
     *
     * @param user new user which would be set as default one viz. LightUser
     */
    fun updateUserInfo(user: LightUser){
        userMutable.value=user
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to call getUserPublicAdvert function and handle the response.
     *
     * @param user which advert are going to be fetched viz. LightUser
     * @param userToken user authentication token viz. UserTokenAuth
     * @param context context of activity or fragment where is function called
     */
    fun loadAllUserPublicAdverts(user: LightUser, userToken: UserTokenAuth, context: Context) {
        FavoriteViewModel.loadedFavoriteAdvert =true
        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                retroServiceAdvert.getUserPublicAdvert(user.email,user.createdIn,userToken.token)
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonEnables.value=true
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonEnables.value=true
                }
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val body: ReturnListAdvertModel? = Gson().fromJson(Gson().toJson(response.body()), ReturnListAdvertModel::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) userAdvertsMutable.value = body!!
                    buttonEnables.value=true
                }
            } else {
                try {
                    errorResponse(response,context)
                    withContext(Dispatchers.Main){
                        buttonEnables.value=true
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return@launch
        }
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to call getMyUser function and handle the response.
     *
     * @param userToken user authentication token viz. UserTokenAuth
     * @param context context of activity or fragment where is function called
     */
    fun loadUserInformation(userToken: UserTokenAuth, context: Context) {
        FavoriteViewModel.loadedFavoriteAdvert =true
        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                retroServiceUser.getMyUser(userToken.token)
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonEnables.value=true
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonEnables.value=true
                }
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val body: LightUser? = Gson().fromJson(Gson().toJson(response.body()), LightUser::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null)userMutable.value=body!!
                    buttonEnables.value=true
                }
            } else {
                try {
                    errorResponse(response,context)
                    withContext(Dispatchers.Main){
                        buttonEnables.value=true
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT).show()
                        buttonEnables.value=true
                    }
                }
            }
            return@launch
        }
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to call uploadProfileImage function and handle the response.
     *
     * @param imageFile new image file that would be used as new one
     * @param userToken user authentication token viz. UserTokenAuth
     * @param context context of activity or fragment where is function called
     */
    fun uploadUserImage(imageFile:File, userToken: UserTokenAuth, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val body = crudTools.createImageFileBody( imageFile)
            val response = try {
                retroServiceUser.uploadProfileImage(
                    body,
                    userToken.token
                )
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonEnables.value=true
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonEnables.value=true
                }
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val body: ReturnTypeSuccessUris? =
                    Gson().fromJson(
                        Gson().toJson(response.body()),
                        ReturnTypeSuccessUris::class.java
                    )
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, body?.success.toString(), Toast.LENGTH_LONG).show()
                    val userOld = userMutable.value!!
                    userOld.mainImageUrl = body!!.imageUrls[0]
                    userMutable.value=userOld
                    buttonEnables.value=true
                }
            } else {
                try {
                    crudTools.errorResponse(response,context)
                    withContext(Dispatchers.Main){
                        buttonEnables.value=true
                    }
                }catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT).show()
                        buttonEnables.value=true
                    }
                }
            }
            return@launch
        }
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to call updateUserPassword function and handle the response.
     *
     * @param oldPassword string user enter as his old password
     * @param newPassword string user enter as his new password
     * @param userToken user authentication token viz. UserTokenAuth
     * @param context context of activity or fragment where is function called
     */
    fun updatePassword(oldPassword: String, newPassword:String, userToken: UserTokenAuth, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceUser.updateUserPassword(okhttp3.Credentials.basic(oldPassword, newPassword), userToken.token)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonEnables.value=true
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonEnables.value=true
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: ReturnTypeSuccess? = Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccess::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        Toast.makeText(context,"${body?.success}", Toast.LENGTH_LONG).show()
                        buttonEnables.value=true
                        endSettings.value=true
                        AuthViewModel.guestUser.value?.let {
                            UserModelLogin(
                                it.email,newPassword)
                        }?.let { AuthViewModel.updateUserCredentials(it) }
                    }
                }
            }else{
                try {
                    crudTools.errorResponse(response,context)
                    withContext(Dispatchers.Main){
                        buttonEnables.value=true
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                        buttonEnables.value=true
                    }
                }
            }
            return@launch
        }
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to call updateUser function and handle the response.
     *
     * @param newUser user which contains information's about newly updated user
     * @param userToken user authentication token viz. UserTokenAuth
     * @param context context of activity or fragment where is function called
     */
    fun updateUser(newUser: LightUser, userToken: UserTokenAuth, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceUser.updateUser(newUser, userToken.token)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonEnables.value=true
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonEnables.value=true
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: ReturnTypeSuccess? = Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccess::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        Toast.makeText(context,"${body?.success}", Toast.LENGTH_LONG).show()
                        userMutable.value=newUser
                        buttonEnables.value=true
                        endSettings.value=true
                    }
                }
            }else{
                try {
                    crudTools.errorResponse(response,context)
                    withContext(Dispatchers.Main){
                        buttonEnables.value=true
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                        buttonEnables.value=true
                    }
                }
            }
            return@launch
        }
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to call deleteUser function and handle the response.
     *
     * @param password string user enter as his actual password to verify him self
     * @param userToken user authentication token viz. UserTokenAuth
     * @param context context of activity or fragment where is function called
     */
    fun deleteUser(password:String, userToken: UserTokenAuth, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceUser.deleteUser(okhttp3.Credentials.basic(password, ""), userToken.token)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    println(e)
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonEnables.value=true
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonEnables.value=true
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: ReturnTypeSuccess? = Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccess::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        Toast.makeText(context,"${body?.success}", Toast.LENGTH_LONG).show()
                        buttonEnables.value=true
                        AuthViewModel.updateUserCredentials(UserModelLogin("",""))
                        LogOutAuth.mutableLogOutUser.value = true
                    }
                }
            }else{
                try {
                    crudTools.errorResponse(response,context)
                    withContext(Dispatchers.Main){
                        buttonEnables.value=true
                    }
                } catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                        buttonEnables.value=true
                    }
                }
            }
            return@launch
        }
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to handle error response.
     *
     * @param response error response from https request
     * @param context context of activity or fragment
     */
    private suspend fun errorResponse(response:Response<Any>,context: Context){
        val errorBody = response.errorBody()
        val errorResponse: ReturnTypeError? = Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
        withContext(Dispatchers.Main){
            if (response.code() == 401) LogOutAuth.mutableLogOutUser.value = true
            Toast.makeText(context,"${errorResponse?.error}", Toast.LENGTH_LONG).show()
            buttonEnables.value=true
        }
    }
}