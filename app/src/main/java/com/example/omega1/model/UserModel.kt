package com.example.omega1.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    val createdIn: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val password: String,
    val phone: String,
    val validated: Validated
)
data class UserModelLogin(
    val email: String,
    val password: String,
)
data class UserTokenAuth(
    @SerializedName("token")
    val token:String
)