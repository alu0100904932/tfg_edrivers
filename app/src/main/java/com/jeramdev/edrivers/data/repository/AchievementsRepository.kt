package com.jeramdev.edrivers.data.repository

import com.jeramdev.edrivers.data.api.FirestoreServiceImpl

class AchievementsRepository {

    private val firestore = FirestoreServiceImpl()

    suspend fun completeFirstLessonAchievement(userEmail: String) =
        firestore.completeFirstLessonAchievement(userEmail)

    suspend fun completePerfectLessonAchievementLevel(userEmail: String, level: Int) {
        firestore.completePerfectLessonAchievementLevel(userEmail, level)
    }

}