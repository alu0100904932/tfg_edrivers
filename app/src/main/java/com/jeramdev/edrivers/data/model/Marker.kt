package com.jeramdev.edrivers.data.model

import com.google.firebase.firestore.GeoPoint

data class Marker(
    val latLng: GeoPoint? = null,
    val type: String = "",
    val title: String = ""
)
