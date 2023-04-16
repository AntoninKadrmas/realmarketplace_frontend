package com.realmarketplace.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * A group of *view_model*.
 *
 * Object contains logout live data for logout user from application.
 */
object LogOutAuth {
    var mutableLogOutMain = MutableLiveData<Boolean>()
    var mutableLogOutUser = MutableLiveData<Boolean>()
    var mutableLogOutAdvert = MutableLiveData<Boolean>()
}