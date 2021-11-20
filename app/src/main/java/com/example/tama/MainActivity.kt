package com.example.tama

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tama.data.fetch.DataFetcher
import com.example.tama.databinding.ActivityMainBinding
import com.example.tama.ui.events.EventsFragment
import com.example.tama.ui.home.MapsFragment
import com.example.tama.ui.locations.LocationFragment
import kotlinx.android.synthetic.main.activity_main.*

import android.view.View
import android.widget.Button
import android.content.Intent



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val fragmentManager = supportFragmentManager
    private val eventFragment = EventsFragment()
    private val locationFragment = LocationFragment()
    private val mapFragment = MapsFragment()
    private var activeFragment: Fragment = locationFragment

    @RequiresApi(Build.VERSION_CODES.O)
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
        val button = findViewById<View>(R.id.button2) as Button
        button.setOnClickListener { openNewActivity() }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        DataFetcher.fetchData(this, "2021-10-24T22:00:00.000Z", "2021-11-01T22:59:59.999Z")

        fragmentManager.beginTransaction().apply {
            add(R.id.container, locationFragment, getString(R.string.title_events))
            add(R.id.container, eventFragment, getString(R.string.title_locations)).hide(eventFragment)
            add(R.id.container, mapFragment, getString(R.string.title_map)).hide(mapFragment)
        }.commit()
        initListeners()
        nav_view.itemIconTintList = null
    }
    private fun openNewActivity() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
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
