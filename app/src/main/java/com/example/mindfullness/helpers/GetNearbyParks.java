package com.example.mindfullness.helpers;

import android.location.Location;
import android.util.Log;

import com.example.mindfullness.types.Park;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetNearbyParks {
    public ArrayList<Park> getNearbyParks(Location currentLocation) {

        ArrayList<Park> parksArray = new ArrayList<>();

        if (currentLocation == null) {
            Log.e("Mapa", "Brak dostępu do lokalizacji");
            return parksArray;
        }

        // Adres URL zapytania Overpass API
        String overpassUrl = "https://overpass-api.de/api/interpreter?data=" +
                "[out:json];" +
                "(node[\"leisure\"=\"park\"](around:50000," + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + ");" +
                "way[\"leisure\"=\"park\"](around:50000," + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + ");" +
                "relation[\"leisure\"=\"park\"](around:50000," + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "););" +
                "out;";

        // Wysłanie zapytania HTTP i obsługa odpowiedzi
        HTTPJson httpJson = new HTTPJson();
        httpJson.sendGetRequest(overpassUrl, new HTTPJson.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    JSONArray parksArrayJSON = responseJSON.getJSONArray("elements");
                    for(int i = 0; i < parksArrayJSON.length(); i++) {
                        JSONObject park = (JSONObject) parksArrayJSON.get(i);
                        Park parkToPush = new Park(park);
                        parksArray.add(parkToPush);
                    }
                } catch (JSONException e) {
                    Log.e("Error", "Error while parsing json" + e.toString());
                }

                Log.d("Parki", parksArray.toString());
                Log.d("Parki", String.valueOf(parksArray.size()));
            }

            @Override
            public void onError(String error) {
                Log.e("Błąd", error);
            }
        });
        return parksArray;
    }
}
