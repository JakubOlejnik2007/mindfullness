package com.example.mindfullness;

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
import java.util.Date;
import java.util.List;


public class Social extends AppCompatActivity {

    private List<Entry> entries;
    private RecyclerView recyclerView;
    private EntryAdapter entryAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

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
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getString("username");
                            String content = document.getString("content");
                            long timestamp = document.getTimestamp("timestamp").getSeconds() * 1000;
                            Entry entry = new Entry(username, content, new Date(timestamp));
                            entries.add(entry);
                        }
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
