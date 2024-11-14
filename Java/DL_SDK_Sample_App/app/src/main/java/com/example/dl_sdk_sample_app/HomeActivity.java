package com.example.dl_sdk_sample_app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.dl_sdk_sample_app.databinding.ActivityHomeBinding;
import com.google.android.material.navigation.NavigationView;
import com.example.dl_sdk_sample_app.R; // Ensure R is correctly imported

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawerLayout = binding.drawerLayout;
        navigationView = binding.navigationView;

        setSupportActionBar(binding.toolbar);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(menuItem -> {
            menuItem.setChecked(true);
            drawerLayout.closeDrawers();

            Fragment fragment = null;
            int id = menuItem.getItemId();
            if(id == R.id.nav_home){
                fragment = new HomeFragment();
            } else if(id == R.id.nav_configure_properties){
                fragment = new ConfigurePropertiesFragment();
            } else if(id == R.id.nav_configure_intent){
                fragment = new ConfigureIntentFragment();
            } else if(id == R.id.nav_config_change_notification){
                fragment = new ConfigChangeNotificationFragment();
            } else if(id == R.id.nav_usb_whitelisting){
                fragment = new UsbWhitelistingFragment();
            } else if(id == R.id.nav_bluetooth_silent_pairing){
                fragment = new BluetoothSilentPairingFragment();
            } else if(id == R.id.nav_change_device_name){
                fragment = new ChangeDeviceNameFragment();
            }
            // Add cases for additional features

            if (fragment != null) {
                loadFragment(fragment);
            }
            return true;
        });

        loadFragment(new HomeFragment());
    }

    private void loadFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
}
