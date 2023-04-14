package com.realmarketplace.ui.favorite

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.UserTokenAuth
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.realmarketplace.rest.AdvertService
import com.realmarketplace.rest.RetrofitInstance
import com.realmarketplace.rest.ReturnListFavoriteAdvertModel
import com.realmarketplace.rest.ReturnTypeError
import com.realmarketplace.viewModel.LoadingBar
import retrofit2.HttpException
import java.io.IOException

object FavoriteViewModel {
    private val mutableFavoriteAdverts = MutableLiveData<ArrayList<AdvertModel>>()
    val favoriteAdverts:LiveData<ArrayList<AdvertModel>> get()= mutableFavoriteAdverts
    var loadedFavoriteAdvert=false
    private var mutableFavoriteTab = MutableLiveData<Int>()
    val favoriteTab: LiveData<Int> get()= mutableFavoriteTab
    fun changeStatus(state:Int){
        mutableFavoriteTab.value=state
    }
    fun clearFavoriteAdvert(){
        mutableFavoriteAdverts.value=ArrayList()
    }
    fun addNewAdvert(advert: AdvertModel){
        if(mutableFavoriteAdverts.value!=null)
            if(!mutableFavoriteAdverts.value!!.contains(advert)) {
                var newList = mutableFavoriteAdverts.value!!
                newList.add(advert)
                mutableFavoriteAdverts.value = newList
            }
    }
    fun removeAdvert(advertId: String){
        if(mutableFavoriteAdverts.value!=null){
            println(mutableFavoriteAdverts.value)
            var newList = mutableFavoriteAdverts.value!!
            val selected =newList!!.filter {
                it._id == advertId
            }
            if(selected.isNotEmpty()){
                val position:Int = newList.indexOf(selected[0])
                if(position!=-1) {
                    newList.removeAt(position)
                    mutableFavoriteAdverts.value = newList
                }
                println(mutableFavoriteAdverts.value)
            }
        }
    }
    private var retroServiceAdvert: AdvertService = RetrofitInstance.getRetroFitInstance().create(
        AdvertService::class.java
    )
    fun loadAllFavoriteAdverts(token: UserTokenAuth, context: Context) {
        LoadingBar.mutableHideLoadingMainActivity.value=false
        loadedFavoriteAdvert =true
        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                retroServiceAdvert.getFavorite(token.token)
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    LoadingBar.mutableHideLoadingMainActivity.value=true
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    LoadingBar.mutableHideLoadingMainActivity.value=true
                }
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val body: ReturnListFavoriteAdvertModel? =
                    Gson().fromJson(Gson().toJson(response.body()), ReturnListFavoriteAdvertModel::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        val newList= ArrayList<AdvertModel>()
                        val newFavoriteList = ArrayList<String>()
                        for(value in body!!){
                            if(value.advert!=null){
                                newList.add(value.advert)
                                newFavoriteList.add(value.advert._id)
                            }
                        }
                        mutableFavoriteAdverts.value=newList
                        FavoriteObject.updateAdvertId(newFavoriteList)
                    }
                    LoadingBar.mutableHideLoadingMainActivity.value=true
                }
            } else {
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? =
                        Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "${errorResponse?.error}", Toast.LENGTH_LONG).show()
                        LoadingBar.mutableHideLoadingMainActivity.value=true
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT)
                            .show()
                        LoadingBar.mutableHideLoadingMainActivity.value=true
                    }
                }
            }
            return@launch
        }
    }

}