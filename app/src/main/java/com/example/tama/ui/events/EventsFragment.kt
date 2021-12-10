package com.example.tama.ui.events

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tama.databinding.FragmentEventsBinding
import com.example.tama.helpers.getEvents
import kotlinx.android.synthetic.main.fragment_events.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class EventsFragment : Fragment() {

    private lateinit var eventsAdapter: EventsAdapter
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

        eventsAdapter = EventsAdapter(mutableListOf())

        rvEventItems.adapter = eventsAdapter
        rvEventItems.layoutManager = LinearLayoutManager(this.context)

        val formatter = DateTimeFormatter.ofPattern("d. M. yyyy, HH.mm")

        val dateFormat = DateTimeFormatter.ofPattern("d.M.")
        val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

        val cleanings = getEvents(this.context!!)

        for (item in cleanings.events) {
            val name = item.name
            val date = LocalDate.parse(item.from, formatter).format(dateFormat)
            val from = LocalTime.parse(item.from, formatter).format(timeFormat)
            val to = LocalTime.parse(item.to, formatter).format(timeFormat)
            val event = Event(name, date, from, to)
//            TODO - getLocations() a porovnavat item.name s location.sublocation.name
            eventsAdapter.addEvent(event)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
