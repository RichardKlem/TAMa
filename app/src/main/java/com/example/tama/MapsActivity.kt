package com.example.tama

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var marker: Marker? = null
    private val brnoLatLng: LatLng = LatLng(49.19522, 16.60796)
    private var selectedLatLng: LatLng = brnoLatLng

    override fun onCreate(savedInstanceState: Bundle?) {
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
                    val geocoder = Geocoder(this@MapsActivity)
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
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("SelectedLatLng", selectedLatLng)
        startActivity(intent)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15f))
    }
}