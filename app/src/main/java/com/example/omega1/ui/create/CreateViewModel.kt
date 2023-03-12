package com.example.omega1.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class CreateViewModel : ViewModel() {
    var images: LiveData<List<String>> = MutableLiveData()
    fun setImageList(list:List<String>){
        viewModelScope.launch {
            images = MutableLiveData(list)
        }
    }
}