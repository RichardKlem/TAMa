package com.example.tama.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tama.data.entity.Street
import com.example.tama.data.fetch.DataFetcher
import com.example.tama.helpers.*
import java.text.SimpleDateFormat
import java.util.*

class StreetsFetcherWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val currentDate = Calendar.getInstance().time
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, 1);
        val endDate = c.time;
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        DataFetcher.fetchData(this.applicationContext, "${dateFormat.format(currentDate)}T00:00:00.000Z", "${dateFormat.format(endDate)}T20:59:59.999Z", {}, { streets: List<Street> ->
            val locations = getLocations(this.applicationContext)
            locations.locations.forEach { location ->
                deleteLocationDB(this.applicationContext, location.id)
            }

            val gps = GPS(0.0, 0.0)
            val subLocations = listOf<SubLocation>();

            streets.forEach { street ->
                insertLocation(this.applicationContext, street.searchName, street.name, gps, 0, subLocations);
            }
        })

        return Result.success()
    }
}