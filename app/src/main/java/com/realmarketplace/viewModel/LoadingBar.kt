package com.realmarketplace.viewModel

import androidx.lifecycle.MutableLiveData

/**
 * A group of *tool*.
 *
 * Object contains loading bar live data for show and hide loading bar.
 */
object LoadingBar {
    var mutableHideLoading = MutableLiveData<Boolean>()
    var mutableHideLoadingMainActivity = MutableLiveData<Boolean>()
    var mutableHideLoadingAuthActivity = MutableLiveData<Boolean>()
    var mutableHideLoadingUpdateDeleteActivity = MutableLiveData<Boolean>()
    var mutableHideLoadingAdvertActivity = MutableLiveData<Boolean>()
    var mutableHideLoadingUserActivity = MutableLiveData<Boolean>()
    var mutableHideLoadingUserSettingsActivity = MutableLiveData<Boolean>()
}