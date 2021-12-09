package com.example.tama.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tama.data.entity.Cleaning
import com.example.tama.data.entity.Street
import com.example.tama.data.fetch.DataFetcher
import com.example.tama.helpers.*
import java.text.SimpleDateFormat
import java.util.*

class EventFetcherWorker(ctx: Context, params: WorkerParameters): Worker(ctx, params) {
    private var streets: List<Street>? = null
    private var cleanings: List<Cleaning>? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val currentDate = Calendar.getInstance().time
        val c = Calendar.getInstance()
        c.add(Calendar.DATE, 1)
        val endDate = c.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        DataFetcher.fetchData(this.applicationContext, "${dateFormat.format(currentDate)}T00:00:00.000Z", "${dateFormat.format(endDate)}T20:59:59.999Z",
            { c ->
                this.cleanings = c
                if (this.streets != null) {
                    this.onFetched()
                }
            },
            { s ->
                this.streets = s
                if (this.cleanings != null) {
                    this.onFetched()
                }
            }
        )

        return Result.success()
    }

    private fun onFetched(): Unit
    {
        val eventsOld = getEvents(this.applicationContext)
        eventsOld.events.forEach { event ->
            deleteEvent(this.applicationContext, event.id)
        }

        val gps = GPS(0.0, 0.0)
        val dateFormat = SimpleDateFormat("d. M. yyyy, HH.mm")

        this.cleanings?.forEach { cleaning ->
            val street = this.streets?.find { s -> s.id == cleaning.sId }
            if (street != null) {
                insertEvent(this.applicationContext, cleaning.name, gps, dateFormat.format(cleaning.from), dateFormat.format(cleaning.to))
            }
        }
    }
}