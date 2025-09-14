package com.example.medlo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    ProgressBar progressBar;
    int progressStatus = 0;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background_splashscreen);

        progressBar = findViewById(R.id.splashProgress);

        SharedPreferences prefs = getSharedPreferences("ReminderPrefs", MODE_PRIVATE);
        if (!prefs.contains("Initialized")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("TotalReminder", 0);
            editor.putInt("CompleteReminder", 0);
            editor.putBoolean("Initialized", true); // Mark that it's been initialized
            editor.apply();
        }


        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus += 1;
                handler.post(() -> progressBar.setProgress(progressStatus));
                try {
                    Thread.sleep(30); // speed of loading
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Once done, navigate to Signup screen
            handler.post(() -> {
                Intent intent = new Intent(SplashScreen.this, Signup.class);
                startActivity(intent);
                finish();
            });
        }).start();
    }
}
