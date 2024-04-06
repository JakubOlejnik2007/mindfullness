package com.example.mindfullness.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "MyPreferences";


    public static void saveData(Context context, String username, String name, String email, int controlValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putInt("controlValue", controlValue);
        editor.apply();
    }


    public static String[] readData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");
        String name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email", "");
        String controlValue = String.valueOf(sharedPreferences.getInt("controlValue", 0));
        return new String[]{username, name, email, controlValue};
    }


    public static void removeNameAndEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("name");
        editor.remove("email");
        editor.apply();
    }
}
