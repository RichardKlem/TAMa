package com.example.tama.ui.events

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tama.R
import com.example.tama.helpers.Location
import com.example.tama.helpers.LocationEvents
import kotlinx.android.synthetic.main.event_on_main.view.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class EventsCardAdapter(
    private val locations: MutableList<Location>,
    private val cleanings: LocationEvents
) : RecyclerView.Adapter<EventsCardAdapter.LocEventViewHolder>() {

    class LocEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun addLoc(loc: Location) {
        locations.add(loc)
        notifyItemInserted(locations.size - 1)
    }

    private fun deleteLoc(position: Int) {
        locations.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocEventViewHolder {
        return LocEventViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.event_on_main,
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: LocEventViewHolder, position: Int) {
        val curLocation = locations[position]
        val eventsAdapter = EventsAdapter(mutableListOf())

        val formatter = DateTimeFormatter.ofPattern("d. M. yyyy, HH.mm")

        val dateFormat = DateTimeFormatter.ofPattern("d.M.")
        val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

        val cleaningsNames = mutableListOf<String>()
        for (item in cleanings.events) {
            cleaningsNames.add(item.name)
        }

        holder.itemView.apply {
            tvEventsTitle.text = curLocation.userNaming
            val subLocations = curLocation.listOfSubLocations
            var eventFound = false

            for (item in subLocations) {
                val subLocName = curLocation.technicalName

                if (subLocName in cleaningsNames) {
                    for (it in cleanings.events) {
                        if (subLocName == it.name) {
                            eventFound = true
                            val title = it.name
                            val date = LocalDate.parse(it.from, formatter).format(dateFormat)
                            val from = LocalTime.parse(it.from, formatter).format(timeFormat)
                            val to = LocalTime.parse(it.to, formatter).format(timeFormat)
                            val event = Event(title, date, from, to)
                            eventsAdapter.addEvent(event)
                            break
                        }
                    }
                }
            }

            ibExpandEvent.setOnClickListener {
                if (clEvent.visibility == View.GONE)
                    clEvent.visibility = View.VISIBLE
                else
                    clEvent.visibility = View.GONE
            }
            clEvent.adapter = eventsAdapter
            clEvent.layoutManager = LinearLayoutManager(context)

            if (!eventFound) {
                val title = context.getString(R.string.no_event_for_location)
                val event = Event(title, " ", " ", " ")
                eventsAdapter.addEvent(event)
            }
        }
    }

    override fun getItemCount(): Int {
        return locations.size
    }
}
