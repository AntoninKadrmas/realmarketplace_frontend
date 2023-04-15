package com.realmarketplace.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * A group of *view_model*.
 *
 * Take care of permissions.
 */
class PermissionViewModel:ViewModel() {
    private var mutableStoragePermission = MutableLiveData<Boolean>()
    val permissionStorage: LiveData<Boolean> get() = mutableStoragePermission
    private var mutableStoragePermissionAsk = MutableLiveData<Boolean>()
    val permissionStorageAsk: LiveData<Boolean> get() = mutableStoragePermissionAsk

    /**
     * A group of *view_model_function*.
     *
     * Function used to set state pf storage permission.
     * @param state if true permission is granted else permission is denied
     */
    fun setPermissionStorage(state:Boolean){
        mutableStoragePermission.value=state
    }
    /**
     * A group of *view_model_function*.
     *
     * Function used to set state pf storage permission ask.
     * @param state if true application was already ask for permission else permission wasn't asked at all
     */
    fun setPermissionStorageAsk(state:Boolean){
        mutableStoragePermissionAsk.value = state
    }

}