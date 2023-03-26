package com.example.omega1.ui.create.crud

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.omega1.model.AdvertModel
import com.example.omega1.rest.*
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class CrudAdvertViewModel : ViewModel() {
    private var mutableReturnDelete = MutableLiveData<Boolean>()
    val returnDelete:LiveData<Boolean> get() = mutableReturnDelete
    private var mutableReturnUpdate = MutableLiveData<AdvertModel>()
    val returnUpdate:LiveData<AdvertModel> get() = mutableReturnUpdate
    private var mutableImagesFile = MutableLiveData<ArrayList<File>>().apply {
        value=ArrayList()
    }
    private var mutableLogOut = MutableLiveData<Boolean>()
    val imagesFile: LiveData<ArrayList<File>> get() = mutableImagesFile
    val logOut :LiveData<Boolean> get() = mutableLogOut
    val clean = MutableLiveData<AdvertModel>()
    var executed = false
    fun setLogOut(state:Boolean){
        mutableLogOut.value=state
    }
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
    fun createImageFileBody(skipFirs:Int):ArrayList<MultipartBody.Part>{
        val bodyList = ArrayList<MultipartBody.Part>()
        if (!imagesFile.value.isNullOrEmpty()) {
            var counter=0
            for (file in imagesFile.value!!) {
                counter++
                if(counter<=skipFirs)continue
                val requestFile: RequestBody =
                    RequestBody.create("image/${file?.absolutePath.toString()
                        .substring(file?.absolutePath.toString().lastIndexOf(".") + 1)
                    }".toMediaTypeOrNull(), file!!
                    )
                val body = MultipartBody.Part.createFormData("uploaded_file", file?.name, requestFile)
                bodyList.add(body)
            }
        }
        return bodyList
    }
    fun createAdvert(advertModel: AdvertModel, userToken: String, context: Context) {
        executed=true
        CoroutineScope(Dispatchers.IO).launch {
            val bodyList = createImageFileBody(0)
            val response = try {
                println(bodyList)
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
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val body: ReturnTypeSuccessAdvert? =
                    Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccessAdvert::class.java)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, body?.success.toString(), Toast.LENGTH_LONG).show()
                    clean.value=body?.advert
                }
            } else {
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? =
                        Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main) {
                        if(response.code()==401)mutableLogOut.value = true
                        Toast.makeText(context, "${errorResponse?.error}", Toast.LENGTH_LONG).show()
                    }
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
    fun updateAdvert(newAdvertModel: AdvertModel,deletedUrls:ArrayList<String>, userToken: String, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val bodyList = createImageFileBody(newAdvertModel.imagesUrls.size)
            val response = try {
                println(bodyList)
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
                        newAdvertModel.mainImage
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
                    println(e)
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
                    Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccessUris::class.java)
                withContext(Dispatchers.Main) {
                    println(body)
                    Toast.makeText(context, body?.success.toString(), Toast.LENGTH_LONG).show()
                    newAdvertModel.imagesUrls=body?.imageUrls!!
                    mutableReturnUpdate.value=newAdvertModel
                }
            } else {
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? =
                        Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main) {
                        if(response.code()==401)mutableLogOut.value = true
                        Toast.makeText(context, "${errorResponse?.error}", Toast.LENGTH_LONG).show()
                    }
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
            }
            return@launch
        } catch (e: HttpException) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
            }
            return@launch
        }
        if (response.isSuccessful && response.body() != null) {
            val body: ReturnTypeSuccessAdvert? =
                Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccessAdvert::class.java)
            withContext(Dispatchers.Main) {
                mutableReturnDelete.value = true
                Toast.makeText(context, body?.success.toString(), Toast.LENGTH_LONG).show()
            }
        } else {
            val errorBody = response.errorBody()
            try {
                val errorResponse: ReturnTypeError? =
                    Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                withContext(Dispatchers.Main) {
                    if(response.code()==401)mutableLogOut.value = true
                    Toast.makeText(context, "${errorResponse?.error}", Toast.LENGTH_LONG).show()
                }
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
}