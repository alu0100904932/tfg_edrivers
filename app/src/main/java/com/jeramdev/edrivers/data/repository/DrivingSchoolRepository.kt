package com.jeramdev.edrivers.data.repository

import com.jeramdev.edrivers.data.api.FirestoreServiceImpl
import com.jeramdev.edrivers.data.model.DrivingSchoolModel

class DrivingSchoolRepository {

    private val firestore = FirestoreServiceImpl()

    suspend fun getDrivingSchools(): List<DrivingSchoolModel> =
        firestore.getDrivingSchools()

    suspend fun getDrivingSchoolsNames(): List<String> =
        firestore.getDrivingSchoolsNames()

    suspend fun getDrivingSchoolByName(drivingSchoolName: String): DrivingSchoolModel? =
        firestore.getDrivingSchoolByName(drivingSchoolName)

}