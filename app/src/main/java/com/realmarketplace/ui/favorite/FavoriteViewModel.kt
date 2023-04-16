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
import com.realmarketplace.ui.auth.LogOutAuth
import com.realmarketplace.viewModel.LoadingBar
import retrofit2.HttpException
import java.io.IOException

/**
 * A group of *view_model*.
 *
 * Take care of favorite advert list of AdvertModel.
 * Operates over some of the advert endpoints.
 */
object FavoriteViewModel {
    private val mutableFavoriteAdverts = MutableLiveData<ArrayList<AdvertModel>>()
    val favoriteAdverts:LiveData<ArrayList<AdvertModel>> get()= mutableFavoriteAdverts
    var loadedFavoriteAdvert=false
    private var mutableFavoriteTab = MutableLiveData<Int>()
    val favoriteTab: LiveData<Int> get()= mutableFavoriteTab
    /**
     * A group of *view_model_function*.
     *
     * Function used to change favorite tab position.
     */
    fun changeStatus(position:Int){
        mutableFavoriteTab.value=position
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to change favorite advert list to empty advert list.
     */
    fun clearFavoriteAdvert(){
        mutableFavoriteAdverts.value=ArrayList()
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to add new advert in favorite advert list.
     *
     * @param advert that would be added in to favorite advert list viz. AdvertModel
     */
    fun addNewAdvert(advert: AdvertModel){
        if(mutableFavoriteAdverts.value!=null)
            if(!mutableFavoriteAdverts.value!!.contains(advert)) {
                var newList = mutableFavoriteAdverts.value!!
                newList.add(advert)
                mutableFavoriteAdverts.value = newList
            }
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to deleted advert in favorite advert list by specific advert id.
     *
     * @param advertId id of advert that would be deleted
     */
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
    /**
     * A group of *view_model_function*.
     *
     * Function used to call getFavorite function and handle the response.
     *
     * @param userToken user authentication token viz. UserTokenAuth
     * @param context context of activity or fragment where is function called
     */
    fun loadAllFavoriteAdverts(userToken: UserTokenAuth, context: Context) {
        LoadingBar.mutableHideLoadingMainActivity.value=false
        loadedFavoriteAdvert =true
        CoroutineScope(Dispatchers.IO).launch {
            val response = try {
                retroServiceAdvert.getFavorite(userToken.token)
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
                        if(response.code()==401)LogOutAuth.mutableLogOutMain.value=true
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