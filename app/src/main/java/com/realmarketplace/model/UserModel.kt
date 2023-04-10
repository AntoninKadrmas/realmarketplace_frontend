package com.realmarketplace.model

data class UserModel(
    val createdIn: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val password: String,
    val phone: String,
    val validated: Validated,
    val mainImageUrl: String = ""
)
data class UserModelLogin(
    val email: String,
    val password: String,
)
data class UserTokenAuth(
    val token:String
):java.io.Serializable
data class LightUser(
    var createdIn: String,
    var email: String,
    var firstName: String,
    var lastName: String,
    var phone: String,
    var validated: Validated,
    var mainImageUrl: String
):java.io.Serializable