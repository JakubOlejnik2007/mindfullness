package com.example.mindfullness.helpers;

import android.location.Location;
import android.util.Log;

import com.example.mindfullness.types.Park;
import com.example.mindfullness.types.Park.OnParkInitializedListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetNearbyParks {
    public interface ParksCallback {
        void onParksReceived(ArrayList<Park> parksArray);
        void onError(String error);
    }

    public void getNearbyParks(Location currentLocation, ParksCallback callback) {

        if (currentLocation == null) {
            callback.onError("Brak dostępu do lokalizacji");
            return;
        }

        // Adres URL zapytania Overpass API
        String overpassUrl = "https://overpass-api.de/api/interpreter?data=" +
                "[out:json];" +
                "(node[\"leisure\"=\"park\"](around:20000," + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + ");" +
                "way[\"leisure\"=\"park\"](around:20000," + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + ");" +
                "relation[\"leisure\"=\"park\"](around:20000," + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "););" +
                "out;";

        // Wysłanie zapytania HTTP i obsługa odpowiedzi
        HTTPJson httpJson = new HTTPJson();
        httpJson.sendGetRequest(overpassUrl, new HTTPJson.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                ArrayList<Park> parksArray = new ArrayList<>();
                try {
                    JSONObject responseJSON = new JSONObject(response);
                    JSONArray parksArrayJSON = responseJSON.getJSONArray("elements");
                    for (int i = 0; i < parksArrayJSON.length(); i++) {
                        JSONObject park = (JSONObject) parksArrayJSON.get(i);
                        if (park.getString("type").equals("relation")) continue;
                        // Dodaj argument OnParkInitializedListener
                        Park parkToPush = new Park(park, new OnParkInitializedListener() {
                            @Override
                            public void onParkInitialized(Park park) {
                                // Po zakończeniu inicjalizacji obiektu Park, dodaj go do listy
                                parksArray.add(park);
                                // Jeśli wszystkie parki zostały dodane, wywołaj callback.onParksReceived
                                if (parksArray.size() == parksArrayJSON.length()) {
                                    callback.onParksReceived(parksArray);
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    callback.onError("Error while parsing json" + e.toString());
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
}

