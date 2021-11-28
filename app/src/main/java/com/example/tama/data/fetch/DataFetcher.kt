package com.example.tama.data.fetch

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.tama.data.entity.Cleaning
import com.example.tama.data.entity.Section
import com.example.tama.data.entity.Street
import com.example.tama.data.entity.Waypoint
import org.json.JSONException
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

class DataFetcher {
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        fun fetchData(context: Context, from: String, to: String, callback: (List<Cleaning>) -> Unit) {
            val streets = mutableListOf<Street>()
            val cleanings = mutableListOf<Cleaning>()

            val queue = Volley.newRequestQueue(context)

            val streetsUrl = "https://cisteni.bkom.cz/api/street"
            val streetsRequest = JsonObjectRequest(Request.Method.GET, streetsUrl, null,
                { response ->
                    val streetsArray = response.getJSONArray("streets")
                    for (i in 0 until streetsArray.length()) {
                        val streetObject = streetsArray.getJSONObject(i)
                        try {
                            val streetEntity = Street(
                                streetObject.getInt("id"),
                                streetObject.getString("name"),
                                streetObject.getString("searchName")
                            )
                            streets.add(streetEntity)
                        } catch (ex: JSONException) {
                            // ignore error
                        }
                    }
                    print("Streets loaded")

                },
                { error ->
                    // TODO: Handle error
                }
            )

            val cleaningsUrl =
                "https://cisteni.bkom.cz/api/sweep/map?from=%s&to=%s".format(from, to)
            val cleaningsRequest = JsonObjectRequest(Request.Method.GET, cleaningsUrl, null,
                { response ->
                    val cleaningsArray = response.getJSONArray("sweeps")
                    for (i in 0 until cleaningsArray.length()) {
                        val cleaningObject = cleaningsArray.getJSONObject(i)
                        val fromDate =
                            DateTimeFormatter.ISO_DATE_TIME.parse(cleaningObject.getString("from"))
                        val toDate =
                            DateTimeFormatter.ISO_DATE_TIME.parse(cleaningObject.getString("to"))
                        val sectionObject = cleaningObject.getJSONObject("section")
                        val waypointsArray = sectionObject.getJSONArray("waypoints")
                        val waypoints = mutableListOf<Waypoint>()
                        for (n in 0 until waypointsArray.length()) {
                            val waypointObject = waypointsArray.getJSONObject(n)
                            try {
                                waypoints.add(
                                    Waypoint(
                                        waypointObject.getInt("id"),
                                        waypointObject.getDouble("lat"),
                                        waypointObject.getDouble("lon"),
                                        waypointObject.getInt("order")
                                    )
                                )
                            } catch (ex: JSONException) {
                                // ignore error
                            }
                        }

                        try {
                            val cleaningEntity = Cleaning(
                                cleaningObject.getInt("id"),
                                cleaningObject.getString("name"),
                                Date.from(Instant.from(fromDate)),
                                Date.from(Instant.from(toDate)),
                                cleaningObject.getInt("sid"),
                                Section(
                                    sectionObject.getInt("id"),
                                    sectionObject.getString("name"),
                                    sectionObject.getBoolean("privateRoad"),
                                    sectionObject.getInt("streetID"),
                                    waypoints.toTypedArray()
                                )
                            )
                            cleanings.add(cleaningEntity)
                        } catch (ex: JSONException) {
                            // ignore error
                        }
                    }
                    print("Cleanings loaded")
                    callback(cleanings.toList())
                },
                { error ->
                    // TODO: Handle error
                }
            )


            queue.add(streetsRequest)
            queue.add(cleaningsRequest)
        }
    }
}