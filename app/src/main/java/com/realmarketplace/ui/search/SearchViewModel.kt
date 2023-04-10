package com.realmarketplace.ui.search

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.model.UserTokenAuth
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.realmarketplace.rest.AdvertService
import com.realmarketplace.rest.RetrofitInstance
import com.realmarketplace.rest.ReturnListAdvertModel
import com.realmarketplace.rest.ReturnTypeError
import retrofit2.HttpException
import java.io.IOException

class SearchViewModel : ViewModel() {

    private var mutableAdvertModel = MutableLiveData<ArrayList<AdvertModel>>()
    val advertModel: LiveData<ArrayList<AdvertModel>> get() = mutableAdvertModel
    fun clearSearchedAdverts(){
        mutableAdvertModel.value = ArrayList<AdvertModel>()

    }
    fun setAdvertModel(advertList: ArrayList<AdvertModel>){
        mutableAdvertModel.value = advertList
    }
    private var retroServiceAdvert: AdvertService = RetrofitInstance.getRetroFitInstance().create(
        AdvertService::class.java
    )
    fun loadAllAdverts(context: Context,token: UserTokenAuth) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                retroServiceAdvert.getAdvert(token.token)
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
                val body: ReturnListAdvertModel? =
                    Gson().fromJson(Gson().toJson(response.body()), ReturnListAdvertModel::class.java)
                withContext(Dispatchers.Main) {
                    if (body != null) {
                        setAdvertModel(body)
                    }
                }
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
}