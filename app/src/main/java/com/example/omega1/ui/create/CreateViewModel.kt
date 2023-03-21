package com.example.omega1.ui.create

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.omega1.model.AdvertId
import com.example.omega1.model.AdvertModel
import com.example.omega1.rest.AdvertService
import com.example.omega1.rest.RetrofitInstance
import com.example.omega1.rest.ReturnTypeError
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException


class CreateViewModel : ViewModel() {
    private var mutableImagesFile = MutableLiveData<ArrayList<File>>()
    val imagesFile: LiveData<ArrayList<File>> get() = mutableImagesFile
    fun appendNewFile(new_list: ArrayList<File>) {
        var list = ArrayList<File>()
        if (mutableImagesFile.value != null){
            list = mutableImagesFile.value!!
        }
        for(item in new_list){
            list.add(item)
        }
        mutableImagesFile.value = list
    }
    fun removeOldFileByPos(position:Int) {
        val list = ArrayList<File>()
        var count=0;
        if (mutableImagesFile.value != null)
            for (file in mutableImagesFile.value!!) {
                if (count != position) list.add(file)
                count++
            }
        mutableImagesFile.value = list
    }
    private var retroServiceAdvert: AdvertService = RetrofitInstance.getRetroFitInstance().create(
        AdvertService::class.java
    )
    fun createAdvert(advertModel: AdvertModel, userToken: String, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val bodyList = ArrayList<MultipartBody.Part>()
            if (!imagesFile.value.isNullOrEmpty()) {
                for(file in imagesFile.value!!){
                    val requestFile: RequestBody =
                        RequestBody.create(
                            "image/${file?.absolutePath.toString().substring(file?.absolutePath.toString().lastIndexOf(".")+1)}"
                                .toMediaTypeOrNull(), file!!)
                    val body = MultipartBody.Part.createFormData(
                        "uploaded_file",
                        file?.name,
                        requestFile
                    )
                    bodyList.add(body)
                }
            }
            val requestBody: RequestBody = RequestBody.create("text/json".toMediaTypeOrNull(),Gson().toJson(advertModel).toString())
            bodyList.add(MultipartBody.Part.createFormData(
                "advert_info",
                "advert_info",
                requestBody
            ))
            val response = try{
                println(bodyList)
                retroServiceAdvert.createAdvert(bodyList, userToken)
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
                val body: AdvertId? =
                    Gson().fromJson(Gson().toJson(response.body()), AdvertId::class.java)

            } else {
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? =
                        Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main) {
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
//    private fun uploadImage(filePart: MultipartBody.Part, context: Context) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = try {
//                retroServiceAdvert.uploadAdvertImage(filePart)
//            } catch (e: IOException) {
//                withContext(Dispatchers.Main) {
//                    println(e)
//                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
//                }
//                return@launch
//            } catch (e: HttpException) {
//                withContext(Dispatchers.Main) {
//                    println(e)
//                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
//                }
//                return@launch
//            }
//            if (response.isSuccessful && response.body() != null) {
//                val body: AdvertId? =
//                    Gson().fromJson(Gson().toJson(response.body()), AdvertId::class.java)
////                println(body)
//            } else {
//                val errorBody = response.errorBody()
//                try {
//                    val errorResponse: ReturnTypeError? =
//                        Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(context, "${errorResponse?.error}", Toast.LENGTH_LONG).show()
//                    }
//                } catch (e: Exception) {
//                    withContext(Dispatchers.Main) {
//                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            }
//            return@launch
//        }
//    }
}