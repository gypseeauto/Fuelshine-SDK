package com.gypsee.sdk.models

data class TripMileage(
    val expectedFuelCost: Double,
    val actualFuelCost: Double,
    val fuelPrice: Double,
    val weightagesData: String,
    val tripDistanceKm: Double,
    val epaArAiMileage: Double,
    val ecoSpeedStartRange: Double,
    val ecoSpeedEndRange: Double,
    val mileageObtained: Double,
    val adjustedMileageWithPenality: Double,
    val fuelSavingAmount: Float,
    val fuelConsumedInLiters: Double,
    val safeKmPercentage: Double,
    val alertTimings: String,
    val co2Emission: Double,
    val expectedFuelConsumedInLiters: Double,
    val penalityForAboveEcoSpeed: Double,
    val penalityForHarshAcceleration: Double,
    val penalityForHarshBreaking: Double,
    val belowEcoSpeedMin: Double,
    val potentialEcoDriveTime: Double,
    val potentialTripMileage: Double,
    val mileageLossForTheTrip: Double,
    val potentialSavingLossForTheTrip: Double,
    val fuelSaving: Boolean
)
