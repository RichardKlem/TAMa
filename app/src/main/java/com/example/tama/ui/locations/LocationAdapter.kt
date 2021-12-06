package com.example.tama.ui.locations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tama.R
import com.example.tama.helpers.GPS
import com.example.tama.helpers.deleteLocationDB
import com.example.tama.helpers.insertLocation
import kotlinx.android.synthetic.main.location_on_main.view.*

class LocationAdapter(
    private val locations: MutableList<Location>
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

//    fun addLocation(context: Context, location: Location) {
    fun addLocation(location: Location) {
        locations.add(location)
        notifyItemInserted(locations.size - 1)

//        val mockGPS = GPS(.0, .0)
//        insertLocation(context, location.title, mockGPS)
    }

    private fun deleteLocation(position: Int) {
        locations.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.location_on_main,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val curLocation = locations[position]
        holder.itemView.apply {
            tvLocation.text = curLocation.title

            btnDeleteLocation.setOnClickListener {
                deleteLocation(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return locations.size
    }
}
