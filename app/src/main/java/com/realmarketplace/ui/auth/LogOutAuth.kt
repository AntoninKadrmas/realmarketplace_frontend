package com.realmarketplace.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object LogOutAuth {
    private var mutableLogOut = MutableLiveData<Boolean>()
    val logOut : LiveData<Boolean> get() = mutableLogOut
    fun setLogOut(state:Boolean){
        mutableLogOut.value=state
    }
}