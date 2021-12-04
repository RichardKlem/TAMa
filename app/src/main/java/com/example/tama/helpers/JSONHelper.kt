package com.example.tama.helpers


import android.content.Context
import android.util.Log
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*
import java.util.*

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
 * Data class of user selected location.
 *
 * @property id Generated [UUID].
 * @property name User defined or geo package defined name of the location.
 * @property gps [GPS] value of the location.
 */
@Serializable
data class Location(val id: String, var name: String, val gps: GPS)

/**
 * Data class wrapping the list of user defined locations.
 * Can be initialized with empty list. This is also valid and useful.
 *
 * @property locations List of locations.
 */
@Serializable
data class UserLocations(val locations: MutableList<Location>)


/**
 * Function to generate [UUID].
 *
 * @return String [UUID]
 */
fun createUUID(): String {
    return UUID.randomUUID().toString()
}

const val FILE_NAME = "locations.json"


/**
 * Helper function to create file.
 *
 * @param context [Context] to open a file.
 * @param fileName File name to use.
 */
fun createFile(context: Context, fileName: String = FILE_NAME) {
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
fun readFile(context: Context, fileName: String = FILE_NAME): String {
    var inputStream: InputStream? = null
    var locationsString = ""
    try {
        inputStream = context.openFileInput(fileName)
        // Read data from JSON file.
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var receiveString: String?
        val stringBuilder = StringBuilder()
        while (bufferedReader.readLine().also { receiveString = it } != null) {
            stringBuilder.append(receiveString)
        }
        locationsString = stringBuilder.toString()
    } catch (e: FileNotFoundException) {
        createFile(context)
    } catch (e: IOException) {
        Log.e("JSON Helper - readFile()", "Can not read file: $e")
    } finally {
        try {
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return locationsString
}

/**
 * Helper function to write some data/text into the file.
 *
 * @param context [Context] to open a file.
 * @param data Data of type [String] to write into the file.
 * @param fileName File name to use.
 * @return true if no Exception was thrown, false else.
 */
fun writeFile(context: Context, data: String, fileName: String = FILE_NAME): Boolean {
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

/**
 * Function to add a new location into the list of locations.
 *
 * @param context [Context] to open a file.
 * @param name Name of the new location.
 * @param gps [GPS] value of the new location.
 * @return Updated list of user's locations.
 */
fun insertLocation(context: Context, name: String, gps: GPS): UserLocations? {
    val locationsObject = getLocations(context)
    try {
        val newLocation = Location(createUUID(), name, gps)
        locationsObject.locations.add(newLocation)

        val locationsJson = Json.encodeToString(locationsObject)
        writeFile(context, locationsJson)
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
fun deleteLocation(context: Context, id: String): UserLocations? {
    val locationsObject: UserLocations = getLocations(context)
    try {
        locationsObject.locations.removeAll { it.id == id }

        val locationsJson = Json.encodeToString(locationsObject)
        writeFile(context, locationsJson)
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
 * @param newName New name of the location.
 * @return Updated UserLocations object, could be empty.
 */
fun updateLocation(context: Context, id: String, newName: String): UserLocations {
    val locationsObject: UserLocations = getLocations(context)
    try {
        locationsObject.locations.find { it.id == id }?.name = newName

        val locationsJson = Json.encodeToString(locationsObject)
        writeFile(context, locationsJson)
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
        val data = readFile(context)
        // If the JSON file has already some data, load them.
        if (data.isNotEmpty()) {
            locationsObject = Json.decodeFromString(data)
        }
    } catch (e: Exception) {
        Log.e("JSON Helper - getLocations()", "Something went wrong.")
    }
    return locationsObject
}