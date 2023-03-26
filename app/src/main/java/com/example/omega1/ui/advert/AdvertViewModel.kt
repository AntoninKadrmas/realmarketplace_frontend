package com.example.omega1.ui.advert

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.omega1.model.AdvertModel
import com.example.omega1.model.UserModelLogin
import com.example.omega1.model.UserTokenAuth
import com.example.omega1.rest.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AdvertViewModel:ViewModel() {
    private val mutableMyAdverts = MutableLiveData<ArrayList<AdvertModel>>()
    val myAdverts:LiveData<ArrayList<AdvertModel>> get() = mutableMyAdverts
    private val mutableFavoriteAdverts = MutableLiveData<ArrayList<AdvertModel>>()
    val favoriteAdverts:LiveData<ArrayList<AdvertModel>> get()=mutableFavoriteAdverts
    var loadedMyAdverts = false
    var loadedFavoriteAdvert=false
    fun addNewMyAdvert(advert:AdvertModel){
        println("upload $advert")
        if(mutableMyAdverts.value==null)mutableMyAdverts.value = ArrayList()
        if(!mutableMyAdverts.value!!.contains(advert)) mutableMyAdverts.value?.add(advert)
    }
    fun removeMyAdvertByIndex(index:Int){
        mutableMyAdverts.value!!.removeAt(index)
    }
    fun addNewFavoriteAdvert(advert:AdvertModel){
        if(mutableFavoriteAdverts.value==null)mutableFavoriteAdverts.value = ArrayList()
        if(!mutableFavoriteAdverts.value!!.contains(advert))mutableFavoriteAdverts.value?.add(advert)
        else mutableFavoriteAdverts.value!![mutableFavoriteAdverts.value!!.indexOf(advert)]=advert
    }
    fun removeNewFavoriteAdvert(advert:AdvertModel){
        if(mutableFavoriteAdverts.value!!.contains(advert))mutableFavoriteAdverts.value?.remove(advert)
    }
    private var retroServiceAdvert: AdvertService = RetrofitInstance.getRetroFitInstance().create(
        AdvertService::class.java
    )
    fun loadAllUserAdverts(tokenAuth: UserTokenAuth, context: Context){
        println("run function")
        loadedMyAdverts=true
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAdvert.getUserAdvert(tokenAuth.token)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    loadedMyAdverts=false
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    loadedMyAdverts=false
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: ReturnListAdvertModel? = Gson().fromJson(Gson().toJson(response.body()), ReturnListAdvertModel::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        println(body)
                        for(item in body){
                            addNewMyAdvert(item)
                        }
                    }
                }
            }else{
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? = Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main){
                        loadedMyAdverts=false
                        Toast.makeText(context,"${errorResponse?.error}", Toast.LENGTH_LONG).show()
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        loadedMyAdverts=false
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return@launch
        }
    }
    fun addFavoriteAdvert(advert:AdvertModel,token:UserTokenAuth, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAdvert.addFavorite(advert._id, token.token)
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
                val body: ReturnTypeSuccess? = Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccess::class.java)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, body?.success, Toast.LENGTH_SHORT).show()
                }
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