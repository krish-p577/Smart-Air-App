package com.SmartAir.ParentDashboard.view;

import static com.SmartAir.ParentDashboard.view.ParentDashboardActivity.childId;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ScheduleActivity extends AppCompatActivity {

    // Variable to store the ID of the currently selected RadioButton
    private int selectedRadioButtonId = -1;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_page);

        TextView schedule_text = findViewById(R.id.current_selection);
        scheduleText(schedule_text);

        RadioGroup radioGroupOptions = findViewById(R.id.radio_buttons);
        Button savebut = findViewById(R.id.schedule_save);



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

                    scheduleText(schedule_text);



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
    }

    private void scheduleText(TextView text_box){
        db.collection("Users")
                .document(childId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                   if (documentSnapshot.exists()){
                       text_box.setText("Current Schedule: " + documentSnapshot.get("schedule"));
                   }

                    AdherenceCalculator.calculate(childId, (String) documentSnapshot.get("schedule"), new AdherenceCalculator.AdherenceCallback() {
                        @Override
                        public void onResult(int adherencePercentage, boolean isCurrentlyCompliant) {

                            // Update UI
                            TextView scoreView = findViewById(R.id.adheranceView);
                            TextView statusView = findViewById(R.id.adherence_progress);

                            scoreView.setText("Adherence Score: " + adherencePercentage + "%");

                            if (isCurrentlyCompliant) {
                                statusView.setText("Status: On Track");
                                statusView.setTextColor(Color.GREEN);
                            } else {
                                statusView.setText("Status: Overdue");
                                statusView.setTextColor(Color.RED);
                            }
                        }
                    });

                });
    }

    private String adherenceCalculation(String childID){


        return "";
    }
}
