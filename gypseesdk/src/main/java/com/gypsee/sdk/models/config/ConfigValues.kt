package com.gypsee.sdk.models.config

data class ConfigValues(
    val harshAcceleration: Double,
    val harshBraking: Double,
    val overSpeedAlertMaxDuration: Int,
    val updateTripTime: Int
)