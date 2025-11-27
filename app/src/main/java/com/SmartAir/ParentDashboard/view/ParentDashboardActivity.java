package com.SmartAir.ParentDashboard.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.SmartAir.ParentDashboard.model.ParentModel;
import com.SmartAir.ParentDashboard.model.PefLogsModel;
import com.SmartAir.ParentDashboard.model.RescueLogModel;
import com.SmartAir.ParentDashboard.presenter.ParentDashboardPresenter;
import com.SmartAir.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.LoginException;

public class ParentDashboardActivity extends AppCompatActivity implements ParentDashboardView {

    public static String childId = "";

    private static String TESTUSERID = "voS60SSmSSZL9j3XGKyhHNSs4LR2";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ParentDashboardPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_dashboard);

        // naving to schedule innit
        Button schedule_button = findViewById(R.id.radio_buttons);
        schedule_button.setOnClickListener(v -> {
            if (!childId.isEmpty()) {
                Intent intent = new Intent(this, ScheduleActivity.class);
                startActivity(intent);

                finish();
            }
            else {
                Toast.makeText(ParentDashboardActivity.this, "No child selected", Toast.LENGTH_LONG).show();
            }
        });



        FirebaseApp.initializeApp(this);


        Log.i("TAG", "CREATED PAGE");


        TextView test_text = findViewById(R.id.r6_test);
        TextView box1 = findViewById(R.id.myText);
        TextView box2 = findViewById(R.id.myText3);
        TextView box3 = findViewById(R.id.myText4);
        Button pdfbutton = findViewById(R.id.pdfbutton);


        List<ReportGenerationActivity.CheckIn> data = Arrays.asList(
                new ReportGenerationActivity.CheckIn("2024-11-15", "Red", 4, "Wheezing, Cough", "Had trouble sleeping due to cough."),
                new ReportGenerationActivity.CheckIn("2024-11-14", "Yellow", 1, "Mild Cough", "Played soccer, used rescue once.")
                );
        pdfbutton.setOnClickListener(v -> {
            View reportContent = ReportGenerationActivity.createMockReportView(this, data);

            File pdfFile = ReportGenerationActivity.generatePdfFromView(this, reportContent, "AsthamaReport");

            if (pdfFile != null) {
                ReportGenerationActivity.sharePdfFile(this, pdfFile);
            }

        });

        Spinner spinner = findViewById(R.id.mySpinner);

        List<String> childList = new ArrayList<>();
        List<String> childIdList = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                childList
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        getUserChildren(adapter, childList, childIdList);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedChild = childList.get(position);
                String selectedId = childIdList.get(position);
