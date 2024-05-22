package com.sector.travelmanager.fragments.detail

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sector.travelmanager.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DetailFragmentArgs>()

    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                fusedLocationClient?.lastLocation
                    ?.addOnSuccessListener { location ->
                        if (location != null) {
                            val latitude = location.latitude
                            val longitude = location.longitude
                        }
                    }
            } else {
                // Если разрешение не предоставлено, можно обработать эту ситуацию соответствующим образом
                println("Пользователь отказал в доступе к геолокации")
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        binding.tvName.text = args.attraction.name
        binding.tvDescription.text = args.attraction.description
        binding.tvRating.text = args.attraction.rating.toString()
        binding.tvPlace.text = "${args.attraction.city}, ${args.attraction.area}"
        binding.tvDistance.text = "4км"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            requestLocation()
        }

        setData()

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnOpenMap.setOnClickListener {
            findNavController().navigate(
                DetailFragmentDirections.actionDetailFragmentToMapFragment(args.attraction)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setData() {
        binding.ivAttraction.load(args.attraction.image) {
            transformations(
                RoundedCornersTransformation(bottomLeft = 60f, bottomRight = 60f)
            )
        }
    }

    private fun calculateDistance(
        userLatitude: Double, userLongitude: Double,
        attractionLatitude: Double, attractionLongitude: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(userLatitude, userLongitude, attractionLatitude, attractionLongitude, results)
        return results[0] // расстояние в метрах
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        fusedLocationClient?.lastLocation
            ?.addOnSuccessListener { location ->
                if (location != null) {
                    val userLatitude = location.latitude
                    val userLongitude = location.longitude

                    val attractionLatitude = args.attraction.lat ?: 0.0
                    val attractionLongitude = args.attraction.lon ?: 0.0

                    val distanceInMeters = calculateDistance(userLatitude, userLongitude, attractionLatitude, attractionLongitude)
                    val distanceInKilometers = (distanceInMeters / 1000.0).toInt()

                    binding.tvDistance.text = "$distanceInKilometers км"
                }
            }
    }
}