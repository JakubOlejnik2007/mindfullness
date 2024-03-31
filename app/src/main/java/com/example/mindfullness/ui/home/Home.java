package com.example.mindfullness.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mindfullness.databinding.FragmentHomeBinding;
import com.example.mindfullness.helpers.HTTPJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Home extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        textView.setText("This is home fragment");
        displayArticles();
        return root;
    }

    public void displayArticles() {
        Log.d("Article", "Displaying articles");
        HTTPJson api = new HTTPJson();
        api.sendGetRequest("https://jsonplaceholder.typicode.com/posts/", new HTTPJson.OnResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray articlesArray = new JSONArray(response);
                    for (int i = 0; i < articlesArray.length(); i++) {
                        JSONObject article = articlesArray.getJSONObject(i);
                        Log.d("Article " + i, article.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("Error", "Error parsing JSON response: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                Log.e("Error", "Error while fetching articles: " + error);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
