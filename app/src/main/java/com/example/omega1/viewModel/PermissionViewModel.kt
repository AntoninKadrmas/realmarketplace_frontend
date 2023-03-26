package com.example.omega1.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionViewModel:ViewModel() {
    private var mutableStoragePermission = MutableLiveData<Boolean>()
    val permissionStorage: LiveData<Boolean> get() = mutableStoragePermission
    private var mutableStoragePermissionAsk = MutableLiveData<Boolean>()
    val permissionStorageAsk: LiveData<Boolean> get() = mutableStoragePermissionAsk
    fun setPermissionStorage(state:Boolean){
        mutableStoragePermission.value=state
    }
    fun setPermissionStorageAsk(state:Boolean){
        mutableStoragePermissionAsk.value = state
    }

}