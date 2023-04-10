package com.realmarketplace.model

data class Validated(
    val validID:Boolean,
    val validEmail:Boolean,
    val validSMS:Boolean,
):java.io.Serializable