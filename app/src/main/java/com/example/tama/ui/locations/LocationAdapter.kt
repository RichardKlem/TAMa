package com.example.tama.ui.locations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.tama.R
import com.example.tama.helpers.Location
import com.example.tama.helpers.deleteLocationDB
import com.example.tama.helpers.getLocations
import kotlinx.android.synthetic.main.location_on_main.view.*

class LocationAdapter(
    private var locations: MutableList<Location>
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun loadLocations(context: Context) {
        locations = getLocations(context).locations
        notifyDataSetChanged()
    }

    private fun deleteLocation(context: Context, position: Int) {
        deleteLocationDB(context, locations[position].id)
        locations = getLocations(context).locations
        notifyDataSetChanged()  // Must be this, to re-calculate positions of all other locations.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        locations = getLocations(parent.context).locations
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
            tvLocation.text = curLocation.userNaming

            btnDeleteLocation.setOnClickListener {
                deleteLocation(context, position)
            }

            clLocation.setOnClickListener { view ->
                view.findNavController().navigate(R.id.navigation_events)
            }
        }
    }

    override fun getItemCount(): Int {
        return locations.size
    }
}
