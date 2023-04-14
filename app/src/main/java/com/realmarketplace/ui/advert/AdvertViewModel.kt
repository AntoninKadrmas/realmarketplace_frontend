package com.realmarketplace.ui.advert

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.UserTokenAuth
import com.realmarketplace.ui.auth.LogOutAuth
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.realmarketplace.rest.*
import retrofit2.HttpException
import java.io.IOException

object AdvertViewModel {
    const val VISIBLE_ON=0
    const val VISIBLE_OFF=1
    const val FAVORITE_OFF=2
    const val FAVORITE_ON=3
    private val mutableMyAdverts = MutableLiveData<ArrayList<AdvertModel>>()
    val myAdverts:LiveData<ArrayList<AdvertModel>> get() = mutableMyAdverts
    val showAdvert = MutableLiveData<AdvertModel>()
    var enableButton = MutableLiveData<Int>()
    private var showToolBarMutable = MutableLiveData<Boolean>()
    val showToolBar: LiveData<Boolean> get() = showToolBarMutable
    var loadedMyAdverts = false
    fun changeToolBarState(state:Boolean){
        showToolBarMutable.value = state
    }
    fun addNewMyAdvertItem(advertList: AdvertModel){
        if(mutableMyAdverts.value==null) mutableMyAdverts.value = ArrayList()
        else{
            var tempList:ArrayList<AdvertModel> = mutableMyAdverts.value!!
            if(tempList.indexOf(advertList)==-1)tempList.add(advertList)
            mutableMyAdverts.value = tempList
        }
    }
    fun addNewMyAdvert(advertList:ArrayList<AdvertModel>){
        mutableMyAdverts.value = advertList
    }
    fun updateNewMyAdvert(previousAdvert:String,advert: AdvertModel):Int{
        val previous = myAdverts.value?.filter { it->
            it._id==previousAdvert
        }
        var position = -1
        if(previous?.size!! >0){
            position = myAdverts.value?.indexOf(previous[0])!!
            mutableMyAdverts.value!![position]=advert
            mutableMyAdverts.value = mutableMyAdverts.value
        }
        else if(!mutableMyAdverts.value!!.contains(advert)) mutableMyAdverts.value?.add(advert)
        return position
    }
    fun removeNewMyAdvert(position:Int){
        if(mutableMyAdverts.value!=null){
            val newList:ArrayList<AdvertModel> = mutableMyAdverts.value!!
            newList.removeAt(position)
            mutableMyAdverts.value = newList
        }
    }
    fun clearNewMyAdvert(){
        mutableMyAdverts.value = ArrayList<AdvertModel>()
    }
    fun existsNewMyAdvert(previousAdvert:String): Int {
        val previous = myAdverts.value?.filter { it->
            it._id==previousAdvert
        }
        return myAdverts.value?.indexOf(previous?.get(0) ?: "")!!
    }
    private var retroServiceAdvert: AdvertService = RetrofitInstance.getRetroFitInstance().create(
        AdvertService::class.java
    )
    fun loadAllUserAdverts(tokenAuth: UserTokenAuth, context: Context){
        loadedMyAdverts =true
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAdvert.getUserAdvert(tokenAuth.token)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    loadedMyAdverts =false
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    loadedMyAdverts =false
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: ReturnListAdvertModel? = Gson().fromJson(Gson().toJson(response.body()), ReturnListAdvertModel::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        addNewMyAdvert(body)
                    }
                }
            }else{
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? = Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main){
                        if(response.code()==401) LogOutAuth.setLogOut(true)
                        loadedMyAdverts =false
                        Toast.makeText(context,"${errorResponse?.error}", Toast.LENGTH_LONG).show()
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        loadedMyAdverts =false
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            return@launch
        }
    }
    fun addFavoriteAdvert(advert: AdvertModel, token: UserTokenAuth, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAdvert.addFavorite(advert._id, token.token)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    enableButton.value= FAVORITE_ON
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    enableButton.value= FAVORITE_ON
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: ReturnTypeSuccess? = Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccess::class.java)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, body?.success, Toast.LENGTH_SHORT).show()
                    enableButton.value= FAVORITE_ON
                }
            }else{
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? = Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main){
                        if(response.code()==401) LogOutAuth.setLogOut(true)
                        Toast.makeText(context,"${errorResponse?.error}", Toast.LENGTH_SHORT).show()
                        enableButton.value= FAVORITE_ON
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                        enableButton.value= FAVORITE_ON
                    }
                }
            }
            return@launch
        }
    }
    fun deleteFavoriteAdvert(advert: AdvertModel, token: UserTokenAuth, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAdvert.deleteFavorite(advert._id, token.token)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    enableButton.value= FAVORITE_OFF
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    enableButton.value= FAVORITE_OFF
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: ReturnTypeSuccess? = Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccess::class.java)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, body?.success, Toast.LENGTH_SHORT).show()
                    enableButton.value= FAVORITE_OFF
                }
            }else{
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? = Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main){
                        if(response.code()==401) LogOutAuth.setLogOut(true)
                        Toast.makeText(context,"${errorResponse?.error}", Toast.LENGTH_SHORT).show()
                        enableButton.value= FAVORITE_OFF
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                        enableButton.value= FAVORITE_OFF
                    }
                }
            }
            return@launch
        }
    }
    fun changeVisibility(advert: AdvertModel, token: UserTokenAuth, state:Boolean, context: Context){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceAdvert.updateAdvertVisibility(advert._id,state, token.token)
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    if(state) enableButton.value= VISIBLE_ON
                    else enableButton.value= VISIBLE_OFF
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    if(state) enableButton.value= VISIBLE_ON
                    else enableButton.value= VISIBLE_OFF
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: ReturnTypeSuccess? = Gson().fromJson(Gson().toJson(response.body()), ReturnTypeSuccess::class.java)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, body?.success, Toast.LENGTH_SHORT).show()
                    if(state) enableButton.value= VISIBLE_ON
                    else enableButton.value= VISIBLE_OFF
                }
            }else{
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? = Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main){
                        if(response.code()==401) LogOutAuth.setLogOut(true)
                        Toast.makeText(context,"${errorResponse?.error}", Toast.LENGTH_SHORT).show()
                        if(state) enableButton.value= VISIBLE_ON
                        else enableButton.value= VISIBLE_OFF
                    }
                }
                catch (e:Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(context,"Server dose not respond.", Toast.LENGTH_SHORT).show()
                        if(state) enableButton.value= VISIBLE_ON
                        else enableButton.value= VISIBLE_OFF
                    }
                }
            }
            return@launch
        }
    }
}