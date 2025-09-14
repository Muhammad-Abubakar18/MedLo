package com.example.medlo;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class DismissReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int requestCode = intent.getIntExtra("requestCode", 0);

        // Cancel the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(requestCode);

        SharedPreferences prefs = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE);
        int completed = prefs.getInt("CompleteReminder", 0);
        int total = prefs.getInt("TotalReminder", 0);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("CompleteReminder", completed + 1);

        // Decrease total reminder count (optional but logical for a dismissed task)
        if (total > 0) {
            editor.putInt("TotalReminder", total - 1);
        }

        // Optionally save last taken medicine info
        String lastMedicine = intent.getStringExtra("medicineName");
        if (lastMedicine != null) {
            editor.putString("LastTakenMedicine", lastMedicine);
        }

        editor.apply();
    }

}
