package com.example.medlo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    private static final String PREFS_NAME = "MedicineReminderPrefs";
    private static final String REMINDERS_KEY = "savedReminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
                intent.getAction().equals("android.intent.action.QUICKBOOT_POWERON")) {

            Log.d(TAG, "Device booted, restoring alarms");
            restoreAlarms(context);
        }
    }

    private void restoreAlarms(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String remindersJson = prefs.getString(REMINDERS_KEY, null);

        if (remindersJson != null) {
            try {
                JSONArray remindersArray = new JSONArray(remindersJson);

                for (int i = 0; i < remindersArray.length(); i++) {
                    JSONObject reminderJson = remindersArray.getJSONObject(i);
                    String medicineName = reminderJson.getString("medicineName");
                    long timeInMillis = reminderJson.getLong("time");
                    int requestCode = reminderJson.getInt("requestCode");

                    // Only restore future alarms
                    if (timeInMillis > Calendar.getInstance().getTimeInMillis()) {
                        scheduleAlarm(context, medicineName, timeInMillis, requestCode);
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing saved reminders", e);
            }
        }
    }

    private void scheduleAlarm(Context context, String medicineName, long timeInMillis, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("medicineName", medicineName);
        intent.putExtra("requestCode", requestCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
        );
    }
}
