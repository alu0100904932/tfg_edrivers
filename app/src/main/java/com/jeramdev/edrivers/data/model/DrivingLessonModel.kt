package com.jeramdev.edrivers.data.model

import com.google.firebase.firestore.GeoPoint

data class DrivingLessonModel(
    val id: String = "",    // [correo del estudiante]_[fecha]_[hora]
    val student: String = "",
    val drivingInstructor: String = "",
    val drivingSchool: String = "",
    val dateTimeInMillis: Long = 0L,
    val duration: String = "",
    val points: Int = 0,
    val coordinates: List<GeoPoint>? = null,
    val markers: List<Marker>? = null
)