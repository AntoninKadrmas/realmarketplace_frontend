package com.realmarketplace.model

import com.google.gson.annotations.SerializedName

/**
 * A group of *models*.
 *
 * Model for adverts.
 *
 * @param _id id of the book advert
 * @param title is title of the book advert
 * @param author author that wrote the book
 * @param description description of the book advert
 * @param condition condition of the book advert
 * @param price price of the book advert
 * @param priceOption price option of book advert
 * @param genreName genre name of book advert
 * @param genreType fiction or nonfiction
 * @param createdIn time when the book advert was created
 * @param mainImageUrl the main image url that would be displayed in search
 * @param visible decide if the book advert is visible for other users or not
 * @param imagesUrls all image urls of book advert
 * @param user user that created the advert (viz. LightUser)
 *
 */
data class AdvertModel(
    var _id: String,
    val title: String,
    val author: String,
    val description:String,
    val condition:String,
    val price:String,
    val priceOption:String,
    val genreName:String,
    val genreType:String,
    var createdIn:String?=null,
    var mainImageUrl:String,
    var visible:Boolean,
    var imagesUrls:ArrayList<String>,
    var user: LightUser?=null,
): java.io.Serializable
/**
 * A group of *models*.
 *
 * Model for search advert.
 *
 * @param advert list of AdvertModel (viz AdvertModel)
 * @param count number of all result after search
 */
data class AdvertSearchModel(
    @SerializedName("advert")
    val advert:ArrayList<AdvertModel>,
    @SerializedName("count")
    val count:ArrayList<Int>,
):java.io.Serializable
/**
 * A group of *models*.
 *
 * Model for favorite advert.
 *
 * @param advert AdvertModel (viz AdvertModel)
 */
data class FavoriteAdvertModel(
    var advert: AdvertModel
):java.io.Serializable