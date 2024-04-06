package com.example.mindfullness.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mindfullness.databinding.FragmentDashboardBinding;
import com.example.mindfullness.helpers.SharedPreferencesManager;

public class Dashboard extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        textView.setText("Konto");

        TextView nameTextView = binding.name;
        TextView emailTextView = binding.email;
        SeekBar range = binding.seekBar;

        String[] userData = SharedPreferencesManager.readData(getContext());
        String name = userData[0];
        String email = userData[1];
        int rangeProgress = Integer.parseInt(userData[2]);

        if (name.isEmpty() || email.isEmpty()) {
            nameTextView.setText("Brak danych");
            emailTextView.setText("Brak danych");
            range.setProgress(60);
        } else {
            nameTextView.setText(name);
            emailTextView.setText(email);
            range.setProgress(rangeProgress);
        }

        range.setOnSeekBarChangeListener(setListener());

        return root;
    }

    private SeekBar.OnSeekBarChangeListener setListener() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
