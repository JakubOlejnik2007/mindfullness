package com.example.mindfullness;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.work.BackoffPolicy;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.mindfullness.databinding.ActivityMainBinding;
import com.example.mindfullness.helpers.NotificationWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MindfullnessLight);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST_CODE);
        } else {
            setupNotificationWork();
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        for (int i = 1; i < navView.getMenu().size(); i++) {
            navView.getMenu().getItem(i).setTitle(null);
        }

        navView.setOnNavigationItemSelectedListener(item -> {
            for (int i = 0; i < navView.getMenu().size(); i++) {
                int itemId = navView.getMenu().getItem(i).getItemId();
                String title = getResources().getString(getTitleResById(itemId));
                navView.getMenu().getItem(i).setTitle(title);
            }

            for (int i = 0; i < navView.getMenu().size(); i++) {
                navView.getMenu().getItem(i).setTitle(null);
            }

            item.setTitle(getResources().getString(getTitleResById(item.getItemId())));
            return NavigationUI.onNavDestinationSelected(item, navController);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupNotificationWork();
            } else {
                Toast.makeText(this, "Aplikacja wymaga uprawnienia do wysyłania powiadomień.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupNotificationWork() {
        PeriodicWorkRequest notificationWorkRequest =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                NotificationWorker.NOTIFICATION_WORK_TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                notificationWorkRequest);

        Log.d("NotificationWork", "Zaplanowano cykliczne zadanie wysyłające powiadomienie");
    }




    private int getTitleResById(int itemId) {
        String[] parts = getResources().getResourceEntryName(itemId).split("_");
        String resourceName = "title_" + parts[1];
        return getResources().getIdentifier(resourceName, "string", getPackageName());
    }
}
