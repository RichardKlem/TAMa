package com.example.tama.ui.locations

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tama.MapsActivity
import com.example.tama.R
import com.example.tama.databinding.FragmentLocationBinding
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.fragment_location.*
import java.util.*
import kotlin.collections.ArrayList


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
            ViewModelProvider(this).get(LocationViewModel::class.java)

        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addresses: List<Address>
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("UserLocations", MODE_PRIVATE)
        // count is the real count - 0 means no location at all, 1 means exactly 1 location etc.
        val locationCount = sharedPreferences.getInt("count", 0)
        val locName: MutableList<String> = ArrayList()

        locationAdapter = LocationAdapter(mutableListOf())

        val newLatLong: LatLng? = arguments?.getParcelable("SelectedLatLng")

        var address: Any = newLatLong.toString()
        if (newLatLong != null) {
            addresses = geocoder.getFromLocation(
                newLatLong.latitude,
                newLatLong.longitude,
                1
            )
            address = addresses[0].getAddressLine(0)
        }

        rvLocationItems.adapter = locationAdapter
        rvLocationItems.layoutManager = LinearLayoutManager(this.context)

        // Load stored user locations.
        for (i in 0 until locationCount) {
            // The index after location is count from zero, in difference to the count.
            locName.add(sharedPreferences.getString("location_${i}", "")!!)
            locationAdapter.addLocation(
                Location(
                    sharedPreferences.getString(
                        "location_${i}",
                        ""
                    )!!
                )
            )
        }

        // If the LocationFragment is created from MapsActivity, create the new location.
        if (newLatLong != null) {
            val locationName = address.toString()
            if (locationName.isNotEmpty()) {
                val location = Location(locationName)
                locationAdapter.addLocation(location)

                // Persist the new location
                val myEdit = sharedPreferences.edit()
                myEdit.putString("location_${locationCount}", locationName)
                myEdit.putInt("count", locationCount + 1)
                myEdit.commit()
            }
        }

        btnAddLocation.setOnClickListener {
            val locationName = getString(R.string.placeholder_location_name)
            if (locationName.isNotEmpty()) {
                val location = Location(locationName)
                locationAdapter.addLocation(location)
            }
        }
        val button = binding.button2
        button.setOnClickListener { openNewActivity() }
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
