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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import com.realmarketplace.rest.*
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.IOException

class   UserViewModel:ViewModel() {
    private var crudTools= CrudAdvertTools()
    private val userAdvertsMutable = MutableLiveData<ArrayList<AdvertModel>>()
    val userAdverts: LiveData<ArrayList<AdvertModel>> get() = userAdvertsMutable
    private val userMutable = MutableLiveData<LightUser>()
    val user: LiveData<LightUser> get() = userMutable
    var endSettings:MutableLiveData<Boolean> = MutableLiveData()
    var buttonEnables = true
    private var retroServiceAdvert: AdvertService = RetrofitInstance.getRetroFitInstance().create(
        AdvertService::class.java
    )
    private var retroServiceUser: AuthService = RetrofitInstance.getRetroFitInstance().create(
        AuthService::class.java
    )
    fun updateUserInfo(user: LightUser){
        userMutable.value=user
    }
    fun loadAllUserPublicAdverts(user: LightUser, token: UserTokenAuth, context: Context) {
        FavoriteViewModel.loadedFavoriteAdvert =true
        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                retroServiceAdvert.getUserPublicAdvert(user.email,user.createdIn,token.token)
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val body: ReturnListAdvertModel? = Gson().fromJson(Gson().toJson(response.body()), ReturnListAdvertModel::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        userAdvertsMutable.value = body!!
                    }
                }
            } else {
                try {
                    errorResponse(response,context)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            return@launch
        }
    }
    fun loadUserInformation(token: UserTokenAuth, context: Context) {
        FavoriteViewModel.loadedFavoriteAdvert =true
        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                retroServiceUser.getMyUser(token.token)
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val body: LightUser? = Gson().fromJson(Gson().toJson(response.body()), LightUser::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        userMutable.value=body!!
                    }
                }
            } else {
                try {
                    errorResponse(response,context)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            return@launch
        }
    }
    fun uploadUserImage(userOld: LightUser, imageFile:File, userToken: UserTokenAuth, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val body = crudTools.createImageFileBody( imageFile)
            val response = try {
                retroServiceUser.uploadProfileImage(
                    body,
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        userOld.mainImageUrl
                    ),
                    userToken.token
                )
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
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
                    userOld.mainImageUrl = body!!.imageUrls[0]
                    userMutable.value=userOld
                }
            } else {
                try {
                    errorResponse(response,context)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            return@launch
        }
    }
    fun updatePassword(oldPassword: String, newPassword:String, token: UserTokenAuth, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceUser.updateUserPassword(okhttp3.Credentials.basic(oldPassword, newPassword), token.token)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonEnables=true
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonEnables=true
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: ReturnTypeSuccess? = Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccess::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        Toast.makeText(context,"${body?.success}", Toast.LENGTH_LONG).show()
                        buttonEnables=true
                        endSettings.value=true
                    }
                }
            }else{
                try {
                    errorResponse(response,context)
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                        buttonEnables=true
                    }
                }
            }
            return@launch
        }
    }
    fun updateUser(newUser: LightUser, token: UserTokenAuth, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceUser.updateUser(newUser, token.token)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonEnables=true
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonEnables=true
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: ReturnTypeSuccess? = Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccess::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        Toast.makeText(context,"${body?.success}", Toast.LENGTH_LONG).show()
                        userMutable.value=newUser
                        buttonEnables=true
                        endSettings.value=true
                    }
                }
            }else{
                try {
                    errorResponse(response,context)
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                        buttonEnables=true
                    }
                }
            }
            return@launch
        }
    }
    fun deleteUser(password:String, token: UserTokenAuth, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceUser.deleteUser(okhttp3.Credentials.basic(password, ""), token.token)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    println(e)
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonEnables=true
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonEnables=true
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: ReturnTypeSuccess? = Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccess::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        Toast.makeText(context,"${body?.success}", Toast.LENGTH_LONG).show()
                        LogOutAuth.setLogOut(true)
                        buttonEnables=true
                    }
                }
            }else{
                try {
                    errorResponse(response,context)
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                        buttonEnables=true
                    }
                }
            }
            return@launch
        }
    }
    private suspend fun errorResponse(response:Response<Any>,context: Context){
        val errorBody = response.errorBody()
        val errorResponse: ReturnTypeError? = Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
        withContext(Dispatchers.Main){
            if (response.code() == 401) LogOutAuth.setLogOut(true)
            Toast.makeText(context,"${errorResponse?.error}", Toast.LENGTH_LONG).show()
            buttonEnables=true
        }
    }
}