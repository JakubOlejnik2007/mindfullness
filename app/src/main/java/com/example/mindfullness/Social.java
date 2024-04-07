package com.example.mindfullness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.example.mindfullness.types.Entry;
import com.example.mindfullness.adapters.EntryAdapter;
import com.example.mindfullness.helpers.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class Social extends AppCompatActivity {

    private List<Entry> entries;
    private RecyclerView recyclerView;
    private EntryAdapter entryAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        if(Objects.equals(SharedPreferencesManager.readData(this)[0], "")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }


        entries = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerViewEntries);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        entryAdapter = new EntryAdapter(entries);
        recyclerView.setAdapter(entryAdapter);

        db = FirebaseFirestore.getInstance();

        getEntriesFromFirebase();



        Button button = findViewById(R.id.button);
        button.setOnClickListener(this::handleSendMessage);

    }

    private void handleSendMessage(View view) {
        EditText editText = findViewById(R.id.editTextText);
        if(!editText.getText().toString().trim().isEmpty()) addEntryToFirebase(SharedPreferencesManager.readData(this)[0], editText.getText().toString().trim());
    }

    private void getEntriesFromFirebase() {
        db.collection("entries")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Entry> tempEntries = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getString("username");
                            String content = document.getString("content");
                            long timestamp = document.getTimestamp("timestamp").getSeconds() * 1000;
                            Entry entry = new Entry(username, content, new Date(timestamp));
                            tempEntries.add(entry);
                        }
                        Collections.sort(tempEntries, new Comparator<Entry>() {

                            public int compare(Entry entry1, Entry entry2) {
                                return entry2.timestamp.compareTo(entry1.timestamp);
                            }
                        });
                        entries.clear();
                        entries.addAll(tempEntries);
                        entryAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void addEntryToFirebase(String username, String content) {
        Entry entry = new Entry(username, content, new Date());

        db.collection("entries").add(entry)
                .addOnSuccessListener(documentReference -> {
                    // Zaktualizuj pole timestamp w dodanym wpisie
                    documentReference.update("timestamp", FieldValue.serverTimestamp());
                    entries.add(entry);
                    entryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Obsłuż błąd
                });
    }
}
