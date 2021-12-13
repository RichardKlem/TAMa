package com.example.tama.ui.locations

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tama.MapsActivity
import com.example.tama.databinding.FragmentLocationBinding
import kotlinx.android.synthetic.main.fragment_location.*


class LocationFragment : Fragment() {

    private lateinit var locationAdapter: LocationAdapter
    private lateinit var locationViewModel: LocationViewModel
    private var _binding: FragmentLocationBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        locationViewModel =
            ViewModelProvider(this)[LocationViewModel::class.java]

        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationAdapter = LocationAdapter(mutableListOf())
        locationAdapter.loadLocations(requireContext())

        rvLocationItems.adapter = locationAdapter
        rvLocationItems.layoutManager = LinearLayoutManager(this.context)

        btnAddLocation.setOnClickListener { openNewActivity() }
    }

    private fun openNewActivity() {
        val intent = Intent(requireActivity(), MapsActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
