package com.example.tama.helpers


import android.content.Context
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*
import java.util.*


/**____________________________________
 * ______ LOCATION DATA CLASSES _______
 * ____________________________________ */

/**
 * Data class of GPS value of user selected location.
 * No check is done, provided values should be valid.
 *
 * @property lat Latitude value.
 * @property long Longitude value.
 */
@Serializable
data class GPS(val lat: Double, val long: Double)


/**
 * Data class of sub-location. An element of the list of sub-locations in the selected area.
 *
 * @property technicalName Geo package defined name of the location.
 */
@Serializable
data class SubLocation(var technicalName: String)

/**
 * Data class of user selected location.
 *
 * @property id Generated [UUID].
 * @property technicalName Geo package defined name of the location.
 * @property userNaming Name which can be changed by user. Default is shorten [technicalName].
 * @property gps [GPS] value of the location.
 * @property radius Value of the location area, zero means only one street.
 * @property listOfSubLocations List of [SubLocation] objects. If the location is an area then more
 *           then one value will be present, else (the location is only one street) then there will
 *           be only one element.
 */
@Serializable
data class Location(
    val id: String,
    val technicalName: String,
    var userNaming: String,
    val gps: GPS,
    val radius: Int,
    val listOfSubLocations: List<SubLocation>
)

/**
 * Data class wrapping the list of user defined locations.
 * Can be initialized with empty list. This is also valid and useful.
 *
 * @property locations List of locations.
 */
@Serializable
data class UserLocations(val locations: MutableList<Location>)


/**____________________________________
 * _______ EVENTS DATA CLASSES ________
 * ____________________________________ */

/**
 * Data class of user cleaning event.
 *
 * @property id Generated [UUID].
 * @property name Geo package defined name of the Event.
 * @property gps [GPS] value of the event.
 */
@Serializable
data class Event(val id: String, var name: String, val gps: GPS, val from: String, val to: String)


/**
 * Data class wrapping the list of user defined locations.
 * Can be initialized with empty list. This is also valid and useful.
 *
 * @property events List of events.
 */
@Serializable
data class LocationEvents(val events: MutableList<Event>)


/**____________________________________
 * ________ GENERAL FUNCTIONS _________
 * ____________________________________ */

/**
 * Function to generate [UUID].
 *
 * @return String [UUID]
 */
fun createUUID(): String {
    return UUID.randomUUID().toString()
}

const val LOCATION_FILE_NAME = "locations.json"
const val EVENT_FILE_NAME = "events.json"


/**
 * Helper function to create file.
 *
 * @param context [Context] to open a file.
 * @param fileName File name to use.
 */
fun createFile(context: Context, fileName: String) {
    try {
        val bufferedWriter = BufferedWriter(FileWriter(File(context.filesDir, fileName)))
        bufferedWriter.write("")
        bufferedWriter.close()
    } catch (e: Exception) {
        Log.e("JSON Helper - createFile()", "Something went wrong.")
    }
}

/**
 * Helper function to read data/text from the file.
 *
 * @param context [Context] to open a file.
 * @param fileName File name to use.
 * @return String of read data.
 */
