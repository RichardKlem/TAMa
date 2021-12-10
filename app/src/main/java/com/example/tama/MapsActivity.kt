package com.example.tama

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.FragmentActivity
import com.example.tama.helpers.GPS
import com.example.tama.helpers.SubLocation
import com.example.tama.helpers.insertLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*


class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var marker: Marker? = null

    private val defaultAddress = "Husova"
    private val defaultLatLng: LatLng = LatLng(49.1962063, 16.6037825)

    private var selectedLatLng: LatLng = defaultLatLng
    private var selectedAdress: String = defaultAddress

    override fun onCreate(savedInstanceState: Bundle?) {
        val geocoder = Geocoder(this, Locale.getDefault())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val searchView: SearchView = findViewById(R.id.idSearchView)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        val button = findViewById<View>(R.id.button)
        button.setOnClickListener { openNewActivity() }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val location = searchView.query.toString()

                val addressList: List<Address>?

                if (location.isNotEmpty()) {
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                        if (addressList.isNotEmpty()) {
                            val address = addressList[0]
                            val latLng = LatLng(address.latitude, address.longitude)

                            marker?.remove()
                            marker =
                                mMap?.addMarker(MarkerOptions().position(latLng).title(location))
                            mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                            selectedLatLng = latLng
                            selectedAdress = address.thoroughfare  // Street name
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
        mapFragment?.getMapAsync(this)
    }

    private fun openNewActivity() {
        insertLocation(
            this,
            selectedAdress,
            selectedAdress,
            GPS(selectedLatLng.latitude, selectedLatLng.longitude),
            1,
            listOf(SubLocation(selectedAdress))
        )
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15f))
    }
}