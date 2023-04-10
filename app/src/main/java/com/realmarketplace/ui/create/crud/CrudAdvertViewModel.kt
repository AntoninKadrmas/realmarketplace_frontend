package com.realmarketplace.ui.create.crud

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.ui.auth.LogOutAuth
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import com.realmarketplace.rest.*
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class CrudAdvertViewModel : ViewModel() {
    private var crudTools= CrudAdvertTools()
    private var mutableReturnDelete = MutableLiveData<Boolean>()
    val returnDelete:LiveData<Boolean> get() = mutableReturnDelete
    private var mutableReturnUpdate = MutableLiveData<AdvertModel>()
    val returnUpdate:LiveData<AdvertModel> get() = mutableReturnUpdate
    var buttonsEnabled = MutableLiveData<Boolean>()
    private var mutableImagesFile = MutableLiveData<ArrayList<File>>().apply {
        value=ArrayList()
    }
    val imagesFile: LiveData<ArrayList<File>> get() = mutableImagesFile
    val clean = MutableLiveData<AdvertModel>()
    var executed = false
    fun appendNewFile(new_list: ArrayList<File>) {
        var list = ArrayList<File>()
        if (mutableImagesFile.value != null) {
            list = mutableImagesFile.value!!
        }
        for (item in new_list) {
            list.add(item)
        }
        mutableImagesFile.value = list
    }
    fun removeOldFileByPos(position: Int) {
        val list = ArrayList<File>()
        var count = 0;
        if (mutableImagesFile.value != null)
            for (file in mutableImagesFile.value!!) {
                if (count != position) list.add(file)
                count++
            }
        mutableImagesFile.value = list
    }
    fun clearFiles( ){
        mutableImagesFile.value=ArrayList<File>()
    }
    private var retroServiceAdvert: AdvertService = RetrofitInstance.getRetroFitInstance().create(
        AdvertService::class.java
    )
    fun createAdvert(advertModel: AdvertModel, userToken: String, context: Context) {
        executed=true
        CoroutineScope(Dispatchers.IO).launch {
            val bodyList = crudTools.createImagesFileBody(0,imagesFile.value)
            val response = try {
                retroServiceAdvert.createAdvert(
                    bodyList,
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        advertModel.title
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        advertModel.author
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        advertModel.description
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        advertModel.genreName
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        advertModel.genreType
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        advertModel.price
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        advertModel.priceOption
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        advertModel.condition
                    ),
                    userToken
                )
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value=true
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value=true
                }
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val body: ReturnTypeSuccessAdvert? =
                    Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccessAdvert::class.java)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, body?.success.toString(), Toast.LENGTH_LONG).show()
                    clean.value=body?.advert
                    buttonsEnabled.value=true
                }
            } else {
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? =
                        Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main) {
                        if(response.code()==401) LogOutAuth.setLogOut(true)
                        Toast.makeText(context, "${errorResponse?.error}", Toast.LENGTH_LONG).show()
                        buttonsEnabled.value=true
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT)
                            .show()
                        buttonsEnabled.value=true
                    }
                }
            }
            return@launch
        }
    }
    fun updateAdvert(
        newAdvertModel: AdvertModel,
        deletedUrls:ArrayList<String>, userToken: String, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val bodyList = crudTools.createImagesFileBody(newAdvertModel.imagesUrls.size,imagesFile.value)
            val response = try {
                retroServiceAdvert.updateAdvert(
                    bodyList,
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        newAdvertModel._id
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        newAdvertModel.title
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        newAdvertModel.author
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        newAdvertModel.description
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        newAdvertModel.genreName
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        newAdvertModel.genreType
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        newAdvertModel.price
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        newAdvertModel.priceOption
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        newAdvertModel.condition
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        newAdvertModel.mainImageUrl
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        newAdvertModel.imagesUrls.joinToString(";")
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        deletedUrls.joinToString(";")
                    ),
                    userToken
                )
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value=true
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value=true
                }
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val body: ReturnTypeSuccessUris? =
                    Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccessUris::class.java)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, body?.success.toString(), Toast.LENGTH_LONG).show()
                    if(body?.imageUrls!=null){
                        newAdvertModel.imagesUrls=body?.imageUrls!!
                        if(body.imageUrls.size>0)newAdvertModel.mainImageUrl=newAdvertModel.imagesUrls[0]
                        else newAdvertModel.mainImageUrl=""
                    }else{
                        newAdvertModel.imagesUrls= ArrayList()
                        newAdvertModel.mainImageUrl=""
                    }
                    mutableReturnUpdate.value=newAdvertModel
                    buttonsEnabled.value=true
                }
            } else {
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? =
                        Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main) {
                        if(response.code()==401) LogOutAuth.setLogOut(true)
                        Toast.makeText(context, "${errorResponse?.error}", Toast.LENGTH_LONG).show()
                        buttonsEnabled.value=true
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT).show()
                        buttonsEnabled.value=true
                    }
                }
            }
            return@launch
        }
    }
fun deleteAdvert(advertId: String, userToken: String, context: Context) {
    CoroutineScope(Dispatchers.IO).launch {
        val response = try {
            retroServiceAdvert.deleteAdvert(
                advertId,
                userToken
            )
        } catch (e: IOException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                buttonsEnabled.value=true
            }
            return@launch
        } catch (e: HttpException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                buttonsEnabled.value=true
            }
            return@launch
        }
        if (response.isSuccessful && response.body() != null) {
            val body: ReturnTypeSuccessAdvert? =
                Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccessAdvert::class.java)
            withContext(Dispatchers.Main) {
                mutableReturnDelete.value = true
                Toast.makeText(context, body?.success.toString(), Toast.LENGTH_LONG).show()
                buttonsEnabled.value=true
            }
        } else {
            val errorBody = response.errorBody()
            try {
                val errorResponse: ReturnTypeError? =
                    Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                withContext(Dispatchers.Main) {
                    if(response.code()==401) LogOutAuth.setLogOut(true)
                    Toast.makeText(context, "${errorResponse?.error}", Toast.LENGTH_LONG).show()
                    buttonsEnabled.value=true
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value=true

                }
            }
        }
        return@launch
    }
}}