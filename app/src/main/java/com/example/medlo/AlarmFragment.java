// === AlarmFragment.java (Updated with delete + progress logic + reset if complete + reset button) ===
package com.example.medlo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AlarmFragment extends Fragment {

    private EditText medicineNameEditText;
    private Button selectTimeButton, selectDateButton, addReminderButton, saveButton, resetButton;
    private LinearLayout remindersContainer;
    private List<Reminder> remindersList = new ArrayList<>();

    private boolean remindersLocked = false;

    private Calendar selectedDateTime = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        medicineNameEditText = view.findViewById(R.id.medicineNameEditText);
        selectTimeButton = view.findViewById(R.id.selectTimeButton);
        selectDateButton = view.findViewById(R.id.selectDateButton);
        addReminderButton = view.findViewById(R.id.addReminderButton);
        saveButton = view.findViewById(R.id.saveButton);
        resetButton = view.findViewById(R.id.resetButton);
        remindersContainer = view.findViewById(R.id.remindersContainer);

        selectTimeButton.setOnClickListener(v -> showTimePicker());
        selectDateButton.setOnClickListener(v -> showDatePicker());

        addReminderButton.setOnClickListener(v -> {
            if (!remindersLocked) {
                addReminderToList();
            } else {
                Toast.makeText(getContext(), "Reminders are locked after saving", Toast.LENGTH_SHORT).show();
            }
            SharedPreferences sp = getActivity().getSharedPreferences("user_info", Context.MODE_PRIVATE);
            String userEmail = sp.getString("user_email", null);

            if (userEmail != null) {
                String medicineName = medicineNameEditText.getText().toString();
                String alarmTime = timeFormat.format(selectedDateTime.getTime());  // e.g. from TimePicker

                DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                boolean added = dbHelper.insertMedicine(medicineName, alarmTime, userEmail);

                if (added) {
                    Toast.makeText(getContext(), "Alarm saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to save alarm", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "No user info found", Toast.LENGTH_SHORT).show();
            }

        });

        saveButton.setOnClickListener(v -> saveAllReminders());

        resetButton.setOnClickListener(v -> resetAllReminders());

        updateDateTimeButtons();

        return view;
    }

    private void showTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(selectedDateTime.get(Calendar.HOUR_OF_DAY))
                .setMinute(selectedDateTime.get(Calendar.MINUTE))
                .setTitleText("Select Reminder Time")
                .build();

        timePicker.addOnPositiveButtonClickListener(view -> {
            selectedDateTime.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
            selectedDateTime.set(Calendar.MINUTE, timePicker.getMinute());
            updateDateTimeButtons();
        });

        timePicker.show(getParentFragmentManager(), "TIME_PICKER");
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(selectedDateTime.getTimeInMillis())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            selectedDateTime.setTimeInMillis(selection);
            updateDateTimeButtons();
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void updateDateTimeButtons() {
        selectTimeButton.setText(timeFormat.format(selectedDateTime.getTime()));
        selectDateButton.setText(dateFormat.format(selectedDateTime.getTime()));
    }

    private void addReminderToList() {
        String medicineName = medicineNameEditText.getText().toString().trim();

        if (medicineName.isEmpty()) {
            medicineNameEditText.setError("Please enter medicine name");
            return;
        }

        if (selectedDateTime.before(Calendar.getInstance())) {
            Toast.makeText(getContext(), "Please select a future time", Toast.LENGTH_SHORT).show();
            return;
        }

        Reminder reminder = new Reminder(medicineName, selectedDateTime.getTimeInMillis());
        remindersList.add(reminder);

        View reminderItem = LayoutInflater.from(getContext()).inflate(R.layout.item_reminder, remindersContainer, false);

        TextView itemName = reminderItem.findViewById(R.id.itemMedicineName);
        Button itemTime = reminderItem.findViewById(R.id.itemTimeButton);
        Button itemDate = reminderItem.findViewById(R.id.itemDateButton);
        Button deleteButton = reminderItem.findViewById(R.id.deleteButton);

        itemName.setText(reminder.getMedicineName());
        itemTime.setText(timeFormat.format(reminder.getTime()));
        itemDate.setText(dateFormat.format(reminder.getTime()));

        deleteButton.setOnClickListener(v -> {
            remindersContainer.removeView(reminderItem);
            remindersList.remove(reminder);

            AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getContext(), AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getContext(),
                    reminder.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.cancel(pendingIntent);

            SharedPreferences prefs = requireContext().getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE);
            int total = prefs.getInt("TotalReminder", 0);
            if (total > 0) {
                prefs.edit().putInt("TotalReminder", total - 1).apply();
            }

            int complete = prefs.getInt("CompleteReminder", 0);
            if (complete >= total - 1) {
                prefs.edit().putInt("TotalReminder", 0).putInt("CompleteReminder", 0).apply();
            }

            Toast.makeText(getContext(), "Reminder deleted", Toast.LENGTH_SHORT).show();
        });

        remindersContainer.addView(reminderItem);
        medicineNameEditText.setText("");
        selectedDateTime = Calendar.getInstance();
        updateDateTimeButtons();
    }

    private void saveAllReminders() {
        if (remindersList.isEmpty()) {
            Toast.makeText(getContext(), "No reminders to save", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE);
        int total = prefs.getInt("TotalReminder", 0);

        for (Reminder reminder : remindersList) {
            scheduleAlarm(reminder);
            total++;
        }

        prefs.edit().putInt("TotalReminder", total).apply();
        remindersLocked = true;

        Toast.makeText(getContext(), "Reminders saved and locked", Toast.LENGTH_SHORT).show();
        remindersList.clear();
        remindersContainer.removeAllViews();
    }

    private void resetAllReminders() {
        remindersList.clear();
        remindersContainer.removeAllViews();
        remindersLocked = false;

        SharedPreferences prefs = requireContext().getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE);
        prefs.edit().putInt("TotalReminder", 0).putInt("CompleteReminder", 0).apply();

        Toast.makeText(getContext(), "All reminders reset", Toast.LENGTH_SHORT).show();
    }

    private void scheduleAlarm(Reminder reminder) {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("medicineName", reminder.getMedicineName());
        intent.putExtra("requestCode", reminder.hashCode());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                reminder.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getTime(), pendingIntent);
    }
}

class Reminder {
    private final String medicineName;
    private final long time;

    public Reminder(String medicineName, long time) {
        this.medicineName = medicineName;
        this.time = time;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public long getTime() {
        return time;
    }
}
