package com.example.mindfullness.helpers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPJson {

    public interface OnResponseListener {
        void onResponse(String response);
        void onError(String error);
    }

    public void sendGetRequest(String urlString, OnResponseListener listener) {
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    listener.onResponse(response.toString());
                } else {
                    listener.onError("HTTP Error: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                listener.onError("Exception: " + e.getMessage());
            }
        }).start();
    }
}
