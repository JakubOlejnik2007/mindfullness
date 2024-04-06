package com.example.mindfullness.adapters;

import com.example.mindfullness.types.Article;
import com.example.mindfullness.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private List<Article> dataSet;

    public ArticleAdapter(List<Article> dataSet) {
        this.dataSet = dataSet;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = dataSet.get(position);
        TextView textViewTitle = holder.itemView.findViewById(R.id.textViewTitle);
        textViewTitle.setText(article.title);
        TextView textViewBody = holder.itemView.findViewById(R.id.textViewBody);
        textViewBody.setText(article.content);
        ImageView articleThumbnail = holder.itemView.findViewById(R.id.articleThumbnail);
        article.loadImageIntoImageView(articleThumbnail);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
