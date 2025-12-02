package com.SmartAir.ParentDashboard.view;

import static com.SmartAir.ParentDashboard.view.ParentDashboardActivity.childId;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.BaseUser;
import com.SmartAir.onboarding.model.CurrentUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ScheduleActivity extends AppCompatActivity {

    // Variable to store the ID of the currently selected RadioButton
    private int selectedRadioButtonId = -1;

    BaseUser user =  CurrentUser.getInstance().getUserProfile();

    private int currentLookBack = 7;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_page);
        RadioGroup timeToggle = findViewById(R.id.toggle_time_window);

        TextView schedule_text = findViewById(R.id.current_selection);
        scheduleText(schedule_text, currentLookBack);

        RadioGroup radioGroupOptions = findViewById(R.id.radio_buttons);
        Button savebut = findViewById(R.id.schedule_save);

        Toolbar toolbar = findViewById(R.id.toolbarsc);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.parent_dashboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // managing scheduling radio button changes
        radioGroupOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectedRadioButtonId = checkedId; // Store the ID for later use

                RadioButton selectedRadioButton = findViewById(checkedId);

                if (selectedRadioButton != null && selectedRadioButtonId != -1) {
                    String selectedOptionText = selectedRadioButton.getText().toString();
                    Toast.makeText(ScheduleActivity.this, "Selected: " + selectedOptionText, Toast.LENGTH_SHORT).show();
                }
            }
        });

        savebut.setOnClickListener(v -> {
            if (selectedRadioButtonId != -1) {
                RadioButton finalSelectedRadioButton = findViewById(selectedRadioButtonId);

                if (finalSelectedRadioButton != null) {
                    String selectedOptionText = finalSelectedRadioButton.getText().toString();

                    String cid = childId;

                    scheduleText(schedule_text, currentLookBack);

                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("schedule", selectedOptionText);

                    db.collection("Users")
                            .document(cid) // 'cid' is your childId
                            .set(userUpdates, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(ScheduleActivity.this, "Schedule saved!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ScheduleActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });



                    Toast.makeText(ScheduleActivity.this, "Saving selection: " + selectedOptionText, Toast.LENGTH_LONG).show();

                }
            } else {
                // Handle the case where the user tries to save without making a selection
                Toast.makeText(ScheduleActivity.this, "Please select an option first.", Toast.LENGTH_SHORT).show();
            }
        });

        timeToggle.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.btn_7_days) {
                currentLookBack = 7;

            } else if (checkedId == R.id.btn_30_days) {
                currentLookBack = 30;
            }
            scheduleText(schedule_text, currentLookBack);
        });

    }
    private void scheduleText(TextView scheduleDisplayView, int daysLookBack) {

        db.collection("Users")
                .document(childId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String scheduleType = documentSnapshot.getString("schedule");
                        if (scheduleType == null) scheduleType = "Daily";

                        scheduleDisplayView.setText("Current Schedule: " + scheduleType);

                        // Run Calculator
                        AdherenceCalculator.calculate(childId, scheduleType, daysLookBack, (percentage, isCurrentlyCompliant) -> {


                            // FIND VIEWS - Wrap in try-catch to spot ID errors
                            try {
                                TextView scoreView = findViewById(R.id.adherance_view); // CHECK THIS ID
                                TextView statusView = findViewById(R.id.adherence_progress);    // CHECK THIS ID
                                ProgressBar progressBar = findViewById(R.id.adherence_bar); // CHECK THIS ID

                                if (scoreView == null || progressBar == null) {
                                    return;
                                }

                                // Update UI
                                scoreView.setText("Adherence Record: " + percentage + "%");
                                progressBar.setProgress(percentage);

                                // Color Logic...
                                int color = (percentage >= 80) ? Color.parseColor("#4CAF50") :
                                        (percentage >= 50) ? Color.parseColor("#FFC107") :
                                                Color.parseColor("#F44336");

                                String statusMessage = (percentage >= 80) ? "Status: On Track" :
                                        (percentage >= 50) ? "Status: Needs Improvement" :
                                                "Status: Overdue";

                                statusView.setText(statusMessage);
                                statusView.setTextColor(color);
                                progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(color));


                            } catch (Exception e) {
                                Log.e("DEBUG_APP", "Error updating UI: " + e.getMessage());
                                e.printStackTrace();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DEBUG_APP", "Error fetching User Schedule: " + e.getMessage());
                });
    }
}
