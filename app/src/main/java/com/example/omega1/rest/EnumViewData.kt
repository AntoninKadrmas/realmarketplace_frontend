package com.example.omega1.rest

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.omega1.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import kotlin.math.log

class EnumViewData:ViewModel() {
    private val mutablePriceEnum = MutableLiveData<Array<String>>()
    private val mutableGenreEnum = MutableLiveData<ArrayList<GenreItem>>()
    private val mutableConditionEnum = MutableLiveData<Array<String>>()
    val priceEnum: LiveData<Array<String>> get() = mutablePriceEnum
    val genreGenreEnum: LiveData<ArrayList<GenreItem>> get() = mutableGenreEnum
    val conditionEnum: LiveData<Array<String>> get() = mutableConditionEnum
    fun updatePriceEnum(item: Array<String>) {
        mutablePriceEnum.value = item
    }
    fun updateGenreEnum(item: ArrayList<GenreItem>) {
        mutableGenreEnum.value = item
    }
    fun updateConditionEnum(item: Array<String>) {
        mutableConditionEnum.value = item
    }
    private val retroServiceEnum:EnumService = RetrofitInstance.getRetroFitInstance().create(EnumService::class.java)

    @SuppressLint("ResourceType")
    fun loadPriceEnum(){
         CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceEnum.getPrice()
            }catch (e: IOException){
                return@launch
            }catch (e: HttpException){
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                withContext(Dispatchers.Main) {
                    updatePriceEnum(response.body()!!.toTypedArray())
                }
            }else{
            }
            return@launch
        }
    }
    @SuppressLint("ResourceType")
    fun loadConditionEnum(){
         CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceEnum.getBookCondition()
            }catch (e: IOException){
                return@launch
            }catch (e: HttpException){
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                withContext(Dispatchers.Main) {
                    updateConditionEnum(response.body()!!.toTypedArray())
                }
            }else{
            }
            return@launch
        }
    }
    @SuppressLint("ResourceType")
    fun loadGenreEnum(){
         CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceEnum.getGenres()
            }catch (e: IOException){
                return@launch
            }catch (e: HttpException){
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                withContext(Dispatchers.Main) {
                    updateGenreEnum(response.body()!!)
                }
            }
            return@launch
        }
    }
}