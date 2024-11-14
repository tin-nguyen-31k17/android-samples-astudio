package com.example.dl_sdk_sample_app;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.dl_sdk_sample_app.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView bottomNavigation = binding.bottomNavigation;
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.navigation_func_0) {
                loadFragment(new Func0Fragment());
                return true;
            } else if (itemId == R.id.navigation_func_1) {
                loadFragment(new Func1Fragment());
                return true;
            } else if (itemId == R.id.navigation_func_2) {
                loadFragment(new Func2Fragment());
                return true;
            }
            return false;
        });

        // Load the initial fragment
        loadFragment(new HomeFragment());
    }

    private void loadFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
}
