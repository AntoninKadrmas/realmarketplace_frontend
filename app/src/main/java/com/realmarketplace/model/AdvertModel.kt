package com.realmarketplace.model

import com.google.gson.annotations.SerializedName
data class AdvertModel(
    var _id: String,
    var userId:String?=null,
    val title: String,
    val author: String,
    val description:String,
    val condition:String,
    val price:String,
    val priceOption:String,
    val genreName:String,
    val genreType:String,
    val place:String?=null,
    var createdIn:String?=null,
    var mainImageUrl:String,
    var visible:Boolean,
    var imagesUrls:ArrayList<String>,
    var user: LightUser?=null
): java.io.Serializable
data class AdvertId(
    @SerializedName("_id")
    val _id:String
)
data class FavoriteAdvertModel(
    var advert: AdvertModel
):java.io.Serializable