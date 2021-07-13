package com.jeramdev.edrivers.ui.main.view.lesson

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.GeoPoint
import com.jeramdev.edrivers.data.model.Marker
import com.jeramdev.edrivers.databinding.FragmentMapBinding
import com.jeramdev.edrivers.utils.Constants


class MapFragment(
    private val coordinates: List<GeoPoint>?,
    private val markers: List<Marker>?
) : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap

    private var coordinatesLatLng: MutableList<LatLng> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            map = it
            transformToLatLng()
            createPolyline()
            createMarkers()
        }
    }

    /**
     * Transforma de GeoPoint de firestore a LatLng de Google Maps
     */
    private fun transformToLatLng() {
        coordinates?.forEach { coordinate ->
            coordinatesLatLng.add(LatLng(coordinate.latitude, coordinate.longitude))
        }
    }

    /**
     * Crea la polyline en el mapa
     */
    private fun createPolyline() {
        if (coordinates != null) {
            val polylineOptions =
                PolylineOptions()
                    .addAll(coordinatesLatLng)
                    .width(Constants.POLYLINE_WIDTH)
                    .color(Color.RED)
            val polyline = map.addPolyline(polylineOptions)
            polyline.startCap = RoundCap()
            polyline.endCap = RoundCap()

            // Zoom de la cámara a la ruta
            moveCamera()
        }
    }

    /**
     * Hace zoom a la ruta en el mapa
     */
    private fun moveCamera() {
        val boundsBuilder = LatLngBounds.Builder()
        for (latLngPoint in coordinatesLatLng) boundsBuilder.include(latLngPoint)
        val routePadding = 100
        val latLngBounds = boundsBuilder.build()
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding))
    }

    /**
     * Crea los marcadores de errores
     */
    private fun createMarkers() {
        markers?.forEach { marker ->
            if (marker.latLng != null) {
                val mapMarker = MarkerOptions()
                    .position(
                        LatLng(
                            marker.latLng.latitude,
                            marker.latLng.longitude
                        )
                    )
                    .title(marker.title)
                map.addMarker(mapMarker)
            }
        }
    }

    // Override funs
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}