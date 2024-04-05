package com.example.mindfullness.types;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.example.mindfullness.R;
import com.squareup.picasso.Picasso;

public class Article {
    public String title;
    public String description;
    public String content;
    public String imageUrl;

    public Article(String title, String description, String content, String imageUrl) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public void loadImageIntoImageView(ImageView imageView) {
        Picasso.get().load(imageUrl).placeholder(R.drawable.image).into(imageView);
    }
}
