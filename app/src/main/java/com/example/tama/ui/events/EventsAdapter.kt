package com.example.tama.ui.events

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.tama.R
import kotlinx.android.synthetic.main.events_on_main.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random.Default.nextBoolean

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
            if(tvDate.text == currentDate.toString())
                clEvent.setBackgroundColor(Color.RED)
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }
}
