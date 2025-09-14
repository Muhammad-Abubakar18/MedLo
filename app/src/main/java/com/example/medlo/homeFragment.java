package com.example.medlo;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class homeFragment extends Fragment {

    private ProgressBar circularProgressBar;
    private TextView percentageText;

    private TextView nameText, emailText, numberText, lastMedText, lastTimeText;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public homeFragment() {
        // Required empty public constructor
    }

    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProgressUI();  // Refresh progress when fragment becomes visible again
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        percentageText = view.findViewById(R.id.percentageText);

        nameText = view.findViewById(R.id.nameText);
        emailText = view.findViewById(R.id.emailText);
        numberText = view.findViewById(R.id.numberText);
        lastMedText = view.findViewById(R.id.lastMedText);
        lastTimeText = view.findViewById(R.id.lastTimeText);

        SharedPreferences prefs = requireContext().getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE);
        int total = prefs.getInt("TotalReminder", 0);
        int complete = prefs.getInt("CompleteReminder", 0);
        int remainingPercent = (total == 0) ? 0 : (int) (((total - complete) * 100.0f) / total);

        circularProgressBar.setProgress(remainingPercent);
        percentageText.setText(remainingPercent + "% Remaining");

        SharedPreferences sp = requireContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String userEmail = sp.getString("user_email", null);

        if (userEmail != null) {
            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());

            User user = dbHelper.getUserByEmail(userEmail);
            if (user != null) {
                nameText.setText("Name: " + user.getName());
                emailText.setText("Email: " + user.getEmail());
                numberText.setText("Number: " + user.getPhone());
            }

            Medicine medicine = dbHelper.getLastMedicineByEmail(userEmail);
            if (medicine != null) {
                lastMedText.setText("Last Medicine: " + medicine.getName());
                lastTimeText.setText("Time: " + medicine.getTime());
            }
        }

        return view;
    }

    private void updateProgressUI() {
        SharedPreferences prefs = requireContext().getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE);
        int total = prefs.getInt("TotalReminder", 0);
        int completed = prefs.getInt("CompleteReminder", 0);

        float remainingPercent = (total > 0) ? ((float) (total - completed) / total) * 100f : 0f;
        int percentToDisplay = (int) Math.min(remainingPercent, 100);

        ProgressBar progressBar = getView().findViewById(R.id.circularProgressBar);
        TextView progressText = getView().findViewById(R.id.percentageText);
        TextView status = getView().findViewById(R.id.reminderStatus);

        progressBar.setProgress(percentToDisplay);
        progressText.setText(percentToDisplay + "% Remaining");
        status.setText("Completed " + completed + " of " + total + " reminders");
    }
}
