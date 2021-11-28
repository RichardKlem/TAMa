package com.example.tama

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tama.databinding.ActivityMainBinding
import com.example.tama.ui.events.EventsFragment
import com.example.tama.ui.home.MapsFragment
import com.example.tama.ui.locations.LocationFragment
import com.example.tama.worker.NotificationWorker
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val fragmentManager = supportFragmentManager
    private val eventFragment = EventsFragment()
    private val locationFragment = LocationFragment()
    private val mapFragment = MapsFragment()
    private var activeFragment: Fragment = locationFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard, R.id.navigation_events
            )
        )

        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        fragmentManager.beginTransaction().apply {
            add(R.id.container, locationFragment, getString(R.string.title_events))
            add(R.id.container, eventFragment, getString(R.string.title_locations)).hide(eventFragment)
            add(R.id.container, mapFragment, getString(R.string.title_map)).hide(mapFragment)
        }.commit()
        initListeners()
        nav_view.itemIconTintList = null

        // set notification worker
        val workerRequest = PeriodicWorkRequestBuilder<NotificationWorker>(2, TimeUnit.HOURS).build()
        WorkManager.getInstance(applicationContext).enqueue(workerRequest)
    }

    private fun initListeners() {
        nav_view.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_events -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(eventFragment).commit()
                    activeFragment = eventFragment
                    true
                }

                R.id.navigation_dashboard -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(locationFragment).commit()
                    activeFragment = locationFragment
                    true
                }

                R.id.mapsFragment -> {
                    fragmentManager.beginTransaction().hide(activeFragment).show(mapFragment).commit()
                    activeFragment = mapFragment
                    true
                }

                else -> false
            }
        }
    }
}
