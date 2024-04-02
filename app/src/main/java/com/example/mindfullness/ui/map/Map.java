package com.example.mindfullness.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.example.mindfullness.databinding.FragmentMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import com.example.mindfullness.helpers.GetNearbyParks;
import com.example.mindfullness.types.Park;
import java.util.ArrayList;

public class Map extends Fragment {

    private ArrayList<Park> parksArray = new ArrayList<Park>();
    private FragmentMapBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Configuration.getInstance().setUserAgentValue("Mindfulness");

        MapView map = binding.map;
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        getLocation();

        return root;
    }

    private interface ParksCallback {
        void onParksReceived(ArrayList<Park> parksArray);
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.requestLocationUpdates(
                LocationRequest.create(),
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        if (locationResult != null && locationResult.getLastLocation() != null) {
                            currentLocation = locationResult.getLastLocation();
                            MapView map = binding.map;
                            map.getController().setCenter(new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude()));
                            map.getController().setZoom(15);
                            Log.d("Mapa", String.valueOf(currentLocation.getLatitude()));
                            Log.d("Mapa", String.valueOf(currentLocation.getLongitude()));
                            fusedLocationClient.removeLocationUpdates(this);

                            // Wywołanie funkcji do pobrania lokalizacji parków
                            GetNearbyParks get = new GetNearbyParks();
                            get.getNearbyParks(currentLocation, new GetNearbyParks.ParksCallback() {
                                @Override
                                public void onParksReceived(ArrayList<Park> parksArray) {
                                    addParkMarkers(binding.map, parksArray);
                                }

                                @Override
                                public void onError(String error) {
                                    Log.e("Błąd", error);
                                }
                            });
                        }
                    }
                },
                null);
    }

    private void addParkMarkers(MapView map, ArrayList<Park> parksArray) {
        Log.d("Parki", "Drawing markers");
        for (Park park : parksArray) {
            Log.d("ParkiMarkers", park.getParkInfo());
            GeoPoint parkLocation = new GeoPoint(park.getLatitude(), park.getLongitude());
            Marker parkMarker = new Marker(map);
            parkMarker.setPosition(parkLocation);
            parkMarker.setTitle(park.getName());
            parkMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(parkMarker);
        }
        map.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
