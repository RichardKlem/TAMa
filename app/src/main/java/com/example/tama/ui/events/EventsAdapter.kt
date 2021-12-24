package com.example.tama.ui.events

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.tama.R
import kotlinx.android.synthetic.main.events_in_card.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventsAdapter(
    private val events: MutableList<Event>
) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun addEvent(event: Event) {
        events.add(event)
        notifyItemInserted(events.size - 1)
//        notifyDataSetChanged()
    }

    private fun deleteEvent(position: Int) {
        events.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.events_in_card,
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val curLocation = events[position]
        holder.itemView.apply {
            tvEvent.text = curLocation.title
            tvDate.text = curLocation.date
            tvTime.text = curLocation.time

            val date = LocalDateTime.now()
            val currentDateFormat = DateTimeFormatter.ofPattern("d.M.")
            val currentDate = date.format(currentDateFormat)
            if(tvDate.text == currentDate.toString()) {
                clEvent.setBackgroundColor(Color.parseColor("#EC0030"))
            } else {
                clEvent.setBackgroundColor(Color.parseColor("#5BCA5F"))
            }
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }
}
