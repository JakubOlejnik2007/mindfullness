package com.example.mindfullness;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.mindfullness.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

    private int getTitleResById(int itemId) {
        String[] parts = getResources().getResourceEntryName(itemId).split("_");
        String resourceName = "title_" + parts[1];
        return getResources().getIdentifier(resourceName, "string", getPackageName());
    }

}
