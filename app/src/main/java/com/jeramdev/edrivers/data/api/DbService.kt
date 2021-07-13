package com.jeramdev.edrivers.data.api

import com.jeramdev.edrivers.data.model.*

interface DbService {

    // Usuarios
    suspend fun addUser(user: UserModel)

    suspend fun getUser(email: String): UserModel?

    suspend fun updateTotalPoints(userEmail:String, points: Int)

    suspend fun getTotalPoints(userEmail: String): Int?

    suspend fun getRanking(): List<UserModel>

    suspend fun updateRankVisibility(value: Boolean, userEmail: String)

    suspend fun deleteUser(userEmail: String)

    // Autoescuela
    suspend fun getDrivingSchools(): List<DrivingSchoolModel>

    suspend fun getDrivingSchoolsNames(): List<String>

    suspend fun getDrivingSchoolByName(drivingSchoolName: String): DrivingSchoolModel?

    // Prácticas
    suspend fun getDrivingLessonsByStudent(student: String): List<DrivingLessonModel>

    suspend fun getDrivingLessonsByDrivingSchool(drivingSchoolName: String): List<DrivingLessonModel>

    suspend fun getDrivingLessonById(drivingLessonId: String): DrivingLessonModel?

    suspend fun getLastDrivingLessonByStudent(userEmail: String): DrivingLessonModel?

    suspend fun getLastDrivingLessonByDrivingSchool(drivingSchool: String): DrivingLessonModel?

    suspend fun addDrivingLesson(drivingLesson: DrivingLessonModel)

    // Logros
    suspend fun getUserAchievements (userEmail: String): List<AchievementModel>
    suspend fun completeFirstLessonAchievement(userEmail: String)
    suspend fun completePerfectLessonAchievementLevel (userEmail: String, level: Int)
}