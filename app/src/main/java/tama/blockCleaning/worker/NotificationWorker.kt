package tama.blockCleaning.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import tama.blockCleaning.MainActivity
import tama.blockCleaning.data.entity.Cleaning
import tama.blockCleaning.data.fetch.DataFetcher
import tama.blockCleaning.helpers.getLocations
import java.text.SimpleDateFormat
import java.util.*

class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val off = this.applicationContext.getSharedPreferences("enable_notif", Context.MODE_PRIVATE)
            .getBoolean("enable_notif", false)

        if (off) {
            return Result.success()
        }

        val calendar: Calendar = Calendar.getInstance()
        val currentDate = calendar.time
        calendar.add(Calendar.DATE, 1)
        val endDate = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")

        if (calendar.get(Calendar.HOUR_OF_DAY) in 9..15) {
            return Result.success()
        }

        val editor = this.applicationContext.getSharedPreferences("dnd", Context.MODE_PRIVATE)
        val nextNotificationsTime = editor.getLong("dnd", 0)

        if (currentDate.time < nextNotificationsTime && nextNotificationsTime > 0) {
            return Result.success()
        }

        val newDnd = Calendar.getInstance()
        newDnd.add(Calendar.HOUR, 12)
        editor.edit().putLong("dnd", newDnd.time.time).apply()


        DataFetcher.fetchData(
            this.applicationContext,
            "${dateFormat.format(currentDate)}T00:00:00.000Z",
            "${dateFormat.format(endDate)}T20:59:59.999Z",
            { listOfCleanings: List<Cleaning> ->
                val intent = Intent(this.applicationContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                val pendingIntent: PendingIntent = PendingIntent.getActivity(
                    this.applicationContext, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
                val fromFormat = SimpleDateFormat("d. M. yyyy, HH.mm")
                val toFormat = SimpleDateFormat("HH.mm")

                listOfCleanings.forEach { cleaning ->
                    var day = fromFormat.format(cleaning.from)
                    var selected = false

                    getLocations(this.applicationContext).locations.forEach { location ->
                        if (cleaning.name.lowercase().contains(location.userNaming.lowercase())) {
                            selected = true
                        }
                    }

                    if (cleaning.to >= currentDate && selected) {
                        this.createNotificationChannel("1", "Blokové čistění")

                        if (dateFormat.format(cleaning.from) == dateFormat.format(currentDate)) {
                            day = "Dnes"
                        } else if (dateFormat.format(cleaning.from) == dateFormat.format(endDate)) {
                            day = "Zítra"
                        }

                        val builder = NotificationCompat.Builder(this.applicationContext, "1")
                            .setSmallIcon(android.R.drawable.ic_dialog_map)
                            .setContentTitle(cleaning.name)
                            .setContentText(
                                "$day / ${toFormat.format(cleaning.from)} – ${
                                    toFormat.format(
                                        cleaning.to
                                    )
                                } hod"
                            )
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                        with(NotificationManagerCompat.from(this.applicationContext)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(cleaning.id, builder.build())
                        }
                    }
                }
            },
            {})

        return Result.success()
    }

    private fun createNotificationChannel(id: String, name: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance)
            // Register the channel with the system
            val notificationManager: NotificationManager =
                this.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
