package me.davidkurniawan.MovieAppz.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

public class Preference {
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private Context context;

    public static final String LANGUAGE = "language";
    private static final String PREFERENECE = "pref";

    @SuppressLint("CommitPrefEdits")
    public Preference(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFERENECE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSPString(String keySP, String value) {
        editor.putString(keySP, value);
        editor.commit();
    }

    public String getLanguage(){
        return sharedPreferences.getString(LANGUAGE, "");
    }
}
