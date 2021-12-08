package com.example.tama.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tama.R
import com.example.tama.databinding.FragmentEventsBinding
import kotlinx.android.synthetic.main.fragment_events.*

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
            ViewModelProvider(this).get(EventsViewModel::class.java)

        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventsAdapter = EventsAdapter(mutableListOf())

        rvEventItems.adapter = eventsAdapter
        rvEventItems.layoutManager = LinearLayoutManager(this.context)

        val eventName = getString(R.string.placeholder_location_name)
        if (eventName.isNotEmpty()) {
            val event = Event(eventName)
            eventsAdapter.addEvent(event)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
