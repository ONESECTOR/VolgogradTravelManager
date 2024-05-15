package com.sector.travelmanager.fragments.states

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sector.travelmanager.databinding.FragmentListBinding
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.sector.travelmanager.entity.Attraction

class StatesFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: StatesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        getAttractions()
    }

    private fun setupRecyclerView() {
        adapter = StatesAdapter(
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
            StatesFragmentDirections.actionStatesFragmentToDetailFragment(attraction)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}