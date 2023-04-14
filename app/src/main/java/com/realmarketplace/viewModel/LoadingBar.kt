package com.realmarketplace.viewModel

import androidx.lifecycle.MutableLiveData

object LoadingBar {
    var mutableHideLoading = MutableLiveData<Boolean>()
    var mutableHideLoadingMainActivity = MutableLiveData<Boolean>()
    var mutableHideLoadingAuthActivity = MutableLiveData<Boolean>()
    var mutableHideLoadingUpdateDeleteActivity = MutableLiveData<Boolean>()
    var mutableHideLoadingAdvertActivity = MutableLiveData<Boolean>()
    var mutableHideLoadingUserActivity = MutableLiveData<Boolean>()
    var mutableHideLoadingUserSettingsActivity = MutableLiveData<Boolean>()
}