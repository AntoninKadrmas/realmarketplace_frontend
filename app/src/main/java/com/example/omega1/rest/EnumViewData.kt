package com.example.omega1.rest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.log

class EnumViewData:ViewModel() {
    private val mutablePriceEnum = MutableLiveData<Array<String>>()
    private val mutableGenreEnum = MutableLiveData<ArrayList<GenreItem>>()
    private val mutableConditionEnum = MutableLiveData<Array<String>>()
    val priceEnum: LiveData<Array<String>> get() = mutablePriceEnum
    val genreGenreEnum: LiveData<ArrayList<GenreItem>> get() = mutableGenreEnum
    val conditionEnum: LiveData<Array<String>> get() = mutableConditionEnum
    fun updatePriceEnum(item: Array<String>) {
        mutablePriceEnum.value = item
    }
    fun updateGenreEnum(item: ArrayList<GenreItem>) {
        mutableGenreEnum.value = item
    }
    fun updateConditionEnum(item: Array<String>) {
        mutableConditionEnum.value = item
    }
}