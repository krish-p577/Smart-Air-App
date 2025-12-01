package com.SmartAir.ParentDashboard.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.SmartAir.R;
import com.SmartAir.onboarding.model.CurrentUser;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.Timestamp;
import com.SmartAir.ParentDashboard.model.PefLogsModel;
import com.SmartAir.ParentDashboard.model.RescueLogModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ParentDashboardActivity extends AppCompatActivity {

    public static String childId = "";
    private static final String TESTUSERID = "voS60SSmSSZL9j3XGKyhHNSs4LR2";
    private FirebaseFirestore db;
    private boolean isTestMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_dashboard);

        db = FirebaseFirestore.getInstance();
        isTestMode = getIntent().getBooleanExtra("testMode", false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.parent_dashboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner spinner = findViewById(R.id.mySpinner);
        List<String> childList = new ArrayList<>();
        List<String> childIdList = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, childList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        getUserChildren(adapter, childList, childIdList);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position > 0) { // To account for the 'Select Child' prompt
                    childId = childIdList.get(position - 1);
                    updateZone(childId, findViewById(R.id.myText), findViewById(R.id.myText3), findViewById(R.id.myText4));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        // Other button initializations can go here
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void getUserChildren(ArrayAdapter<String> adapter, List<String> childList, List<String> childIdList) {
        String uid = isTestMode ? TESTUSERID : (CurrentUser.getInstance().getUserProfile() != null ? CurrentUser.getInstance().getUid() : null);

        if (uid == null) {
            Toast.makeText(this, "Error: User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        childList.add("Select Child:"); // Prompt
        adapter.notifyDataSetChanged();

        db.collection("Users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> rawIdList = (List<String>) documentSnapshot.get("childrenIds");
                if (rawIdList != null && !rawIdList.isEmpty()) {
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    for (String id : rawIdList) {
                        tasks.add(db.collection("Users").document(id).get());
                    }
                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(list -> {
                        for (Object object : list) {
                            DocumentSnapshot childSnapshot = (DocumentSnapshot) object;
                            if (childSnapshot.exists()) {
                                String displayName = childSnapshot.getString("displayName");
                                if (displayName != null) {
                                    childList.add(displayName);
                                    childIdList.add(childSnapshot.getId());
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    });
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(ParentDashboardActivity.this, "Failed to load children.", Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("SetTextI18n")
    protected void updateZone(String childID, TextView box1, TextView box2, TextView box3){
        Log.i("DEBUG", "function initilize");

        db.collection("pefLogs").
                document(childID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        PefLogsModel info = documentSnapshot.toObject(PefLogsModel.class);
                        assert info != null;
                        box1.setText("Today's Zone:    " + info.getZone());
                        Log.i("PEF LOGS", "Got info on zone: " + info.getZone());
                    }else{
                        Log.e("PEF LOGS", "NO ZONE FOUND?");
                        box1.setText("Today's Zone not added yet");
                    }

                })

                .addOnFailureListener(e ->{
                    Log.e("PEF LOG", "cant get" + e.getMessage(), e);

                });

        db.collection("rescueLogs")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    DocumentSnapshot mostRecentLog = null;
                    Timestamp latestTimestamp = null;

                    int count = 0;
                    // Loop through documents and find the most recent log for this childID
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        if (childID.equals(doc.getString("childid"))) {
                            count += 1;
                            Timestamp ts = doc.getTimestamp("timestamp"); // use getTimestamp()
                            if (ts != null && (latestTimestamp == null || ts.compareTo(latestTimestamp) > 0)) {
                                latestTimestamp = ts;
                                mostRecentLog = doc;
                            }
                        }
                        box3.setText("Weekly Rescue Count: " + count);
                    }

                    if (mostRecentLog != null && latestTimestamp != null) {
                        RescueLogModel log = mostRecentLog.toObject(RescueLogModel.class);
                        if (log != null) {
                            // Convert Timestamp to Date
                            Date date = latestTimestamp.toDate();
                            box2.setText("Last Rescue Time: " + date.toString());
                            return;
                        }else{
                            box2.setText("Last Rescue Time: N/A");
                        }
                    }


                    // No logs found
                    box2.setText("No rescue logs found.");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching logs", e);
                    box2.setText("Error fetching logs");
                });




    }

}
