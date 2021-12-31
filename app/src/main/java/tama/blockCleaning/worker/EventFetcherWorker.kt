package tama.blockCleaning.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import tama.blockCleaning.data.entity.Cleaning
import tama.blockCleaning.data.entity.Street
import tama.blockCleaning.data.fetch.DataFetcher
import tama.blockCleaning.helpers.GPS
import tama.blockCleaning.helpers.deleteEvent
import tama.blockCleaning.helpers.getEvents
import tama.blockCleaning.helpers.insertEvent
import java.text.SimpleDateFormat
import java.util.*

class EventFetcherWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    private var streets: List<Street>? = null
    private var cleanings: List<Cleaning>? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val currentDate = Calendar.getInstance().time
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)
        val endDate = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        DataFetcher.fetchData(this.applicationContext,
            "${dateFormat.format(currentDate)}T00:00:00.000Z",
            "${dateFormat.format(endDate)}T20:59:59.999Z",
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

    private fun onFetched() {
        val eventsOld = getEvents(this.applicationContext)
        eventsOld.events.forEach { event ->
            deleteEvent(this.applicationContext, event.id)
        }

        val gps = GPS(0.0, 0.0)
        val dateFormat = SimpleDateFormat("d. M. yyyy, HH.mm")

        this.cleanings?.forEach { cleaning ->
            val street = this.streets?.find { s -> s.id == cleaning.sId }
            if (street != null) {
                insertEvent(
                    this.applicationContext,
                    cleaning.name,
                    gps,
                    dateFormat.format(cleaning.from),
                    dateFormat.format(cleaning.to)
                )
            }
        }
    }
}