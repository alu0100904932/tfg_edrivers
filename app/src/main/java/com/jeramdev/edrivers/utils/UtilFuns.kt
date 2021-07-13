package com.jeramdev.edrivers.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Muestra una ventana con un mensaje de error
 */
fun showErrorAlert(message: String, context: Context) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
    builder.setTitle("Error")
    builder.setMessage(message)
    builder.setPositiveButton("Aceptar", null)
    val dialog: AlertDialog = builder.create()
    dialog.show()
}

/**
 * Comprueba que la contraseña introducida sea válida
 */
fun validatePassword(password: String) = password.length >= Constants.PASSWORD_MIN_LENGTH

// DateTime

fun formatTime(ms: Long): String {
    var milliseconds = ms
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    milliseconds -= TimeUnit.HOURS.toMillis(hours)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
    milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
    return "${if (hours < 10) "0" else ""}$hours:" +
            "${if (minutes < 10) "0" else ""}$minutes:" +
            "${if (seconds < 10) "0" else ""}$seconds"
}

fun getCurrentTimeInMillis(): Long {
    TimeZone.setDefault(TimeZone.getTimeZone(Constants.TIME_ZONE))
    return Calendar.getInstance().timeInMillis
}

fun getDateTimeFromMillis(ms: Long): String {
    TimeZone.setDefault(TimeZone.getTimeZone(Constants.TIME_ZONE))
    val dateTime = SimpleDateFormat("dd.MM.yyyy_HH:mm:ss", Locale("es", "ES"))
    return dateTime.format(ms)
}

fun getDateFromMillis(ms: Long): String {
    TimeZone.setDefault(TimeZone.getTimeZone(Constants.TIME_ZONE))
    val date = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    return date.format(ms)
}

fun getTimeFromMillis(ms: Long): String {
    TimeZone.setDefault(TimeZone.getTimeZone(Constants.TIME_ZONE))
    val time = SimpleDateFormat("HH:mm:ss", Locale("es", "ES"))
    return time.format(ms)
}

/**
 * Comprueba si el usuario tiene activos los permisos de localización
 */
fun hasLocationPermission(activity: Activity) =
    ContextCompat.checkSelfPermission(
        activity,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

/**
 * Solicita los permisos de localización
 */
fun requestLocationPermission(activity: Activity) {
    if (ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    ) {
        Toast.makeText(
            activity,
            "Es necesario activar los permisos de localización desde los ajustes",
            Toast.LENGTH_SHORT
        ).show()
    } else {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            Constants.REQUEST_CODE_LOCATION
        )
    }
}

