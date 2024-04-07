package com.example.mindfullness.types;


import java.util.Date;

public class Entry {
    public String username;
    public String content;
    public Date timestamp;

    public Entry(String username, String content, Date time){
        this.username = username;
        this.content = content;
        this.timestamp = time;
    }
}
