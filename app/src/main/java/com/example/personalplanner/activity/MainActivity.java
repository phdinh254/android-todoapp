package com.example.personalplanner.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.personalplanner.R;
import com.example.personalplanner.fragment.CalendarFragment;
import com.example.personalplanner.fragment.HomeFragment;
import com.example.personalplanner.fragment.ProfileFragment;
import com.example.personalplanner.fragment.TaskFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private int currentSelectedItemId = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        handleBottomNavigation();
    }

    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    private void handleBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                currentSelectedItemId = R.id.nav_home;
                loadFragment(new HomeFragment());
                return true;

            } else if (itemId == R.id.nav_task) {
                currentSelectedItemId = R.id.nav_task;
                loadFragment(new TaskFragment());
                return true;

            } else if (itemId == R.id.nav_add) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);

                // Nút Thêm chỉ mở màn hình mới, không phải tab cố định.
                bottomNavigationView.post(() -> bottomNavigationView.setSelectedItemId(currentSelectedItemId));
                return false;

            } else if (itemId == R.id.nav_calendar) {
                currentSelectedItemId = R.id.nav_calendar;
                loadFragment(new CalendarFragment());
                return true;

            } else if (itemId == R.id.nav_profile) {
                currentSelectedItemId = R.id.nav_profile;
                loadFragment(new ProfileFragment());
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
