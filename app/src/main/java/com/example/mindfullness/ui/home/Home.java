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
import java.util.Random;

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
        displayRandomChallenge();

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

    private void displayRandomChallenge() {
        String[] wyzwaniaMindfulness = {
                "Zatrzymaj się na chwilę i skoncentruj się na oddechu przez 5 minut.",
                "Spójrz na swój otaczający świat i zidentyfikuj pięć rzeczy, które dostrzegasz, czujesz, słyszysz i wąchasz.",
                "Przeprowadź 10-minutową praktykę wdzięczności, wymieniając trzy rzeczy, za które jesteś dziś wdzięczny/a.",
                "Zanurz się w chwili teraźniejszej, zwracając uwagę na swoje zmysły: co teraz słyszysz, widzisz, czujesz?",
                "Wykonaj ćwiczenie skupienia uwagi, licząc oddechy od 1 do 10, starając się skoncentrować tylko na oddechu.",
                "Zaplanuj krótką przerwę w ciągu dnia, aby zrobić coś, co sprawia ci przyjemność, bez pośpiechu i bez myślenia o innych obowiązkach.",
                "Przypomnij sobie swoje cele i zastanów się, co możesz dzisiaj zrobić, aby się do nich zbliżyć.",
                "Zanurz się w naturze przez kilka minut, oddychając świeże powietrze i dostrzegając piękno otaczającego cię świata.",
                "Zrób sobie przerwę od ekranu przez co najmniej godzinę, skupiając się na aktywnościach, które nie wymagają korzystania z urządzeń elektronicznych.",
                "Praktykuj akceptację, starając się zaakceptować swoje myśli, emocje i doświadczenia bez oceniania czy krytyki."
        };
        Random random = new Random();
        binding.challangetext.setText(wyzwaniaMindfulness[random.nextInt()%10]);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
