package com.sector.travelmanager.fragments.states

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.sector.travelmanager.databinding.FragmentAttractionsBinding
import com.sector.travelmanager.entity.Attraction

class AttractionsFragment : Fragment() {

    private var _binding: FragmentAttractionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AttractionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttractionsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        getAttractions()

        binding.btnMap.setOnClickListener {
            findNavController().navigate(
                AttractionsFragmentDirections.actionStatesFragmentToMapFragment()
            )
        }
    }

    private fun setupRecyclerView() {
        adapter = AttractionsAdapter(
            onItemClick = { attraction ->
                onOpenAttraction(attraction)
            }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun getAttractions() {
        val database = FirebaseFirestore.getInstance()

        database.collection("attractions")
            .get()
            .addOnSuccessListener { snapshot ->
                adapter.submitList(
                    snapshot.documents.map {
                        it.toObject(Attraction::class.java)
                    }
                )
            }
            .addOnFailureListener {
                Log.d("TAG!", it.message.toString())
            }
    }

    private fun onOpenAttraction(attraction: Attraction) {
        findNavController().navigate(
            AttractionsFragmentDirections.actionAttractionsFragmentToDetailFragment(attraction)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}