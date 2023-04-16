package com.realmarketplace.ui.create.crud

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.ui.auth.LogOutAuth
import com.google.gson.Gson
import com.realmarketplace.model.UserTokenAuth
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import com.realmarketplace.rest.*
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.IOException

/**
 * A group of *view_model*.
 *
 * Take care of selected user selected images.
 * Operates over some of the advert endpoints.
 */
class CrudAdvertViewModel : ViewModel() {
    private var crudTools = CrudAdvertTools()
    private var mutableReturnDelete = MutableLiveData<Boolean>()
    val returnDelete: LiveData<Boolean> get() = mutableReturnDelete
    private var mutableReturnUpdate = MutableLiveData<AdvertModel>()
    val returnUpdate: LiveData<AdvertModel> get() = mutableReturnUpdate
    var buttonsEnabled = MutableLiveData<Boolean>()
    private var mutableImagesFile = MutableLiveData<ArrayList<File>>().apply {
        value = ArrayList()
    }
    val imagesFile: LiveData<ArrayList<File>> get() = mutableImagesFile
    val clean = MutableLiveData<AdvertModel>()
    var executed = false
    /**
     * A group of *view_model_function*.
     *
     * Function used to add new files into image files list.
     *
     * @param newList list of image files that is going to be added into image files list
     */
    fun appendNewFile(newList: ArrayList<File>) {
        var list = ArrayList<File>()
        if (mutableImagesFile.value != null) {
            list = mutableImagesFile.value!!
        }
        for (item in newList) {
            list.add(item)
        }
        mutableImagesFile.value = list
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to delete file at given index.
     *
     * @param position index of image file that is going to be deleted
     */
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
    /**
     * A group of *view_model_function*.
     *
     * Function used to clear image files list.
     */
    fun clearFiles() {
        mutableImagesFile.value = ArrayList()
    }

    private var retroServiceAdvert: AdvertService = RetrofitInstance.getRetroFitInstance().create(
        AdvertService::class.java
    )
    /**
     * A group of *view_model_function*.
     *
     * Function used to call createAdvert function and handle the response.
     *
     * @param advertModel advert that is going to be created viz. AdvertModel
     * @param userToken user authentication token viz. UserTokenAuth
     * @param context context of activity or fragment where is function called
     */
    fun createAdvert(advertModel: AdvertModel, userToken: UserTokenAuth, context: Context) {
        executed = true
        CoroutineScope(Dispatchers.IO).launch {
            val bodyList = crudTools.createImagesFileBody(imagesFile.value)
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
                    userToken.token
                )
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value = true
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value = true
                }
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                successResponse(response,context)
            } else {
                try {
                    crudTools.errorResponse(response, context,true)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT)
                            .show()
                        buttonsEnabled.value = true
                    }
                }
            }
            return@launch
        }
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to call updateAdvert function and handle the response.
     *
     * @param newAdvertModel advert with updated information's that is going to be rewrite old advert viz. AdvertModel
     * @param deletedUrls list of deleted existing urls
     * @param userToken user authentication token viz. UserTokenAuth
     * @param context context of activity or fragment where is function called
     */
    fun updateAdvert(
        newAdvertModel: AdvertModel, deletedUrls: ArrayList<String>,
        userToken: UserTokenAuth, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val bodyList = crudTools.createImagesFileBody(crudTools.getNewFileArray(imagesFile.value))
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
                        crudTools.getOldFileArray(imagesFile.value).joinToString(";;")
                    ),
                    RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        deletedUrls.joinToString(";")
                    ),
                    userToken.token
                )
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value = true
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value = true
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
                    if (body?.imageUrls != null) {
                        newAdvertModel.imagesUrls = body?.imageUrls!!
                        if (body.imageUrls.size > 0) newAdvertModel.mainImageUrl = newAdvertModel.imagesUrls[0]
                        else newAdvertModel.mainImageUrl = ""
                    } else {
                        newAdvertModel.imagesUrls = ArrayList()
                        newAdvertModel.mainImageUrl = ""
                    }
                    mutableReturnUpdate.value = newAdvertModel
                    buttonsEnabled.value = true
                }
            } else {
                try {
                    crudTools.errorResponse(response, context)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT)
                            .show()
                        buttonsEnabled.value = true
                    }
                }
            }
            return@launch
        }
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to call deleteAdvert function and handle the response.
     *
     * @param advert is advert which is going to be deleted viz. AdvertModel
     * @param userToken user authentication token viz. UserTokenAuth
     * @param context context of activity or fragment where is function called
     */
    fun deleteAdvert(advert: AdvertModel, userToken: UserTokenAuth, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                retroServiceAdvert.deleteAdvert(
                    advert._id,
                    advert.imagesUrls.joinToString(";"),
                    userToken.token
                )
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value = true
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    buttonsEnabled.value = true
                }
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                successResponse(response,context)
            } else {
                try {
                   crudTools.errorResponse(response, context)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT)
                            .show()
                        buttonsEnabled.value = true
                    }
                }
            }
            return@launch
        }
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to handle successful response.
     *
     * @param response successful response from https request
     * @param context context of activity or fragment where is function called
     */
    private suspend fun successResponse(response: Response<Any>, context: Context){
        val body: ReturnTypeSuccessAdvert? =
            Gson().fromJson(
                Gson().toJson(response.body()),
                ReturnTypeSuccessAdvert::class.java
            )
        withContext(Dispatchers.Main) {
            if(body?.advert!=null)clean.value=body.advert
            mutableReturnDelete.value = true
            Toast.makeText(context, body?.success.toString(), Toast.LENGTH_LONG).show()
            buttonsEnabled.value = true
        }
    }
}
