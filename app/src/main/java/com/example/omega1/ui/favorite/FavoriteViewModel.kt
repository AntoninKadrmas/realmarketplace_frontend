package com.example.omega1.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoriteViewModel : ViewModel() {

    private var mutableFavoriteTab = MutableLiveData<Int>()
    val favoriteTab: LiveData<Int> get()= mutableFavoriteTab
    fun changeStatus(state:Int){
        mutableFavoriteTab.value=state
    }

}