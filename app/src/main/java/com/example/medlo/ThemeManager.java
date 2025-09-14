package com.example.medlo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    public static final String PREF_NAME = "theme_pref";
    public static final String THEME_KEY = "current_theme";

    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_GREY = 2;

    public static void applyTheme(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int theme = prefs.getInt(THEME_KEY, THEME_LIGHT);

        switch (theme) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_GREY:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // avoid full dark
                activity.setTheme(R.style.Theme_Medlo_Grey); // apply grey
                break;
        }
    }

    public static void cycleTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int currentTheme = prefs.getInt(THEME_KEY, THEME_LIGHT);
        int nextTheme = (currentTheme + 1) % 3;
        prefs.edit().putInt(THEME_KEY, nextTheme).apply();
    }
}
