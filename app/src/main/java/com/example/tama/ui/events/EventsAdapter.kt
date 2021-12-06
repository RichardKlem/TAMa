package com.example.tama.ui.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tama.R
import kotlinx.android.synthetic.main.events_on_main.view.*

class EventsAdapter(
    private val events: MutableList<Event>
) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun addEvent(event: Event) {
        events.add(event)
        notifyItemInserted(events.size - 1)
    }

    private fun deleteEvent(position: Int) {
        events.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.events_on_main,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val curLocation = events[position]
        holder.itemView.apply {
            tvEvent.text = curLocation.title
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }
}
