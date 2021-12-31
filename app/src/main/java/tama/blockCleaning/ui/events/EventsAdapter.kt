package tama.blockCleaning.ui.events

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import tama.blockCleaning.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventsAdapter(
    private val events: MutableList<Event>
) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun addEvent(event: Event) {
        events.add(event)
        notifyItemInserted(events.size - 1)
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
        val tvEvent = holder.itemView.findViewById<TextView>(R.id.tvEvent)
        val tvDate = holder.itemView.findViewById<TextView>(R.id.tvDate)
        val tvTime = holder.itemView.findViewById<TextView>(R.id.tvTime)
        val clEvent = holder.itemView.findViewById<ConstraintLayout>(R.id.clEvent)
        val curLocation = events[position]
        holder.itemView.apply {
            tvEvent.text = curLocation.title
            tvDate.text = curLocation.date
            tvTime.text = curLocation.time

            val date = LocalDateTime.now()
            val currentDateFormat = DateTimeFormatter.ofPattern("d.M.")
            val currentDate = date.format(currentDateFormat)
            if (tvDate.text == currentDate.toString()) {
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