//                dbTest(box1,selectedChild);
                childId = selectedId;
                updateZone(selectedId, box1, box2, box3);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }

    @Override
    public void navToPdf () {
//        View reportContent = ReportGenerationActivity.createMockReportView(this, data);




    }


    @SuppressLint("SetTextI18n")
    protected void getUserChildren(ArrayAdapter<String> adapter, List<String> childList, List<String> childIdList){
        Log.i("DEBUG", "Get Children");

        Log.i("DEBUG", "Get Children started");

        // Clear all except the default prompt
        if (childList.size() > 1) childList.subList(1, childList.size()).clear();
        if (childIdList.size() > 1) childIdList.subList(1, childIdList.size()).clear();

        db.collection("Users").document("6DD7WbduSwQ1rdNOmdlGU15JOsp2")
                .get().addOnSuccessListener(documentSnapshot ->{

                    List<String> rawIdList = new ArrayList<>();

                    if(documentSnapshot.exists()){
                        // 1. Get the list of IDs from the parent document
                        try {
                            @SuppressWarnings("unchecked")
                            List<String> tempIdList = (List<String>) documentSnapshot.get("childrenIds");

                            if (tempIdList != null) {
                                rawIdList.addAll(tempIdList); // Use rawIdList for iteration
                            }

                        } catch (ClassCastException e) {
                            Log.e("FirestoreRead", "The 'childrenIds' field data structure is incorrect.", e);
                        }

                        if (rawIdList.isEmpty()) {
                            // If no children, immediately update the adapter (which now only has the prompt)
                            adapter.notifyDataSetChanged();
                            return;
                        }

                        // Initialize a counter to track remaining async tasks
                        final int totalChildren = rawIdList.size();
                        final int[] completedTasks = {0};

                        // Initialize a temporary list to hold names in the correct, sequential order
                        // We use null placeholders so we can use set(index) later.
                        final List<String> orderedNames = new ArrayList<>(Collections.nCopies(totalChildren, (String) null));

                        // 2. Loop through IDs and fetch display names, maintaining order
                        for (int i = 0; i < totalChildren; i++){
                            final String id = rawIdList.get(i);
                            final int index = i; // Store the original sequential index

                            db.collection("Users").document(id).get()
                                    .addOnSuccessListener(documentSnapshot1 -> {

                                        String displayName = (String) documentSnapshot1.get("displayName");
                                        if (displayName != null) {
                                            // ADD to the temporary list at the guaranteed correct index
                                            orderedNames.set(index, displayName);
                                        } else {
                                            // Handle missing display name by inserting a placeholder
                                            orderedNames.set(index, "[No Name]");
                                        }

                                        // 3. Increment counter and check if all tasks are done
                                        completedTasks[0]++;

                                        if (completedTasks[0] == totalChildren) {
                                            // All data is now loaded and in the correct order in orderedNames

                                            // Re-populate final lists sequentially
                                            for (int j = 0; j < totalChildren; j++) {
                                                childList.add(orderedNames.get(j));
                                                childIdList.add(rawIdList.get(j));
                                            }

                                            Log.i("DEBUG", "All children loaded. Final List: " + childList.toString());
                                            adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Important: Even on failure, we must complete the task count
                                        completedTasks[0]++;
                                        Log.e("FirestoreRead", "Failed to fetch child details for ID: " + id, e);

                                        if (completedTasks[0] == totalChildren) {
                                            // Notify the adapter even if there were failures
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        } // end of for loop

                    } else {
                        // Document doesn't exist, just update adapter with only the prompt
                        adapter.notifyDataSetChanged();
                    }

                }).addOnFailureListener(e ->{
                    Log.e("ERRORR", "CANNOT GET PARENT DOCUMENT: " + e.getMessage(), e);
                });

//        db.collection("Users").document("voS60SSmSSZL9j3XGKyhHNSs4LR2")
//                        .get().addOnSuccessListener(documentSnapshot ->{
//                            childList.clear();
//                            childIdList.clear();
//
//                            Log.i("CHILDREN FOUND", "CHILDS: " + documentSnapshot.get("childrenIds"));
//
//                            if(documentSnapshot.exists()){
//                                List<String> childIds = new ArrayList<>();
//
//                                try {
//                                    @SuppressWarnings("unchecked")
//                                    List<String> rawList = (List<String>) documentSnapshot.get("childrenIds");
//
//                                    // 3. Check if the list is present and not null before using it.
//                                    if (rawList != null) {
//                                        childIdList.addAll(rawList);
//                                    }
//
//                                } catch (ClassCastException e) {
//                                    Log.e("FirestoreRead", "The 'children' field data structure is not a List of Strings.", e);
//                                }
//
//                                Log.i("CHILDREN FOUND", "CHILDS: " + childIdList.toString());
//
//                            }
//
//                            for (String id: childIdList){
//                                db.collection("Users").document(id).get()
//                                        .addOnSuccessListener(documentSnapshot1 -> {
//                                            childList.add((String) documentSnapshot1.get("displayName"));
//                                        });
//                            }
//                            adapter.notifyDataSetChanged();
//                }).addOnFailureListener(e ->{
//                    Log.e("ERRORR", "CANNOT GET CHILDREN" + e.getMessage(), e);
//                });




//        db.collection("users").document("1")
//                .collection("children")
//                .get().
//                addOnSuccessListener(queryDocumentSnapshots -> {
//                    childList.clear();
//
//                    for (DocumentSnapshot document : queryDocumentSnapshots) {
//                        String childName = document.getString("name");
//                        if (childName != null) {
//                            childList.add(childName);
//                            childIdList.add(document.getId());
//                        }
//                    }
//
//                    for (DocumentSnapshot document : queryDocumentSnapshots) {
//
//                    }
//
//                    adapter.notifyDataSetChanged();
//                    Log.i("SPINNER TAG", "Loaded Children" + childList);
//
//                }).addOnFailureListener(e ->{
//                    Log.e("SPINNER FAILUE", "ERR", e);
//                });

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


    @SuppressLint("SetTextI18n")
    protected void dbTest(TextView test_text, String selectedChild){
        Log.i("DEBUG", "function initilize");

        db.collection("sers").
                document("1")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ParentModel user = documentSnapshot.toObject((ParentModel.class));
                        String name = documentSnapshot.getString("displayName");
//                        String role = documentSnapshot.getString("role");

                        assert user != null;
                        test_text.setText("Name: " + user.getName());

                        Log.i("DEBUG", "DEBUG NAME:" + name + "   " );
                    }
                })
                .addOnFailureListener(e ->{

                });

    }



}
