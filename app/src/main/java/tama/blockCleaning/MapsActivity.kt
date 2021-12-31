package tama.blockCleaning

import android.R.layout.select_dialog_item
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import tama.blockCleaning.helpers.GPS
import tama.blockCleaning.helpers.SubLocation
import tama.blockCleaning.helpers.insertLocation
import java.io.IOException
import java.util.*


@Serializable
data class StreetsObj(val streetsList: MutableList<String>)

@Serializable
data class Street(val name: String, val gps: GPS)

@Serializable
data class StreetsWithGPS(val streetsList: MutableList<Street>)

class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var circle: Circle? = null
    private var midLatLng: LatLng? = LatLng(49.1962063, 16.6037825)
    private var streetsWithGps: StreetsWithGPS = StreetsWithGPS(mutableListOf())
    private var streets: StreetsObj = StreetsObj(mutableListOf())

    private val circleFillColor: Int =
        0x33 and 0xff shl 24 or (0xff and 0xff shl 16) or (0x00 and 0xff shl 8) or (0x00 and 0xff)

    override fun onCreate(savedInstanceState: Bundle?) {
        val geocoder = Geocoder(this, Locale.getDefault())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val areaSwitch = findViewById<View>(R.id.switchArea) as SwitchMaterial
        val radiusSlider = findViewById<View>(R.id.radiusSlider) as Slider
        val actv = findViewById<View>(R.id.autoCompleteTextView) as AutoCompleteTextView?

        val manager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        actv?.requestFocus()
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                showSoftKeyboard(findViewById(R.id.autoCompleteTextView))
                timer.cancel()
            }
        }, 1000)

        try {
            val input = this.assets.open("streetsWithGPS.json")
            val buffer = ByteArray(input.available())
            input.read(buffer)
            input.close()
            val bufferString = String(buffer, charset("UTF-8"))
            if (bufferString.isNotEmpty()) {
                streetsWithGps = Json.decodeFromString(bufferString)
            }
        } catch (e: Exception) {
            Log.e(
                "MapsActivity: ",
                "Something went wrong while loading streets with GPS from assets JSON.\n ${e.printStackTrace()}"
            )
        }
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
                "Something went wrong while loading streets from assets JSON.\n ${e.printStackTrace()}"
            )
        }

        radiusSlider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            if (areaSwitch.isChecked) {
                radiusSlider.visibility = VISIBLE
                circle?.radius = value.toDouble()
            }
        })

        areaSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                radiusSlider.visibility = VISIBLE
                circle?.center = midLatLng!!
                circle?.isVisible = true

            } else {
                radiusSlider.visibility = GONE
                circle?.isVisible = false
            }
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        val button = findViewById<View>(R.id.mapAddButton)
        button.setOnClickListener { openNewActivity(geocoder) }

        actv?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.endsWith("\n")) {
                    val location = actv.text.toString()
                    val addressList: List<Address>?

                    if (location.isNotEmpty()) {
                        try {
                            addressList = geocoder.getFromLocationName("$location Brno", 1)
                            if (addressList.isNotEmpty()) {
                                val address = addressList[0]
                                val latLng = LatLng(address.latitude, address.longitude)

                                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                                if (circle != null) {
                                    circle?.center = latLng
                                } else {
                                    circle = mMap?.addCircle(
                                        CircleOptions()
                                            .center(latLng)
                                            .radius(radiusSlider.value.toDouble())
                                            .strokeWidth(4F)
                                            .strokeColor(Color.RED)
                                            .fillColor(circleFillColor)
                                            .visible(areaSwitch.isChecked)
                                    )
                                }
                            }
                        } catch (e: IOException) {
                            Log.e(
                                "Maps Activity: ",
                                "Geocoding gone wrong.\n${e.printStackTrace()}"
                            )
                        }
                    }
                    actv.text = actv.text.trim() as Editable?
                    Selection.setSelection(actv.text, actv.length())
                    closeKeyboard(manager)
                    actv.clearFocus()
                    return
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {}
        })

        mapFragment?.getMapAsync(this)
    }

    private fun openNewActivity(geocoder: Geocoder) {
        val snackbar = Snackbar
            .make(
                findViewById(R.id.mapsActivityView),
                getString(R.string.unknownStreet),
                Snackbar.LENGTH_LONG
            )
        val addressName: String
        try {
            addressName = geocoder.getFromLocation(
                midLatLng!!.latitude,
                midLatLng!!.longitude,
                1
            )[0].thoroughfare
        } catch (e: Exception) {
            snackbar.show()
            Log.e(
                "Maps Activity - GEOCODER: ",
                "Error in geocoding LatLong.\n${e.printStackTrace()}"
            )
            return
        }

        val areaSwitch = findViewById<View>(R.id.switchArea) as SwitchMaterial
        val radiusSlider = findViewById<View>(R.id.radiusSlider) as Slider

        // Each location has at least one sub-location = itself.
        val streetsInArea = mutableListOf<SubLocation>()
        if (areaSwitch.isChecked) {
            for (street in streetsWithGps.streetsList) {
                val locationA = Location("mid point")
                locationA.latitude = midLatLng!!.latitude
                locationA.longitude = midLatLng!!.longitude

                val locationB = Location("street")
                locationB.latitude = street.gps.lat
                locationB.longitude = street.gps.long

                val distance: Float = locationA.distanceTo(locationB)
                if (distance <= radiusSlider.value)
                    streetsInArea.add(SubLocation(street.name))
            }
            insertLocation(
                this,
                addressName,
                addressName,
                GPS(midLatLng!!.latitude, midLatLng!!.longitude),
                radiusSlider.value.toInt(),
                streetsInArea
            )
        } else {
            streetsInArea.add(SubLocation(addressName))
            insertLocation(
                this,
                addressName,
                addressName,
                GPS(midLatLng!!.latitude, midLatLng!!.longitude),
                1,
                streetsInArea
            )
        }
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
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(midLatLng!!, 15f))

        mMap?.setOnCameraMoveListener {
            circle?.isVisible = false
        }

        val areaSwitch = findViewById<View>(R.id.switchArea) as SwitchMaterial
        val radiusSlider = findViewById<View>(R.id.radiusSlider) as Slider

        mMap?.setOnCameraIdleListener {
            val oldLatLng = midLatLng
            try {
                midLatLng = mMap?.cameraPosition?.target
            } catch (e: Exception) {
                midLatLng = oldLatLng
                Log.e("Map Camera Position Error", "\n${e.printStackTrace()}")
            }
            circle?.center = midLatLng!!
            circle?.isVisible = areaSwitch.isChecked
        }

        circle = mMap?.addCircle(
            CircleOptions()
                .center(midLatLng!!)
                .radius(radiusSlider.value.toDouble())
                .strokeWidth(4F)
                .strokeColor(Color.RED)
                .fillColor(circleFillColor)
                .visible(areaSwitch.isChecked)
        )
    }

    private fun showSoftKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun closeKeyboard(manager: InputMethodManager) {
        val view = this.currentFocus
        if (view != null) {
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
