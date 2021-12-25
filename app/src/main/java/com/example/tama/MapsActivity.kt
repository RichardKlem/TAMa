package com.example.tama

import android.R.layout.select_dialog_item
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.*


@Serializable
data class StreetsObj(val streetsList: MutableList<String>)

class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var marker: Marker? = null
    private var streets: StreetsObj = StreetsObj(mutableListOf())

    private val defaultAddress = "Husova"
    private val defaultLatLng: LatLng = LatLng(49.1962063, 16.6037825)

    private var selectedLatLng: LatLng = defaultLatLng
    private var selectedAdress: String = defaultAddress

    override fun onCreate(savedInstanceState: Bundle?) {
        val geocoder = Geocoder(this, Locale.getDefault())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Load all Brno streets.
        try {
            val input = this.assets.open("brnostreets.json")
            val buffer = ByteArray(input.available())
            input.read(buffer)
            input.close()
            val bufferString = String(buffer, charset("UTF-8"))
            if (bufferString.isNotEmpty()) {
                streets = Json.decodeFromString(bufferString)
            }
        } catch (e: Exception) {
            Log.e(
                "MapsActivity: ",
                "Something went wrong while loading streets from assets JSON.\n $e"
            )
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        val button = findViewById<View>(R.id.mapAddButton)
        button.setOnClickListener { openNewActivity() }

        val actv = findViewById<View>(R.id.autoCompleteTextView) as AutoCompleteTextView?
        actv?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    val location = actv.text.toString()
                    val addressList: List<Address>?

                    if (location.isNotEmpty()) {
                        try {
                            addressList = geocoder.getFromLocationName("$location Brno", 1)
                            if (addressList.isNotEmpty()) {
                                val address = addressList[0]
                                val latLng = LatLng(address.latitude, address.longitude)

                                marker?.remove()
                                marker = mMap?.addMarker(
                                    MarkerOptions().position(latLng).title(location)
                                )
                                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                                selectedLatLng = latLng
                                selectedAdress = address.thoroughfare  // Street name
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    return true
                }
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
        val adapter = ArrayAdapter(this, select_dialog_item, streets.streetsList)
        val actv = findViewById<View>(R.id.autoCompleteTextView) as AutoCompleteTextView?
        /* It will give options from the first character typed. It is not ignoring diacritics
           or accents for now so it is better to give options as early as possible. */
        actv?.threshold = 1

        actv?.setAdapter(adapter)
        mMap = googleMap
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15f))
    }
}