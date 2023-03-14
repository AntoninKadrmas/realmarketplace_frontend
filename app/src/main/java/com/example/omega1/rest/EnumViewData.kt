package com.example.omega1.rest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.log

class EnumViewData:ViewModel() {
    private val mutablePriceEnum = MutableLiveData<Array<String>>()
    private val mutableGenreFictionEnum = MutableLiveData<Array<String>>()
    private val mutableNonGenreFictionEnum = MutableLiveData<Array<String>>()
    private val mutableConditionEnum = MutableLiveData<Array<String>>()
    val priceEnum: LiveData<Array<String>> get() = mutablePriceEnum
    val genreFictionEnum: LiveData<Array<String>> get() = mutableGenreFictionEnum
    val genreNonFictionEnum: LiveData<Array<String>> get() = mutableNonGenreFictionEnum
    val conditionEnum: LiveData<Array<String>> get() = mutableConditionEnum
    fun updatePriceEnum(item: Array<String>) {
        println(item)
        mutablePriceEnum.value = item
    }
    fun updateGenreFictionEnum(item: Array<String>) {
        mutableGenreFictionEnum.value = item
    }
    fun updateNonGenreFictionEnum(item: Array<String>) {
        mutableNonGenreFictionEnum.value = item
    }
    fun updateConditionEnum(item: Array<String>) {
        mutableConditionEnum.value = item
    }
}