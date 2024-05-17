package com.sector.travelmanager.fragments.map

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.sector.travelmanager.databinding.FragmentMapBinding
import com.sector.travelmanager.utils.zoomDown
import com.sector.travelmanager.utils.zoomUp
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.ScreenRect
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.user_location.UserLocationLayer

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var userLocationLayer: UserLocationLayer? = null

    private var cameraListener = CameraListener { p0, p1, p2, finish ->
        userLocationLayer?.resetAnchor()
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
        moveToStartPosition()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            onCurrentLocationClicked()
        }
    }

    private fun onCurrentLocationClicked() {
        getLastKnownLocation()
    }

    private fun moveToStartPosition() {
        val isLocationPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (isLocationPermissionGranted) {
            onCurrentLocationClicked()
        } else {
            animateCameraToVolgogradLocation()
        }
    }

    private fun animateCameraToVolgogradLocation() {
        animateCameraToLocation(
            Point(VOLGOGRAD_LAT, VOLGOGRAD_LON),
            DEFAULT_LOCATION_ZOOM
        )
    }

    private fun getLastKnownLocation() {

    }

    private fun animateCameraToLocation(point: Point, zoom: Float, callback: () -> Unit = {}) {
        binding.mapView.map?.move(
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
        binding.mapView.mapWindow.focusRegion
        binding.mapView.mapWindow.focusRect = ScreenRect(
            ScreenPoint(
                0.0f,
                0.0f
            ),
            ScreenPoint(
                binding.mapView.mapWindow.width().toFloat(),
                binding.mapView.mapWindow.height().toFloat()
            )
        )
    }

    private fun getDefaultPosition() {

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