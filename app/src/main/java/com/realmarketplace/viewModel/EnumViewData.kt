package com.realmarketplace.viewModel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.realmarketplace.rest.EnumService
import com.realmarketplace.rest.GenreItem
import com.realmarketplace.rest.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * A group of *view_model*.
 *
 * Take care of price enum, genre enum and condition enum.
 * Operates over some of the enum endpoints.
 */
class EnumViewData:ViewModel() {
    private val mutablePriceEnum = MutableLiveData<ArrayList<String>>()
    private val mutableGenreEnum = MutableLiveData<ArrayList<GenreItem>>()
    private val mutableConditionEnum = MutableLiveData<ArrayList<String>>()
    val priceEnum: LiveData<ArrayList<String>> get() = mutablePriceEnum
    val genreGenreEnum: LiveData<ArrayList<GenreItem>> get() = mutableGenreEnum
    val conditionEnum: LiveData<ArrayList<String>> get() = mutableConditionEnum
    /**
     * A group of *view_model_function*.
     *
     * Function used to update list of string in price enum.
     * @param list is list of strings that would be set as default price enum list
     */
    fun updatePriceEnum(list: ArrayList<String>) {
        mutablePriceEnum.value = list
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to update list of string in genre enum.
     * @param list is list of strings that would be set as default genre enum list
     */
    fun updateGenreEnum(list: ArrayList<GenreItem>) {
        mutableGenreEnum.value = list
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to update list of string in condition enum.
     * @param list is list of strings that would be set as default condition enum list
     */
    fun updateConditionEnum(list: ArrayList<String>) {
        mutableConditionEnum.value = list
    }
    private val retroServiceEnum: EnumService = RetrofitInstance.getRetroFitInstance()
        .create(EnumService::class.java)

    /**
     * A group of *view_model_function*.
     *
     * Function used to call getPrice function and handle the response.
     */
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
                    updatePriceEnum(response.body()!!)
                }
            }else{
            }
            return@launch
        }
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to call getBookCondition function and handle the response.
     */
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
                    updateConditionEnum(response.body()!!)
                }
            }else{
            }
            return@launch
        }
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to call getGenres function and handle the response.
     */
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