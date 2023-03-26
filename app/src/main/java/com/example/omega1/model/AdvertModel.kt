package com.example.omega1.model

import com.google.gson.annotations.SerializedName

data class AdvertModel(
    var _id: String,
    var userId:String,
    val title: String,
    val author: String,
    val description:String,
    val condition:String,
    val price:String,
    val priceOption:String,
    val genreName:String,
    val genreType:String,
    val place:String,
    var createdIn:String,
    var mainImage:String,
    var imagesUrls:ArrayList<String>,
): java.io.Serializable
data class AdvertId(
    @SerializedName("_id")
    val _id:String
)