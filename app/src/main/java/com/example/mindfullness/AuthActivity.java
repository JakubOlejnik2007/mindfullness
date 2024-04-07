package com.example.mindfullness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.mindfullness.helpers.SharedPreferencesManager;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.util.Map;


public class AuthActivity extends AppCompatActivity {

    private boolean isLogin = true;
    private TextView viewHeader;
    private EditText nameEditText;
    private EditText emailEdixtText;
    private EditText passwordEditText;
    private Button submit;
    private Button changeMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        viewHeader = findViewById(R.id.textLogin);
        nameEditText = findViewById(R.id.name);
        emailEdixtText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        submit = findViewById(R.id.button1);
        changeMethod = findViewById(R.id.button2);

        changeMethod.setOnClickListener(this::changeMethodListener);
        submit.setOnClickListener(this::submitListener);
        prepareView();

    }

    private void prepareView() {
        if(isLogin) {
            viewHeader.setText("Zaloguj się");
            nameEditText.setVisibility(nameEditText.GONE);
            submit.setText("Zaloguj się");
            changeMethod.setText("Zarejestruj się");
        } else {
            viewHeader.setText("Zarejestruj się");
            nameEditText.setVisibility(nameEditText.VISIBLE);
            submit.setText("Zarejestruj");
            changeMethod.setText("Zaloguj się");
        }
    }

    private void changeMethodListener(View view) {
        isLogin = !isLogin;
        prepareView();
    }

    private void submitListener(View view) {
        if(isLogin) logIn();
        else register();
    }
    private void logIn() {
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String email = emailEdixtText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document: queryDocumentSnapshots) {
                        String hashedPasswordFromDB = document.getString("password");

                        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                        if (encoder.matches(password, hashedPasswordFromDB)) {
                            String name = document.getString("name");
                            SharedPreferencesManager.saveData(this, name, email, 0);
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            showError("Nieprawidłowe dane logowania");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    showError("Błąd podczas logowania");
                });
    }

    private void register() {
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String name = nameEditText.getText().toString().trim();
        String email = emailEdixtText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Wypełnij wszystkie pola", Toast.LENGTH_SHORT).show();
            return;
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(password);

        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("name", name);
                        user.put("email", email);
                        user.put("password", hashedPassword);

                        db.collection("users")
                                .add(user)
                                .addOnSuccessListener(documentReference -> {
                                    SharedPreferencesManager.saveData(this, name, email, 0); // Tutaj może być inny sposób przechowywania danych, zależy to od Ciebie
                                    Intent intent = new Intent(this, MainActivity.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    showError("Błąd podczas dodawania");
                                });
                    } else {
                        showError("Użytkownik o podanym adresie email już istnieje");
                    }
                })
                .addOnFailureListener(e -> {
                    showError("Błąd podczas połączenia");
                });
    }


    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
