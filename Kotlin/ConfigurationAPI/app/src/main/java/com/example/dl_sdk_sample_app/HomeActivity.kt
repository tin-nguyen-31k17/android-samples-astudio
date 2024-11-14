package com.example.dl_sdk_sample_app

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.dl_sdk_sample_app.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView

        // Set up the toolbar
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()

            when (menuItem.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_configure_properties -> loadFragment(ConfigurePropertiesFragment())
                R.id.nav_configure_intent -> loadFragment(ConfigureIntentFragment())
                R.id.nav_config_change_notification -> loadFragment(ConfigChangeNotificationFragment())
                R.id.nav_usb_whitelisting -> loadFragment(UsbWhitelistingFragment())
                R.id.nav_bluetooth_silent_pairing -> loadFragment(BluetoothSilentPairingFragment())
                R.id.nav_change_device_name -> loadFragment(ChangeDeviceNameFragment())
                // Add cases for additional features
            }
            true
        }

        // Load the initial fragment
        loadFragment(HomeFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}