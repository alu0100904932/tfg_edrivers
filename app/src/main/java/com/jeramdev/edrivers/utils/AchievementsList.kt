package com.jeramdev.edrivers.utils

import com.jeramdev.edrivers.data.model.AchievementModel
import com.jeramdev.edrivers.data.model.AchievementLevelModel

/**
 * Lista de logros
 * En esta lista se deben incluir todos los logros
 */
val achievementsList = listOf(
    AchievementModel(
        title = Constants.FIRST_LESSON_ACHIEVEMENT,
        category = Constants.DRIVING_LESSONS_CATEGORY,
        levels = mapOf(
            Pair(
                "level1",
                AchievementLevelModel(
                    description = "Completar la primera práctica",
                    image = ""
                )
            )
        )
    ),
    AchievementModel(
        title = Constants.PERFECT_LESSON_ACHIEVEMENT,
        category = Constants.DRIVING_LESSONS_CATEGORY,
        levels = mapOf(
            Pair(
                "level1",
                AchievementLevelModel(
                    level = 1,
                    description = "Completar 1 práctica sin fallos",
                    image = ""
                )
            ),
            Pair(
                "level2",
                AchievementLevelModel(
                    level = 2,
                    description = "Completar 5 prácticas sin fallos",
                    image = ""
                )
            ),
            Pair(
                "level3",
                AchievementLevelModel(
                    level = 3,
                    description = "Completar 10 prácticas sin fallos",
                    image = ""
                )
            )
        )
    )
)