fun readFile(context: Context, fileName: String): String {
    var inputStream: InputStream? = null
    var outString = ""
    try {
        inputStream = context.openFileInput(fileName)
        // Read data from JSON file.
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var receiveString: String?
        val stringBuilder = StringBuilder()
        while (bufferedReader.readLine().also { receiveString = it } != null) {
            stringBuilder.append(receiveString)
        }
        outString = stringBuilder.toString()
    } catch (e: FileNotFoundException) {
        createFile(context, fileName)
    } catch (e: IOException) {
        Log.e("JSON Helper - readFile()", "Can not read file: $e")
    } finally {
        try {
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return outString
}

/**
 * Helper function to write some data/text into the file.
 *
 * @param context [Context] to open a file.
 * @param data Data of type [String] to write into the file.
 * @param fileName File name to use.
 * @return true if no Exception was thrown, false else.
 */
fun writeFile(context: Context, data: String, fileName: String): Boolean {
    try {
        val bufferedWriter = BufferedWriter(FileWriter(File(context.filesDir, fileName)))
        bufferedWriter.write(data)
        bufferedWriter.close()
    } catch (e: IOException) {
        Log.e("JSON Helper - writeFile()", "File write failed: $e")
        return false
    }
    return true
}


/**____________________________________
 * __ LOCATION MANIPULATION FUNCTIONS _
 * ____________________________________ */

/**
 * Function to add a new location into the list of locations.
 * Parameters are in fact the same the [Location] constructor takes.
 *
 * @param context [Context] to open a file.
 * @param technicalName Name of the new location.
 * @property userNaming Name which can be changed by user. Default is shorten [technicalName].
 * @property gps [GPS] value of the location.
 * @property radius Value of the location area, zero means only one street.
 * @property subLocations List of [SubLocation] objects. If the location is an area then more
 *           then one value will be present, else (the location is only one street) then there will
 *           be only one element.
 * @return Updated list of user's locations.
 */
fun insertLocation(context: Context, technicalName: String, userNaming: String, gps: GPS, radius: Int, subLocations: List<SubLocation>): UserLocations? {
    val locationsObject = getLocations(context)
    try {
        val newLocation = Location(createUUID(), technicalName, userNaming, gps, radius, subLocations)
        locationsObject.locations.add(newLocation)

        val locationsJson = Json.encodeToString(locationsObject)
        writeFile(context, locationsJson, LOCATION_FILE_NAME)
    } catch (e: Exception) {
        Log.e("JSON Helper - insertLocation()", "Something went wrong.")
        return null
    }
    return locationsObject
}

/**
 * Remove certain location specified by the id parameter.
 *
 * @param context Context to open a file.
 * @param id String of UUID of the location to be removed.
 * @return Updated UserLocations object, could be empty.
 */
fun deleteLocationDB(context: Context, id: String): UserLocations? {
    val locationsObject: UserLocations = getLocations(context)
    try {
        locationsObject.locations.removeAll { it.id == id }

        val locationsJson = Json.encodeToString(locationsObject)
        writeFile(context, locationsJson, LOCATION_FILE_NAME)
    } catch (e: Exception) {
        Log.e("JSON Helper - deleteLocation()", "Something went wrong.")
        return null
    }
    return locationsObject
}

/**
 * Update certain location specified by the id parameter.
 * Only the location's name is supported for now.
 * If no location with specified id exists, nothing happens and original object is returned.
 *
 * @param context Context to open a file.
 * @param id String of UUID of the location to be updated.
 * @param newName New user naming of the location.
 * @return Updated UserLocations object, could be empty.
 */
fun updateLocation(context: Context, id: String, newName: String): UserLocations {
    val locationsObject: UserLocations = getLocations(context)
    try {
        locationsObject.locations.find { it.id == id }?.userNaming = newName

        val locationsJson = Json.encodeToString(locationsObject)
        writeFile(context, locationsJson, LOCATION_FILE_NAME)
    } catch (e: Exception) {
        Log.e("JSON Helper - updateLocation()", "Something went wrong.")
    }
    return locationsObject
}

/**
 * Helper function to get object of UserLocations.
 *
 * @param context Context to open a file.
 * @return UserLocations object, could be empty.
 */
fun getLocations(context: Context): UserLocations {
    var locationsObject = UserLocations(mutableListOf())
    try {
        val data = readFile(context, LOCATION_FILE_NAME)
        // If the JSON file has already some data, load them.
        if (data.isNotEmpty()) {
            locationsObject = Json.decodeFromString(data)
        }
    } catch (e: Exception) {
        Log.e("JSON Helper - getLocations()", "Something went wrong.")
    }
    return locationsObject
}


/**____________________________________
 * __ EVENTS MANIPULATION FUNCTIONS ___
 * ____________________________________ */

/**
 * Function to add a new event into the list of events.
 *
 * @param context [Context] to open a file.
 * @param name Name of the new event.
 * @param gps [GPS] value of the new event.
 * @return Updated list of user's events.
 */
fun insertEvent(context: Context, name: String, gps: GPS, from: String, to: String): LocationEvents? {
    val eventsObject = getEvents(context)
    try {
        val newEvent = Event(createUUID(), name, gps, from, to)
        eventsObject.events.add(newEvent)

        val locationsJson = Json.encodeToString(eventsObject)
        writeFile(context, locationsJson, EVENT_FILE_NAME)
    } catch (e: Exception) {
        Log.e("JSON Helper - insertLocation()", "Something went wrong.")
        return null
    }
    return eventsObject
}

/**
 * Remove certain location specified by the id parameter.
 *
 * @param context Context to open a file.
 * @param id String of UUID of the location to be removed.
 * @return Updated UserLocations object, could be empty.
 */
fun deleteEvent(context: Context, id: String): LocationEvents? {
    val eventObject: LocationEvents = getEvents(context)
    try {
        eventObject.events.removeAll { it.id == id }

        val eventsJson = Json.encodeToString(eventObject)
        writeFile(context, eventsJson, EVENT_FILE_NAME)
    } catch (e: Exception) {
        Log.e("JSON Helper - deleteEvent()", "Something went wrong.")
        return null
    }
    return eventObject
}

/**
 * Update certain location specified by the id parameter.
 * Only the location's name is supported for now.
 * If no location with specified id exists, nothing happens and original object is returned.
 *
 * @param context Context to open a file.
 * @param id String of UUID of the location to be updated.
 * @param newName New name of the location.
 * @return Updated UserLocations object, could be empty.
 */
fun updateEvent(context: Context, id: String, newName: String): LocationEvents {
    val eventObject: LocationEvents = getEvents(context)
    try {
        eventObject.events.find { it.id == id }?.name = newName

        val eventsJson = Json.encodeToString(eventObject)
        writeFile(context, eventsJson, EVENT_FILE_NAME)
    } catch (e: Exception) {
        Log.e("JSON Helper - updateEvent()", "Something went wrong.")
    }
    return eventObject
}

/**
 * Helper function to get object of UserLocations.
 *
 * @param context Context to open a file.
 * @return UserLocations object, could be empty.
 */
fun getEvents(context: Context): LocationEvents {
    var eventsObject = LocationEvents(mutableListOf())
    try {
        val data = readFile(context, EVENT_FILE_NAME)
        // If the JSON file has already some data, load them.
        if (data.isNotEmpty()) {
            eventsObject = Json.decodeFromString(data)
        }
    } catch (e: Exception) {
        Log.e("JSON Helper - getEvents()", "Something went wrong.")
    }
    return eventsObject
}