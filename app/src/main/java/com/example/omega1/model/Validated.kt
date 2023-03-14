package com.example.omega1.model

data class Validated(
    val validIdAndFace: Boolean,
    val validIdBack: Boolean,
    val validIdFront: Boolean,
    val validSecondIdFront: Boolean,
    val validSecondIdBack: Boolean
)