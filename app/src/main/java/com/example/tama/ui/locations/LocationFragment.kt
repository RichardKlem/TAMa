package com.example.tama.ui.locations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tama.R
import com.example.tama.databinding.FragmentLocationBinding
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

        btnAddLocation.setOnClickListener {
            val locationName = getString(R.string.placeholder_location_name)
            if (locationName.isNotEmpty()) {
                val location = Location(locationName)
//             3   locationAdapter.addLocation(view.context, location)
                locationAdapter.addLocation(location)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
