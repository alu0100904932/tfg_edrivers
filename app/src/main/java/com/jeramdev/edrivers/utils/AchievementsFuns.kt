package com.jeramdev.edrivers.utils

import com.jeramdev.edrivers.data.model.AchievementModel
import com.jeramdev.edrivers.data.repository.AchievementsRepository
import com.jeramdev.edrivers.data.repository.DrivingLessonsRepository
import com.jeramdev.edrivers.data.repository.UsersRepository

/**
 * Comprueba los logros del usuario
 */
suspend fun checkAchievements(userEmail: String, category: String) {
    val userAchievements = UsersRepository().getUserAchievements(userEmail)
    when (category) {
        Constants.DRIVING_LESSONS_CATEGORY -> checkDrivingLessonsAchievements(
            userEmail,
            userAchievements.filter { achievements ->
                achievements.category == Constants.DRIVING_LESSONS_CATEGORY
            }
        )
        // Aquí se añadirían más categorías
    }
}

/**
 * Compruebo los logros de las prácticas
 */
private suspend fun checkDrivingLessonsAchievements(
    userEmail: String,
    drivingLessonsAchievements: List<AchievementModel>
) {
    checkFirstLessonAchievement(
        userEmail,
        drivingLessonsAchievements.find { achievement ->
            achievement.title == Constants.FIRST_LESSON_ACHIEVEMENT
        })
    checkPerfectLessonAchievement(
        userEmail,
        drivingLessonsAchievements.find { achievement ->
            achievement.title == Constants.PERFECT_LESSON_ACHIEVEMENT
        })
}

/**
 * Método de comprobación del logro de completar la primera práctica
 */
private suspend fun checkFirstLessonAchievement(
    userEmail: String,
    firstLessonAchievement: AchievementModel?
) {
    val level = firstLessonAchievement?.levels?.get("level1")
    if (level != null) {
        if (!level.completed) {
            AchievementsRepository().completeFirstLessonAchievement(userEmail)
        }
    }
}

/**
 * Método de comprobación del logro de completar prácticas sin fallos
 */
private suspend fun checkPerfectLessonAchievement(
    userEmail: String,
    perfectLessonAchievement: AchievementModel?
) {
    if (perfectLessonAchievement != null) {
        // Comprobar el número de prácticas sin fallo
        val drivingLessons = DrivingLessonsRepository().getDrivingLessonsByStudent(userEmail)
        var perfectLessons = 0
        drivingLessons.forEach { drivingLesson ->
            if (drivingLesson.markers.isNullOrEmpty()) {
                perfectLessons++
            }
        }
        // Comprobar los niveles de los logros
        when (perfectLessons) {
            1 -> {
                val levelNumber = 1
                val level = perfectLessonAchievement.levels["level$levelNumber"]
                if (level != null) {
                    if (!level.completed) {
                        AchievementsRepository().completePerfectLessonAchievementLevel(
                            userEmail,
                            levelNumber
                        )
                    }
                }
            }
            5 -> {
                val levelNumber = 2
                val level = perfectLessonAchievement.levels["level$levelNumber"]
                if (level != null) {
                    if (!level.completed) {
                        AchievementsRepository().completePerfectLessonAchievementLevel(
                            userEmail,
                            levelNumber
                        )
                    }
                }
            }
            10 -> {
                val levelNumber = 3
                val level = perfectLessonAchievement.levels["level$levelNumber"]
                if (level != null) {
                    if (!level.completed) {
                        AchievementsRepository().completePerfectLessonAchievementLevel(
                            userEmail,
                            levelNumber
                        )
                    }
                }
            }
        }
    }
}

