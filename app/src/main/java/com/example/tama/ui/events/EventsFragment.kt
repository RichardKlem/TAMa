package com.example.tama.ui.events

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tama.databinding.FragmentEventsBinding
import com.example.tama.helpers.getEvents
import com.example.tama.helpers.getLocations
import kotlinx.android.synthetic.main.fragment_events.*


class EventsFragment : Fragment() {

    private lateinit var locationsAdapter: EventsCardAdapter
    private lateinit var eventsViewModel: EventsViewModel
    private var _binding: FragmentEventsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        eventsViewModel =
            ViewModelProvider(this)[EventsViewModel::class.java]

        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cleanings = getEvents(this.context!!)
        val cleaningsNames = mutableListOf<String>()
        for (item in cleanings.events) {
            cleaningsNames.add(item.name)
        }
        Log.d("CLEANINGS", "cleanings: $cleaningsNames")

        locationsAdapter = EventsCardAdapter(mutableListOf(), cleanings)

        val locations = getLocations(this.context!!)

        for (l in locations.locations) {
            locationsAdapter.addLoc(l)
        }

        rvEventItems.adapter = locationsAdapter
        rvEventItems.layoutManager = LinearLayoutManager(this.context)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
