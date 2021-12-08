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
import com.example.tama.R
import com.example.tama.data.entity.Cleaning
import com.example.tama.data.fetch.DataFetcher
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventsAdapter = EventsAdapter(mutableListOf())

        rvEventItems.adapter = eventsAdapter
        rvEventItems.layoutManager = LinearLayoutManager(this.context)

        val event = Event("Bozetechova", getString(R.string.date), getString(R.string.startTime), getString(R.string.endTime))
        eventsAdapter.addEvent(event)
        val event2 = Event("Ceska", getString(R.string.date_early), getString(R.string.startTime), getString(R.string.endTime))
        eventsAdapter.addEvent(event2)
        val event3 = Event("Grohova", getString(R.string.date), getString(R.string.startTime), getString(R.string.endTime))
        eventsAdapter.addEvent(event3)
        val event4 = Event("Konecneho namesti", getString(R.string.date_early), getString(R.string.startTime), getString(R.string.endTime))
        eventsAdapter.addEvent(event4)
        val event5 = Event("Hlavni nadrazi", getString(R.string.date), getString(R.string.startTime), getString(R.string.endTime))
        eventsAdapter.addEvent(event5)
        val event6 = Event("Antoninska", getString(R.string.date_early), getString(R.string.startTime), getString(R.string.endTime))
        eventsAdapter.addEvent(event6)
        val event7 = Event("Uvoz", getString(R.string.date), getString(R.string.startTime), getString(R.string.endTime))
        eventsAdapter.addEvent(event7)
        val event8 = Event("Masarykova", getString(R.string.date_early), getString(R.string.startTime), getString(R.string.endTime))
        eventsAdapter.addEvent(event8)

//        TODO - implement fetchData() call
//        val cleanings = listOf<Cleaning>()
//        DataFetcher.fetchData(view.context, "2021-10-24T22:00:00.000Z", "2021-11-01T22:59:59.999Z", { cleanings -> })
//
//        var i = 0
//        for (item in cleanings) {
//            if (i > 9) break
//            i++
//            val eventName = item.name
//            if (eventName.isNotEmpty()) {
//                val event = Event(eventName)
//                eventsAdapter.addEvent(event)
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
