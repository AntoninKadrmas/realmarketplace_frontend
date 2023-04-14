package com.realmarketplace.ui.search

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.realmarketplace.model.UserTokenAuth
import com.google.gson.Gson
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.AdvertSearchModel
import com.realmarketplace.rest.*
import com.realmarketplace.viewModel.LoadingBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

object SearchViewModel {
    private var mutableAdvertModelSample = MutableLiveData<ArrayList<AdvertModel>>()
    val advertModelSample: LiveData<ArrayList<AdvertModel>> get() = mutableAdvertModelSample
    var loadedSampleAdvert=false
    var countAdvertSearch = 0
    var pageAdvertSearch = 0
    private var mutableAdvertModelSearch = MutableLiveData<ArrayList<AdvertModel>>()
    val advertModelSearch: LiveData<ArrayList<AdvertModel>> get() = mutableAdvertModelSearch
    fun clearSearchAdverts(){
        mutableAdvertModelSearch.value = ArrayList()
    }
    fun clearSampleAdverts(){
        mutableAdvertModelSample.value = ArrayList()
    }
    fun setAdvertModelSample(advertList: ArrayList<AdvertModel>){
        mutableAdvertModelSample.value = advertList
    }
    fun setAdvertModelSearch(advertList: ArrayList<AdvertModel>){
        mutableAdvertModelSearch.value = advertList
    }
    fun updateAdvertModelSearch(advertList: ArrayList<AdvertModel>){
        if(advertModelSearch.value==null) mutableAdvertModelSearch.value=ArrayList()
        val tempAdvertSearch= advertModelSearch.value!!
        for(advert in advertList){
            tempAdvertSearch.add(advert)
        }
        mutableAdvertModelSearch.value=tempAdvertSearch
    }
    private var retroServiceAdvert: AdvertService = RetrofitInstance.getRetroFitInstance().create(
        AdvertService::class.java
    )
    fun loadAdvertSample(token: UserTokenAuth,context: Context) {
        loadedSampleAdvert=true
        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                retroServiceAdvert.getAdvertSample(token.token)
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    LoadingBar.mutableHideLoading.value=true
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    LoadingBar.mutableHideLoading.value=true
                }
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val body: ReturnListAdvertModel? =
                    Gson().fromJson(Gson().toJson(response.body()), ReturnListAdvertModel::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        setAdvertModelSample(body)
                        LoadingBar.mutableHideLoading.value=true
                    }
                }
            } else {
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? =
                        Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "${errorResponse?.error}", Toast.LENGTH_LONG).show()
                        LoadingBar.mutableHideLoading.value=true
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT)
                            .show()
                        LoadingBar.mutableHideLoading.value=true
                    }
                }
            }
            return@launch
        }
    }
    fun loadAdvertSearch(search:String,firstPage:Boolean,token: UserTokenAuth,context: Context) {
        if(firstPage) pageAdvertSearch=0
        else pageAdvertSearch++
        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                retroServiceAdvert.getAdvertSearch(search,pageAdvertSearch,token.token)
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No internet connection.", Toast.LENGTH_SHORT).show()
                    LoadingBar.mutableHideLoading.value=true
                }
                return@launch
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Http request rejected.", Toast.LENGTH_SHORT).show()
                    LoadingBar.mutableHideLoading.value=true
                }
                return@launch
            }
            if (response.isSuccessful && response.body() != null) {
                val body: AdvertSearchModel =
                    Gson().fromJson(Gson().toJson(response.body()), AdvertSearchModel::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        if(firstPage)setAdvertModelSearch(body.advert)
                        else updateAdvertModelSearch(body.advert)
                        if(body.count.size>0)countAdvertSearch=body.count[0]
                        else countAdvertSearch=0
                        LoadingBar.mutableHideLoading.value=true
                    }
                }
            } else {
                val errorBody = response.errorBody()
                try {
                    val errorResponse: ReturnTypeError? =
                        Gson().fromJson(errorBody?.charStream(), ReturnTypeError::class.java)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "${errorResponse?.error}", Toast.LENGTH_LONG).show()
                        LoadingBar.mutableHideLoading.value=true
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Server dose not respond.", Toast.LENGTH_SHORT)
                            .show()
                        LoadingBar.mutableHideLoading.value=true
                    }
                }
            }
            return@launch
        }
    }
}