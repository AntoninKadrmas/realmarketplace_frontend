package com.realmarketplace.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * A group of *view_model*.
 *
 * Object contains logout live data for logout user from application.
 */
object LogOutAuth {
    private var mutableLogOut = MutableLiveData<Boolean>()
    val logOut : LiveData<Boolean> get() = mutableLogOut
    /**
     * A group of *view_model_function*.
     *
     * Function used to set and trigger logout boolean.
     *
     * @param state boolean variable on which is logout variable going to be set
     */
    fun setLogOut(state:Boolean){
        mutableLogOut.value=state
    }
}