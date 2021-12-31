package tama.blockCleaning.ui.locations

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import tama.blockCleaning.R
import tama.blockCleaning.helpers.Location
import tama.blockCleaning.helpers.deleteLocationDB
import tama.blockCleaning.helpers.getLocations

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
        val tvLocation = holder.itemView.findViewById<TextView>(R.id.tvLocation)
        val locationRadius = holder.itemView.findViewById<TextView>(R.id.locationRadius)
        val btnDeleteLocation = holder.itemView.findViewById<ImageButton>(R.id.btnDeleteLocation)
        val clLocation = holder.itemView.findViewById<ConstraintLayout>(R.id.clLocation)

        val curLocation = locations[position]
        holder.itemView.apply {
            tvLocation.text = curLocation.userNaming
            if (curLocation.radius > 1) {
                locationRadius.text =
                    resources.getString(R.string.radiusOnLocation, curLocation.radius)
            } else {
                locationRadius.text = ""
            }

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
