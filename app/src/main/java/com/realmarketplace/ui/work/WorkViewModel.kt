package com.realmarketplace.ui.work

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.realmarketplace.model.AdvertModel
import com.realmarketplace.rest.EnumService
import com.realmarketplace.rest.RetrofitInstance
import com.realmarketplace.rest.ReturnEnable
import com.realmarketplace.rest.ReturnListAdvertModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

object WorkViewModel {
    private var mutableProductionEnable = MutableLiveData<Boolean>()
    val advertProductionEnable: LiveData<Boolean> get() = mutableProductionEnable
    private var retroServiceEnum: EnumService = RetrofitInstance.getRetroFitInstance().create(
        EnumService::class.java
    )
    @SuppressLint("ResourceType")
    fun checkEnableProduction(){
        CoroutineScope(Dispatchers.IO).launch {
            val response = try{
                retroServiceEnum.productionEnable()
            }catch (e: IOException){
                withContext(Dispatchers.Main) {
                    mutableProductionEnable.value=false
                }
                return@launch
            }catch (e: HttpException){
                withContext(Dispatchers.Main) {
                    mutableProductionEnable.value=false
                }
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                val body: ReturnEnable? =
                    Gson().fromJson(Gson().toJson(response.body()), ReturnEnable::class.java)
                withContext(Dispatchers.Main) {
                    mutableProductionEnable.value=body?.enable
                }
            }else{
                withContext(Dispatchers.Main) {
                    mutableProductionEnable.value=false
                }
            }
            return@launch
        }
    }
}