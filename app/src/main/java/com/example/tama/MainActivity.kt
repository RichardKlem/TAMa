package com.example.tama

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.example.tama.ui.locations.LocationFragment
import com.example.tama.ui.settings.SettingsFragment
import com.example.tama.worker.NotificationWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val fragmentManager = supportFragmentManager
    private lateinit var eventFragment: EventsFragment
    private lateinit var locationFragment: LocationFragment

    //    private lateinit var settingsFragment: SettingsFragment
    private val settingsFragment = SettingsFragment()
    private lateinit var activeFragment: Fragment

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

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController: NavController = navHostFragment.navController

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


//        fragmentManager.beginTransaction().apply {
//            add(R.id.container, locationFragment, getString(R.string.title_events))
//            add(R.id.container, eventFragment, getString(R.string.title_locations)).hide(eventFragment)
//            add(R.id.container, settingsFragment, getString(R.string.settings)).hide(settingsFragment)
//        }.commit()
        println("MAIN BEFORE")
//        initListeners()
        nav_view.itemIconTintList = null

        // set notification worker
        val workerRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(2, TimeUnit.HOURS).build()
        WorkManager.getInstance(applicationContext).enqueue(workerRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.goto_settings) {
////            fragmentManager.beginTransaction().replace(activeFragment.id, settingsFragment)
////            fragmentManager.beginTransaction().addToBackStack(null)
////            fragmentManager.beginTransaction().commit()
//            fragmentManager.beginTransaction().hide(activeFragment).show(settingsFragment).commit()
//            activeFragment = settingsFragment
//            btnAddLocation.hide()
//            return true
            val switchActivityIntent = Intent(this, SettingsActivity::class.java)
            startActivity(switchActivityIntent)
        }

        return super.onOptionsItemSelected(item)
    }

//    private fun initListeners() {
//        nav_view.setOnItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.navigation_events -> {
////                    fragmentManager.beginTransaction().replace(activeFragment.id, eventFragment)
////                    fragmentManager.beginTransaction().addToBackStack(null)
////                    fragmentManager.beginTransaction().commit()
//                    fragmentManager.beginTransaction().hide(activeFragment).show(eventFragment).commit()
//                    activeFragment = eventFragment
//                    btnAddLocation.hide()
//                    true
//                }
//
//                R.id.navigation_dashboard -> {
////                    fragmentManager.beginTransaction().replace(activeFragment.id, locationFragment)
////                    fragmentManager.beginTransaction().addToBackStack(null)
////                    fragmentManager.beginTransaction().commit()
//                    fragmentManager.beginTransaction().hide(activeFragment).show(locationFragment).commit()
//                    activeFragment = locationFragment
//                    btnAddLocation.show()
//                    true
//                }
//
//                else -> false
//            }
//        }
//    }
}
