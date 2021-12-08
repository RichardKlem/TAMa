package com.example.tama.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tama.data.entity.Cleaning
import com.example.tama.data.fetch.DataFetcher
import com.example.tama.helpers.*
import java.text.SimpleDateFormat
import java.util.*

class EventFetcherWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val currentDate = Calendar.getInstance().time
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, 1);
        val endDate = c.time;
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        DataFetcher.fetchData(this.applicationContext, "${dateFormat.format(currentDate)}T00:00:00.000Z", "${dateFormat.format(endDate)}T20:59:59.999Z", { events: List<Cleaning> ->
            val eventsOld = getEvents(this.applicationContext)
            eventsOld.events.forEach { event ->
                deleteEvent(this.applicationContext, event.id)
            }

            val streets = getLocations(this.applicationContext)
            val gps = GPS(0.0, 0.0)

            events.forEach { event ->
                val street = streets.locations.find { location -> location.id.toInt() == event.sId }
                if (street != null) {
                    insertEvent(this.applicationContext, street.userNaming, gps)
                }
            }
        }, {  })

        return Result.success()
    }
}