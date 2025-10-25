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
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mfpe.medisupply.databinding.FragmentComRutasBinding
import com.mfpe.medisupply.databinding.DialogVisitDetailsBinding
import com.mfpe.medisupply.utils.PrefsManager
import com.mfpe.medisupply.viewmodel.SellerViewModel
import com.mfpe.medisupply.viewmodel.VisitsViewModel
import com.mfpe.medisupply.data.model.Visit
import com.mfpe.medisupply.data.model.RegisterVisitRequest
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.*
import java.net.URL
import org.json.JSONObject
import org.osmdroid.views.CustomZoomButtonsController
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ComRutasFragment : Fragment() {

    private var _binding: FragmentComRutasBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sellerViewModel: SellerViewModel
    private lateinit var visitsViewModel: VisitsViewModel
    private var userLocationMarker: Marker? = null
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val routingScope = CoroutineScope(Dispatchers.IO + Job())

    // Tile source personalizado con estilo más limpio (CartoDB Voyager)
    private val cartoDbVoyager = object : OnlineTileSourceBase(
        "CartoDBVoyager",
        0, 22, 256, ".png",
        arrayOf(
            "https://a.basemaps.cartocdn.com/rastertiles/voyager/",
            "https://b.basemaps.cartocdn.com/rastertiles/voyager/",
            "https://c.basemaps.cartocdn.com/rastertiles/voyager/",
            "https://d.basemaps.cartocdn.com/rastertiles/voyager/"
        )
    ) {
        override fun getTileURLString(pMapTileIndex: Long): String {
            return baseUrl + MapTileIndex.getZoom(pMapTileIndex) +
                    "/" + MapTileIndex.getX(pMapTileIndex) +
                    "/" + MapTileIndex.getY(pMapTileIndex) + mImageFilenameEnding
        }
    }

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

        sellerViewModel = ViewModelProvider(this)[SellerViewModel::class.java]
        visitsViewModel = ViewModelProvider(this)[VisitsViewModel::class.java]

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
            setTileSource(cartoDbVoyager)
            setMultiTouchControls(true)
            controller.setZoom(18.0)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
            setUseDataConnection(true)
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
                binding.mapView.controller.zoomTo(18.0)

                // Agregar marcador de ubicación del usuario
                if (userLocationMarker == null) {
                    userLocationMarker = Marker(binding.mapView)
                    userLocationMarker?.apply {
                        position = userGeoPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Mi ubicación"
                        binding.mapView.overlays.add(this)
                        icon = ResourcesCompat.getDrawable(
                            resources,
                            com.mfpe.medisupply.R.drawable.ic_marker_blue,
                            null
                        )
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
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(date) ?: Date()
        val apiDate = apiDateFormat.format(calendar.time)

        binding.mapView.overlays.removeAll { it !is Marker || it != userLocationMarker }

        val authToken = PrefsManager.getInstance(requireContext()).getAuthToken
        sellerViewModel.getVisits(authToken!!, apiDate) { success, message, data ->
            if (success && data != null) {
                val pendingPoints = mutableListOf<GeoPoint>()

                data.visits.forEach { visit ->
                    val geolocation = visit.client.geolocation
                    val coordinates = geolocation.split(",").map { it.trim() }

                    if (coordinates.size == 2) {
                        try {
                            val latitude = coordinates[0].toDouble()
                            val longitude = coordinates[1].toDouble()
                            val geoPoint = GeoPoint(latitude, longitude)

                            val marker = Marker(binding.mapView)
                            marker.apply {
                                position = geoPoint
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = visit.client.name
                                snippet = "Estado: ${visit.status}<br>${if (visit.observations.isNotEmpty())"Observaciones: ${visit.observations}" else ""}"

                                icon = if (visit.status == "completed") {
                                    ResourcesCompat.getDrawable(
                                        resources,
                                        com.mfpe.medisupply.R.drawable.ic_marker_green,
                                        null
                                    )
                                } else {
                                    ResourcesCompat.getDrawable(
                                        resources,
                                        com.mfpe.medisupply.R.drawable.ic_marker_red,
                                        null
                                    )
                                }

                                if (visit.status == "pending") {
                                    setOnMarkerClickListener { _, _ ->
                                        showVisitDialog(visit)
                                        true
                                    }
                                }

                                binding.mapView.overlays.add(this)
                            }

                            if (visit.status == "pending") {
                                pendingPoints.add(geoPoint)
                            }
                        } catch (e: NumberFormatException) {
                            e.printStackTrace()
                        }
                    }
                }

                if (pendingPoints.size >= 1 && userLocationMarker?.position != null) {
                    val routePoints = mutableListOf<GeoPoint>()
                    routePoints.add(userLocationMarker!!.position)
                    routePoints.addAll(pendingPoints)
                    getRouteFromOSRM(routePoints)
                }

                binding.mapView.invalidate()

            } else {
                Toast.makeText(
                    requireContext(),
                    "Error al cargar visitas: $message",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getRouteFromOSRM(points: List<GeoPoint>) {
        routingScope.launch {
            try {
                // Construir URL para OSRM (servicio público de routing)
                val coordinates = points.joinToString(";") { "${it.longitude},${it.latitude}" }
                val urlString = "https://router.project-osrm.org/route/v1/driving/$coordinates?overview=full&geometries=geojson"

                val url = URL(urlString)
                val connection = url.openConnection()
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val response = connection.getInputStream().bufferedReader().readText()
                val jsonResponse = JSONObject(response)

                if (jsonResponse.getString("code") == "Ok") {
                    val routes = jsonResponse.getJSONArray("routes")
                    if (routes.length() > 0) {
                        val route = routes.getJSONObject(0)
                        val geometry = route.getJSONObject("geometry")
                        val coordinatesArray = geometry.getJSONArray("coordinates")
                        val duration = route.getDouble("duration")
                        val distance = route.getDouble("distance")

                        val routePoints = mutableListOf<GeoPoint>()
                        for (i in 0 until coordinatesArray.length()) {
                            val coord = coordinatesArray.getJSONArray(i)
                            val lon = coord.getDouble(0)
                            val lat = coord.getDouble(1)
                            routePoints.add(GeoPoint(lat, lon))
                        }

                        withContext(Dispatchers.Main) {
                            val routeLine = Polyline().apply {
                                outlinePaint.color = android.graphics.Color.BLACK
                                outlinePaint.strokeWidth = 10f
                                outlinePaint.alpha = 200
                                outlinePaint.strokeCap = android.graphics.Paint.Cap.ROUND
                                outlinePaint.strokeJoin = android.graphics.Paint.Join.ROUND
                                setPoints(routePoints)
                            }
                            binding.mapView.overlays.add(0, routeLine)
                            binding.mapView.invalidate()

                            val durationMinutes = (duration / 60).toInt()
                            val distanceKm = (distance / 1000).let { "%.2f".format(it) }
                            Toast.makeText(
                                requireContext(),
                                "Ruta: $distanceKm km - Duración: $durationMinutes min",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "No se pudo calcular la ruta por calles.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showVisitDialog(visit: Visit) {
        val dialogBinding = DialogVisitDetailsBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        dialogBinding.tvClientName.text = visit.client.name

        dialogBinding.btnSelectFile.setOnClickListener {
            // TODO: Implementar selección de archivo/imagen
        }

        dialogBinding.btnRegisterVisit.setOnClickListener {
            val observations = dialogBinding.inputObservations.text.toString()

            if (observations.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Por favor ingrese observaciones.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Crear el request para registrar la visita
            val registerRequest = RegisterVisitRequest(
                visitDate = Date(),
                observations = observations,
                visualEvidence = "",
                geolocation = visit.client.geolocation
            )

            val authToken = PrefsManager.getInstance(requireContext()).getAuthToken
            visitsViewModel.registerCompletedVisit( authToken!!, visit.id, registerRequest) { success, message, _ ->
                if (success) {
                    Toast.makeText(
                        requireContext(),
                        "Visita registrada exitosamente para ${visit.client.name}",
                        Toast.LENGTH_LONG
                    ).show()
                    dialog.dismiss()

                    val currentDate = binding.inputVisitDate.text.toString()
                    if (currentDate.isNotEmpty()) {
                        loadVisitLocations(currentDate)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Error al registrar visita: $message",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        dialog.show()
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
        routingScope.cancel()
        _binding = null
    }
}
