package com.sector.travelmanager.fragments.map

import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.FirebaseFirestore
import com.sector.travelmanager.R
import com.sector.travelmanager.databinding.FragmentMapBinding
import com.sector.travelmanager.databinding.PlacemarkLayoutBinding
import com.sector.travelmanager.entity.Attraction
import com.sector.travelmanager.utils.zoomDown
import com.sector.travelmanager.utils.zoomUp
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.ScreenRect
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val args: MapFragmentArgs by navArgs()

    private var mapView: MapView? = null

    private var userLocationLayer: UserLocationLayer? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            enableUserLocationLayer()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapView
        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapView!!.mapWindow)

        when {
            args.attraction != null -> {
                moveToLocation()
            }
            else -> {
                addAttractionMarkers()
                animateCameraToVolgogradLocation()
            }
        }

        binding.btnNavigatePlus.setOnClickListener {
            setBaseRect()
            binding.mapView.zoomUp()
        }
        binding.btnNavigateMinus.setOnClickListener {
            setBaseRect()
            binding.mapView.zoomDown()
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnCurrentLocation.setOnClickListener {
            checkLocationPermission()
        }
    }

    private fun moveToUserLocation(userLocation: Point) {
        val maxZoom = 19.0f

        mapView?.map?.move(
            CameraPosition(userLocation, maxZoom, 0.0f, 0.0f)
        )
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                // Разрешение уже предоставлено
                enableUserLocationLayer()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Покажите объяснение пользователю
                // Затем запросите разрешение
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                // Запросите разрешение
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun enableUserLocationLayer() {
        userLocationLayer?.isVisible = true
        userLocationLayer?.isHeadingEnabled = true

        val location = userLocationLayer?.cameraPosition()?.target
        location?.let { moveToUserLocation(it) }
    }

    private fun moveToLocation() {
        val width = (mapView?.width?.times(0.5))?.toFloat() ?: 0f
        val height = (mapView?.width?.times(0.5))?.toFloat() ?: 0f
        userLocationLayer?.setAnchor(
            PointF(width, height),
            PointF(width, height)
        )

        val latitude = args.attraction?.lat ?: 0.0
        val longitude = args.attraction?.lon ?: 0.0

        val targetLocation = Point(latitude, longitude)
        mapView?.map?.move(
            CameraPosition(targetLocation, 16.0f, 0.0f, 0.0f)
        )
        val mapObjects = mapView?.map?.mapObjects
        mapObjects?.addPlacemark(targetLocation)
    }

    private fun animateCameraToVolgogradLocation() {
        animateCameraToLocation(
            Point(VOLGOGRAD_LAT, VOLGOGRAD_LON),
            DEFAULT_LOCATION_ZOOM
        )
    }

    private fun animateCameraToLocation(point: Point, zoom: Float, callback: () -> Unit = {}) {
        mapView?.map?.move(
            CameraPosition(point, zoom, 0.0f, 0.0f),
            Animation(
                Animation.Type.SMOOTH,
                1F
            )
        ) {
            callback.invoke()
        }
    }

    private fun setBaseRect() {
        val width = mapView?.mapWindow?.width()?.toFloat() ?: 0f
        val height = mapView?.mapWindow?.height()?.toFloat() ?: 0f
        mapView?.mapWindow?.focusRegion
        mapView?.mapWindow?.focusRect = ScreenRect(
            ScreenPoint(0.0f, 0.0f),
            ScreenPoint(width, height)
        )
    }

    @Suppress("DEPRECATION")
    private fun addAttractionMarkers() {
        val database = FirebaseFirestore.getInstance()

        database.collection("attractions")
            .get()
            .addOnSuccessListener { snapshot ->
                val attractions = snapshot.documents.map {
                    it.toObject(Attraction::class.java)
                }
                val mapObjects = mapView?.map?.mapObjects
                val markView = ViewProvider(PlacemarkLayoutBinding.inflate(layoutInflater).root)

                attractions.forEach { attraction ->
                    val latitude = attraction?.lat ?: 0.0
                    val longitude = attraction?.lon ?: 0.0

                    val mark = mapObjects?.addPlacemark(
                        Point(latitude, longitude)
                    )
                    mark?.setView(markView)
                    mark?.userData = attraction
                    mark?.isDraggable = true
                    mark?.setIconStyle(IconStyle().setAnchor(PointF(0.5f, 1.0f)))
                    mark?.addTapListener { _, _ ->
                        attraction?.let {
                            findNavController().navigate(
                                MapFragmentDirections.actionMapFragmentToDetailFragment(it)
                            )
                        }
                        true
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DEFAULT_LOCATION_ZOOM = 10f

        private const val VOLGOGRAD_LAT = 48.700001
        private const val VOLGOGRAD_LON = 44.516666
    }
}