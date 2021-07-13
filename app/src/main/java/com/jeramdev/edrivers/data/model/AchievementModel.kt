package com.jeramdev.edrivers.data.model

/**
 * Modelo de datos de un logro
 */
data class AchievementModel(
    val title: String = "",
    val category: String = "",
    val levels: Map<String, AchievementLevelModel> = emptyMap()
)
