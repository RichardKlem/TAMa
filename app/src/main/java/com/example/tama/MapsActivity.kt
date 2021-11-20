package com.example.tama

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var marker: Marker? = null


    // creating a variable
    // for search view.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // initializing our search view.
        val searchView: SearchView = findViewById(R.id.idSearchView)

        // Obtain the SupportMapFragment and get notified
        // when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        // adding on query listener for our search view.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // on below line we are getting the
                // location name from search view.
                val location = searchView.getQuery().toString()

                // below line is to create a list of address
                // where we will store the list of all address.
                var addressList: List<Address>? = null

                // checking if the entered location is null or not.
                if (!location.isEmpty()) {
                    // on below line we are creating and initializing a geo coder.
                    val geocoder = Geocoder(this@MapsActivity)
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1)
                        // on below line we are getting the location
                        // from our list a first position.
                        if (addressList.isNotEmpty()) {
                            val address = addressList[0]

                            // on below line we are creating a variable for our location
                            // where we will add our locations latitude and longitude.
                            val latLng = LatLng(address.latitude, address.longitude)

                            marker?.remove()
                            // on below line we are adding marker to that position.
                            marker = mMap?.addMarker(MarkerOptions().position(latLng).title(location))

                            // below line is to animate camera to that position.
                            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        // at last we calling our map fragment to update.
        mapFragment!!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}