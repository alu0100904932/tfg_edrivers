package com.jeramdev.edrivers.data.model

/**
 * Modelo de datos del nivel de un logro
 */
data class AchievementLevelModel(
     val level: Int = 1,
     val description: String = "",
     val image: String = "",
     val completed: Boolean = false
)
