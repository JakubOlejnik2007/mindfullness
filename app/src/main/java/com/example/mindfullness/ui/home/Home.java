package com.example.mindfullness.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mindfullness.databinding.FragmentHomeBinding;
import com.example.mindfullness.helpers.HTTPJson;
import com.example.mindfullness.types.Article;
import com.example.mindfullness.adapters.ArticleAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

public class Home extends Fragment {

    private FragmentHomeBinding binding;
    private ArrayList<Article> articles = new ArrayList<Article>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        textView.setText("Witaj!");



        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.articlesView.setLayoutManager(layoutManager);

        displayArticles();


        return root;
    }

    public void displayArticles() {
        FirebaseApp.initializeApp(requireContext());
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("articles")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            articles = new ArrayList<>();
                            // Przetwarzanie wyników pobrania
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                articles.add(new Article(document.getString("title"), document.getString("abstract"), document.getString("content"), document.getString("thumbnail")));

                            }
                            ArticleAdapter adapter = new ArticleAdapter(articles);
                            binding.articlesView.setAdapter(adapter);
                        } else {
                            // Obsługa błędów
                            Log.w("Articles", "Błąd pobierania dokumentów", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
