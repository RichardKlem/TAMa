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
import com.example.tama.helpers.GPS
import com.example.tama.helpers.SubLocation
import com.example.tama.helpers.insertLocation
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
        locationAdapter.loadLocations(requireContext())

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

        // This is for mocked data. Once we have map integrated, then no need of that.
        var cnt = 0
        btnAddLocation.setOnClickListener {
            val locationName =
                if (cnt % 2 == 1) getString(R.string.placeholder_location_name) else getString(R.string.placeholder_location_name) + "-- area"
            val subLocations = if (cnt % 2 == 1) listOf(SubLocation(locationName)) else listOf(
                SubLocation(locationName + "1"), SubLocation(locationName + "2")
            )
            cnt++
            insertLocation(
                requireContext(),
                locationName,
                locationName,
                GPS(50.0, 14.0),
                0,
                subLocations
            )
            locationAdapter.loadLocations(requireContext())
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
