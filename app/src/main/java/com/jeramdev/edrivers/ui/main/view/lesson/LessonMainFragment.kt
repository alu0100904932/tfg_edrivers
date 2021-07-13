package com.jeramdev.edrivers.ui.main.view.lesson

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.GeoPoint
import com.jeramdev.edrivers.data.model.DrivingLessonModel
import com.jeramdev.edrivers.data.model.Marker
import com.jeramdev.edrivers.data.repository.DrivingLessonsRepository
import com.jeramdev.edrivers.data.repository.UsersRepository
import com.jeramdev.edrivers.databinding.FragmentLessonMainBinding
import com.jeramdev.edrivers.ui.main.view.main.HomeFragment
import com.jeramdev.edrivers.ui.main.view.main.MainActivity
import com.jeramdev.edrivers.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class LessonMainFragment(private val student: String, private val drivingInstructor: String) :
    Fragment() {
    private var _binding: FragmentLessonMainBinding? = null
    private val binding get() = _binding!!

    private var isRunning: Boolean = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationEnable = false

    private val coordinates: MutableList<GeoPoint> = mutableListOf()
    private val markers: MutableList<Marker> = mutableListOf()
    private var durationInMillis: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Comprobar permisos
        locationEnable = false
        if (hasLocationPermission(activity as MainActivity)) {
            locationEnable = true
        } else {
            requestLocationPermission(activity as MainActivity)
        }

        // Si no tiene activa la localización vuelve al menú home
        if (!locationEnable) {
            (activity as MainActivity).makeCurrentFragment(HomeFragment())
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity as MainActivity)

        // Setup Buttons
        setupButtons(
            startButtonEnable = true,
            pauseButtonEnable = false,
            stopButtonEnable = false
        )

        // Botón play
        binding.lessonPlayButton.setOnClickListener { startLesson() }

        // Botón pausa
        binding.lessonPauseButton.setOnClickListener { pauseLesson() }

        // Botón stop
        binding.lessonStopButton.setOnClickListener { stopLesson() }

        // Botones de las faltas
        setupFaultButtons()
    }

    private fun setupButtons(
        startButtonEnable: Boolean,
        pauseButtonEnable: Boolean,
        stopButtonEnable: Boolean
    ) {
        binding.lessonPlayButton.isEnabled = startButtonEnable
        binding.lessonPauseButton.isEnabled = pauseButtonEnable
        binding.lessonStopButton.isEnabled = stopButtonEnable
    }

    private fun startLesson() {
        (activity as MainActivity).menuDisable()
        setupButtons(
            startButtonEnable = false,
            pauseButtonEnable = true,
            stopButtonEnable = true
        )
        clearCoordinatesList()
        isRunning = true
        updateLocationsOn()
        startTime()
    }

    private fun pauseLesson() {
        setupButtons(startButtonEnable = true, pauseButtonEnable = false, stopButtonEnable = true)
    }

    // Finaliza la práctica
    private fun stopLesson() {
        (activity as MainActivity).menuEnable()
        setupButtons(
            startButtonEnable = true,
            pauseButtonEnable = false,
            stopButtonEnable = false
        )
        isRunning = false
        updateLocationsOff()
        val drivingLesson = DrivingLessonModel(
            id = "${student}_${getDateTimeFromMillis(getCurrentTimeInMillis())}",
            student = student,
            drivingInstructor = drivingInstructor,
            dateTimeInMillis = getCurrentTimeInMillis(),
            duration = formatTime(durationInMillis),
            points = calculatePoints(durationInMillis),
            coordinates = coordinates,
            markers = markers
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Añade la práctica a la base de datos
                DrivingLessonsRepository().addDrivingLesson(drivingLesson)
                // Suma los puntos de la práctica al usuario
                UsersRepository().updateTotalPoints(drivingLesson.student, drivingLesson.points)
                // Comprobar los logros de las prácticas
                checkAchievements(drivingLesson.student, Constants.DRIVING_LESSONS_CATEGORY)
            } catch (e: FirebaseFirestoreException) {
                (activity as MainActivity).runOnUiThread {
                    showErrorAlert(e.message.toString(), activity as MainActivity)
                }
            }
        }
        // Al finalizar muestra la información de la práctica
        val mainActivity = activity as MainActivity
        mainActivity.makeCurrentFragment(LessonInfoFragment(drivingLesson.id))
        mainActivity.menuUnselectAll()
    }

    /**
     * Calcula los puntos de la práctica
     * Otorga 1 punto por cada segundo
     * Resta 180 puntos por cada fallo
     * Lo cual hace que si tienes 20 fallos no tengas puntos
     * No se pueden tener puntos negativos
     */
    private fun calculatePoints(durationInMillis: Long): Int {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis)
        val points = seconds.toInt() - markers.size * Constants.FAULT_VALUE
        return if (points > 0) points else 0
    }

    private fun clearCoordinatesList() {
        coordinates.clear()
    }

    private fun addCoordinate(location: Location) {
        val pos = GeoPoint(location.latitude, location.longitude)
        coordinates.add(pos)
    }

    private fun setupFaultButtons() {
        binding.speedFault.setOnClickListener { addMarker("speedFault", "Velocidad") }
        binding.stopFault.setOnClickListener { addMarker("stopFault", "Stop") }
        binding.lightsFault.setOnClickListener { addMarker("lightsFault", "Luces") }
        binding.carFault.setOnClickListener { addMarker("carFault", "Preparación") }
        binding.trafficLightFault.setOnClickListener { addMarker("trafficLightFault", "Semáforo") }
        binding.parkingFault.setOnClickListener { addMarker("parkingFault", "Aparcamiento") }
        binding.passedFault.setOnClickListener { addMarker("passedFault", "Adelantamiento") }
        binding.yieldFault.setOnClickListener { addMarker("yieldFault", "Ceda el paso") }
        binding.moreFaults.setOnClickListener { /* TODO para más faltas */ }

    }

    @SuppressLint("MissingPermission")
    private fun addMarker(type: String, title: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val location = fusedLocationProviderClient.lastLocation.await()
            val marker = Marker(GeoPoint(location.latitude, location.longitude), type, title)
            markers.add(marker)
        }
    }

    // Funciones timer
    private fun startTime() {
        durationInMillis = 0L
        var lapTime = 0L
        val timeStarted = System.currentTimeMillis()
        val timeRun = 0L
        CoroutineScope(Dispatchers.Main).launch {
            while (isRunning) {
                lapTime = System.currentTimeMillis() - timeStarted
                durationInMillis = timeRun + lapTime
                binding.lessonTime.text = formatTime(durationInMillis)
                delay(Constants.TIMER_DELAY)
            }
            durationInMillis = timeRun + lapTime
            binding.lessonTime.text = formatTime(durationInMillis)
        }
    }

    // Funciones localización
    @SuppressLint("MissingPermission")
    private fun updateLocationsOn() {
        val request = LocationRequest.create().apply {
            interval = Constants.LOCATION_UPDATE_INTERVAL
            fastestInterval = Constants.FASTEST_LOCATION_INTERVAL
            priority = PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun updateLocationsOff() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isRunning) {
                for (location in result.locations) {
                    addCoordinate(location)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationEnable = true
                } else {
                    Toast.makeText(
                        activity as MainActivity,
                        "Es necesario activar los permisos de localización desde los ajustes",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else -> {
            }
        }
    }
}