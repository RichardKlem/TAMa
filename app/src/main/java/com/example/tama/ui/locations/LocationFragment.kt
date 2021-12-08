package com.example.tama.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tama.R
import com.example.tama.databinding.FragmentLocationBinding
import com.example.tama.helpers.*
import kotlinx.android.synthetic.main.fragment_location.*

class LocationFragment : Fragment() {

    private lateinit var locationAdapter: LocationAdapter
    private lateinit var locationViewModel: LocationViewModel
    private var _binding: FragmentLocationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        locationViewModel =
            ViewModelProvider(this).get(LocationViewModel::class.java)

        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationAdapter = LocationAdapter(mutableListOf())

        rvLocationItems.adapter = locationAdapter
        rvLocationItems.layoutManager = LinearLayoutManager(this.context)

        // This is for mocked data. Once we have map integrated, then no need of that.
        var cnt = 0
        btnAddLocation.setOnClickListener {
            val locationName = if (cnt % 2 == 1) getString(R.string.placeholder_location_name) else getString(R.string.placeholder_location_name) + "-- area"
            val subLocations = if ( cnt % 2 == 1) listOf(SubLocation(locationName)) else  listOf(SubLocation(locationName + "1"), SubLocation(locationName + "1"))
            cnt++
            insertLocation(requireContext(),
                locationName,
                locationName,
                GPS(50.0, 14.0),
                0,
                subLocations)
            locationAdapter.loadLocations(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
