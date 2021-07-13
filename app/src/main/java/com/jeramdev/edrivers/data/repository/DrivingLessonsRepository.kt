package com.jeramdev.edrivers.data.repository

import com.jeramdev.edrivers.data.api.FirestoreServiceImpl
import com.jeramdev.edrivers.data.model.DrivingLessonModel

class DrivingLessonsRepository {

    private val firestore = FirestoreServiceImpl()

    suspend fun getDrivingLessonsByStudent(student: String): List<DrivingLessonModel> =
        firestore.getDrivingLessonsByStudent(student)

    suspend fun getDrivingLessonsByDrivingSchool(drivingSchoolName: String): List<DrivingLessonModel> =
        firestore.getDrivingLessonsByDrivingSchool(drivingSchoolName)

    suspend fun getDrivingLessonById(drivingLessonId: String) =
        firestore.getDrivingLessonById(drivingLessonId)

    suspend fun addDrivingLesson(drivingLesson: DrivingLessonModel) =
        firestore.addDrivingLesson(drivingLesson)

    suspend fun getLastDrivingLessonByStudent(userEmail: String) =
        firestore.getLastDrivingLessonByStudent(userEmail)

    suspend fun getLastDrivingLessonByDrivingSchool(drivingSchool: String) =
        firestore.getLastDrivingLessonByDrivingSchool(drivingSchool)

}