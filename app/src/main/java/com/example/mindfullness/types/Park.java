package com.example.mindfullness.types;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Park {
    private double latitude;
    private double longitude;
    private String name;

    public interface OnParkInitializedListener {
        void onParkInitialized(Park park);
    }

    public Park(JSONObject parkObject, OnParkInitializedListener listener) throws JSONException {
        Log.d("Parki", parkObject.getString("type"));
        switch (parkObject.getString("type")) {
            case "node":
                initFromNode(parkObject);
                listener.onParkInitialized(this);
                break;
            case "way":
                initFromWay(parkObject, listener);
                break;
            case "relation":
                initFromRelation(parkObject);
                listener.onParkInitialized(this);
                break;
        }

        Log.d("Parki1", this.getParkInfo());
    }

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private void initFromWay(final JSONObject wayObject, OnParkInitializedListener listener) throws JSONException {
        JSONArray nodes = wayObject.getJSONArray("nodes");
        final double[] sumLat = {0};
        final double[] sumLon = {0};
        final int[] nodeCount = {0};
        for (int i = 0; i < nodes.length(); i++) {
            final int finalI = i;
            executor.execute(() -> {
                try {
                    long nodeId = nodes.getLong(finalI);
                    Double[] nodeInfo = fetchNodeInfo(nodeId);
                    if (nodeInfo != null) {
                        synchronized (this) {
                            sumLat[0] += nodeInfo[0];
                            sumLon[0] += nodeInfo[1];
                            nodeCount[0]++;
                            if (nodeCount[0] == nodes.length()) {
                                if (nodeCount[0] > 0) {
                                    latitude = sumLat[0] / nodeCount[0];
                                    longitude = sumLon[0] / nodeCount[0];
                                }
                                synchronized (wayObject) {
                                    try {
                                        JSONObject tags = wayObject.getJSONObject("tags");
                                        name = tags.has("name") ? tags.getString("name") : "Park bez nazwy";
                                        listener.onParkInitialized(this);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private Double[] fetchNodeInfo(long nodeId) throws IOException, Exception {
        String url = "https://api.openstreetmap.org/api/0.6/node/" + nodeId;
        HttpURLConnection connection = null;
        try {
            URL apiUrl = new URL(url);
            connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String responseString = stringBuilder.toString();

            // Utwórz parser XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(responseString));
            Document doc = builder.parse(inputSource);

            // Pobierz element <node>
            Element nodeElement = (Element) doc.getElementsByTagName("node").item(0);

            // Pobierz wartości lat i lon
            double lat = Double.parseDouble(nodeElement.getAttribute("lat"));
            double lon = Double.parseDouble(nodeElement.getAttribute("lon"));

            return new Double[]{lat, lon};
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void initFromNode(JSONObject nodeObject) throws JSONException {
        this.latitude = nodeObject.getDouble("lat");
        this.longitude = nodeObject.getDouble("lon");
        this.name = nodeObject.getJSONObject("tags").getString("name");
    }

    private void initFromRelation(JSONObject relationObject) throws JSONException {
        JSONArray members = relationObject.getJSONArray("members");
        double sumLat = 0;
        double sumLon = 0;
        int count = 0;
        for (int i = 0; i < members.length(); i++) {
            JSONObject member = members.getJSONObject(i);
            if (member.getString("type").equals("way") && member.getString("role").equals("outer")) {
                JSONArray nodes = member.getJSONArray("nodes");
                for (int j = 0; j < nodes.length(); j++) {
                    JSONObject node = nodes.getJSONObject(j);
                    sumLat += node.getDouble("lat");
                    sumLon += node.getDouble("lon");
                    count++;
                }
            }
        }
        this.latitude = sumLat / count;
        this.longitude = sumLon / count;
        this.name = relationObject.getJSONObject("tags").getString("name");
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getParkInfo() {
        return String.format("%s, %.2f, %.2f", this.name, this.latitude, this.longitude);
    }
}
