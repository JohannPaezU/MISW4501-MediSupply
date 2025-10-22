package com.mfpe.medisupply.ui.comercial

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mfpe.medisupply.databinding.FragmentComRutasBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.*

class ComRutasFragment : Fragment() {

    private var _binding: FragmentComRutasBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocationMarker: Marker? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineLocationGranted || coarseLocationGranted) {
            getUserLocation()
        } else {
            Toast.makeText(
                requireContext(),
                "Permiso de ubicación denegado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComRutasBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osmdroid", 0)
        )

        setupMap()
        setupDatePicker()
        checkLocationPermission()

        return root
    }

    private fun setupMap() {
        binding.mapView.apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(15.0)
        }
    }

    private fun setupDatePicker() {
        binding.inputVisitDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    binding.inputVisitDate.setText(dateFormat.format(calendar.time))
                    loadVisitLocations(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getUserLocation()
            }
            else -> {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userGeoPoint = GeoPoint(location.latitude, location.longitude)
                binding.mapView.controller.setCenter(userGeoPoint)

                // Agregar marcador de ubicación del usuario
                if (userLocationMarker == null) {
                    userLocationMarker = Marker(binding.mapView)
                    userLocationMarker?.apply {
                        position = userGeoPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Mi ubicación"
                        binding.mapView.overlays.add(this)
                    }
                } else {
                    userLocationMarker?.position = userGeoPoint
                }

                binding.mapView.invalidate()
            } else {
                Toast.makeText(
                    requireContext(),
                    "No se pudo obtener la ubicación.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadVisitLocations(date: String) {
        // TODO: Aquí se debe hacer el request al API con la fecha seleccionada
        // Por ahora usamos datos hardcodeados para probar

        // Limpiar marcadores existentes excepto el del usuario
        binding.mapView.overlays.removeAll { it is Marker && it != userLocationMarker }

        // Datos de prueba hardcodeados (ubicaciones en Bogotá)
        val visitLocations = listOf(
            VisitLocation("Cliente 1", 4.6533, -74.0836),
            VisitLocation("Cliente 2", 4.6097, -74.0817),
            VisitLocation("Cliente 3", 4.6351, -74.0703),
            VisitLocation("Cliente 4", 4.6482, -74.0587)
        )

        // Agregar marcadores al mapa
        visitLocations.forEach { location ->
            val marker = Marker(binding.mapView)
            marker.apply {
                position = GeoPoint(location.latitude, location.longitude)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = location.name
                snippet = "Lat: ${location.latitude}, Lon: ${location.longitude}"
                binding.mapView.overlays.add(this)
            }
        }

        binding.mapView.invalidate()

        Toast.makeText(
            requireContext(),
            "Cargadas ${visitLocations.size} ubicaciones para $date",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Data class para las ubicaciones de visita
    data class VisitLocation(
        val name: String,
        val latitude: Double,
        val longitude: Double
    )
}
