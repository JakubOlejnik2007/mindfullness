package com.example.mindfullness.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "MyPreferences";


    public static void saveData(Context context, String name, String email, int controlValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putInt("controlValue", controlValue);
        editor.apply();
    }


    public static String[] readData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String email = sharedPreferences.getString("email", "");
        String controlValue = String.valueOf(sharedPreferences.getInt("controlValue", 60));
        return new String[]{name, email, controlValue};
    }


    public static void removeNameAndEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("name");
        editor.remove("email");
        editor.apply();
    }

    public static void modifyProgress(Context context, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("controlValue");
        editor.putInt("controlValue", value);
        editor.apply();
    }
}
