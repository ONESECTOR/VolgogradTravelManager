package com.sector.travelmanager.fragments.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.RoundedCornersTransformation
import com.sector.travelmanager.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<DetailFragmentArgs>()

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

        setData()

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnOpenMap.setOnClickListener {
            findNavController().navigate(
                DetailFragmentDirections.actionDetailFragmentToMapFragment()
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
}