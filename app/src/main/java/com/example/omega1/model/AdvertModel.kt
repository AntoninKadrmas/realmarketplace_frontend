package com.example.omega1.model

import android.text.Editable
import com.google.gson.annotations.SerializedName

data class AdvertModel(
    val lightUserId:String,
    val title: String?,
    val description:String?,
    val createdIn:String,
    val condition:String?,
    val price:String?,
    val priceOption:String?,
    val genreName:String?,
    val genreType:String?,
    val place:String,
    val mainImage:String,
    val imagesUrls:ArrayList<String>,
)
data class AdvertId(
    @SerializedName("_id")
    val _id:String
)