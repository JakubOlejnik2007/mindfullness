package com.example.mindfullness.helpers;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class MongoDBHandler {
    private String mongoURL = "mongodb+srv://jacobole2000:AZm6UqEzSk0Qyezw@mindfullness1.nvmesu0.mongodb.net/?retryWrites=true&w=majority&appName=Mindfullness1";
    private MongoClient mongoClient;
    private MongoDatabase database;

    public void connect() {
        String connectionString = mongoURL;

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("mindfullness");
    }

    public void handleQuery(Document query) {
        // Obsługa przesłanego zapytania
        // Możesz wykorzystać tę metodę do wykonania operacji na bazie danych, na przykład wstawiania, aktualizacji, usuwania dokumentów
        // Przykładowe użycie: database.getCollection("nazwaKolekcji").insertOne(query);
    }

    public void disconnect() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
