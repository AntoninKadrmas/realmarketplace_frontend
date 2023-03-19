package com.example.omega1.ui.create

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.omega1.R
import com.example.omega1.model.AdvertModel
import com.example.omega1.rest.AdvertService
import com.example.omega1.rest.RetrofitInstance
import com.example.omega1.rest.ReturnTypeError
import com.google.gson.Gson
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.IOException

class CreateViewModel : ViewModel() {
    private var mutableImages= MutableLiveData<ArrayList<Uri>>()
    val images: LiveData<ArrayList<Uri>> get() = mutableImages
    fun appendNew(list:ArrayList<Uri>){
        if(mutableImages.value!=null)
            for(uri in mutableImages.value!!){
                list.add(uri)
            }
        mutableImages.value = list
    }
    fun removeOld(delete_uri: Uri){
        val list =ArrayList<Uri>()
        if(mutableImages.value!=null)
            for(uri in mutableImages.value!!){
                if(uri!=delete_uri)list.add(uri)
            }
        mutableImages.value = list
    }
    private var retroServiceAdvert: AdvertService = RetrofitInstance.getRetroFitInstance().create(
        AdvertService::class.java)
    fun createAdvert(advertModel:AdvertModel,userToken:String, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAdvert.createAdvert(advertModel,userToken)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                println(response.body())
            }else{
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? = Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"${errorResponse?.error}", Toast.LENGTH_LONG).show()
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return@launch
        }
    }
}