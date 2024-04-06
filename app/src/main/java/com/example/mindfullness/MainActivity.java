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

        // Sprawdź, czy aplikacja ma uprawnienie do wysyłania powiadomień
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // Jeśli aplikacja nie ma uprawnienia, poproś użytkownika o nie
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST_CODE);
        } else {
            // Jeśli aplikacja ma uprawnienie, kontynuuj z ustawianiem pracy cyklicznej
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

    // Metoda do obsługi odpowiedzi na żądanie uprawnień
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Uprawnienie zostało udzielone, kontynuuj z ustawianiem pracy cyklicznej
                setupNotificationWork();
            } else {
                // Uprawnienie nie zostało udzielone, poinformuj użytkownika lub podejmij inne działania
                Toast.makeText(this, "Aplikacja wymaga uprawnienia do wysyłania powiadomień.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Metoda do ustawiania pracy cyklicznej i powiadomień
    // Metoda do ustawiania pracy cyklicznej i powiadomień
    private void setupNotificationWork() {
        PeriodicWorkRequest notificationWorkRequest =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                NotificationWorker.NOTIFICATION_WORK_TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                notificationWorkRequest);

        // Dodanie logu do LogCat po dodaniu oczekiwania na powiadomienie
        Log.d("NotificationWork", "Dodano oczekiwanie na powiadomienie");
    }


    private int getTitleResById(int itemId) {
        String[] parts = getResources().getResourceEntryName(itemId).split("_");
        String resourceName = "title_" + parts[1];
        return getResources().getIdentifier(resourceName, "string", getPackageName());
    }
}
