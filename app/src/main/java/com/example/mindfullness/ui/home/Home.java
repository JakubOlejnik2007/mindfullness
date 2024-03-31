package com.example.mindfullness.ui.home;

import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Home extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        textView.setText("This is home fragment");

        // Dodanie menedżera układu do RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.articlesView.setLayoutManager(layoutManager);

        displayArticles();

        return root;
    }

    public void displayArticles() {
        HTTPJson api = new HTTPJson();
        api.sendGetRequest("https://jsonplaceholder.typicode.com/posts/", new HTTPJson.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray articlesArray = new JSONArray(response);
                    ArrayList<Article> articles = new ArrayList<>();
                    for (int i = 0; i < articlesArray.length(); i++) {
                        JSONObject article = articlesArray.getJSONObject(i);
                        Article articleCasted = new Article(article.getString("title"), article.getString("body"));
                        articles.add(articleCasted);
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArticleAdapter adapter = new ArticleAdapter(articles);
                            binding.articlesView.setAdapter(adapter);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                // Obsługa błędu
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
