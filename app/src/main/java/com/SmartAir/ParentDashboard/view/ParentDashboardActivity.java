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
import com.SmartAir.ParentDashboard.model.ParentModel;
import com.SmartAir.ParentDashboard.model.PefLogsModel;
import com.SmartAir.ParentDashboard.model.RescueLogModel;
import com.SmartAir.ParentDashboard.presenter.ParentDashboardPresenter;
import com.SmartAir.R;
import com.SmartAir.dailycheckin.view.DailyCheckInActivity;
import com.SmartAir.history.view.HistoryActivity;
import com.SmartAir.onboarding.model.BaseUser;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.model.ParentUser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;
import com.SmartAir.ParentDashboard.model.PefLogsModel;
import com.SmartAir.ParentDashboard.model.RescueLogModel;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ParentDashboardActivity extends AppCompatActivity {

    public static String childId = "";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    BaseUser user =  CurrentUser.getInstance().getUserProfile();

    String childZone = "Pending";

    AtomicReference<String> childNameRef = new AtomicReference<>("");
    AtomicReference<String> zoneRef = new AtomicReference<>("");

    private ParentDashboardPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_dashboard);
        FirebaseApp.initializeApp(this);

        List<String> childids_fromuser;
        //grabbing userid innit

        if (user instanceof ParentUser){
            childids_fromuser = ((ParentUser) user).getChildrenIds();

        }
        checkRescuAlert(childId);
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

        Button pbButton = findViewById(R.id.SetPBButtons);
        pbButton.setOnClickListener(v -> {
            Toast.makeText(ParentDashboardActivity.this, "outside IF", Toast.LENGTH_LONG).show();
            if (!childId.isEmpty()) {
                Toast.makeText(ParentDashboardActivity.this, "in IF", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ParentDashboardActivity.this, PbActivity.class);
                intent.putExtra("childId", childId);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(ParentDashboardActivity.this, "No child selected", Toast.LENGTH_LONG).show();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.parent_dashboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView test_text = findViewById(R.id.r6_test);
        TextView box1 = findViewById(R.id.myText);
        TextView box2 = findViewById(R.id.myText3);
        TextView box3 = findViewById(R.id.myText4);
        Button reportBut = findViewById(R.id.btn_generate_report);

        reportBut.setOnClickListener(v -> generateComprehensiveReport(30));

        Button historyBtn = findViewById(R.id.history_btn);
        historyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        });

        Button checkin_btn = findViewById(R.id.checkin);
        checkin_btn.setOnClickListener(v -> {
            Intent intent = new Intent(this, DailyCheckInActivity.class);
            startActivity(intent);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void navToPdf () {
//        View reportContent = ReportGenerationActivity.createMockReportView(this, data);

    }

    private void searchForCheckIns(String childName,
                                   List<ReportGenerationActivity.DailyLog> reportLogs,
                                   AtomicInteger tasksCompleted,
                                   AtomicReference<Integer> reportAdherence,
                                   AtomicReference<String> childNameRef,
                                   AtomicReference<String> zoneRef ) {

        db.collection("daily_check_ins")
                .whereEqualTo("Child", childName) // Querying by ID is safer than Name
                .orderBy("timestamp", Query.Direction.DESCENDING) // Get newest first
                .get()
                .addOnSuccessListener(querySnap -> {
                    for (QueryDocumentSnapshot doc : querySnap) {
                        Log.i("LOGS", "FOUND LOG HERE" );
                        String date = doc.getString("timestamp");
                        List<String> triggers = (List<String>) doc.get("Triggers");
                        if (triggers == null) {
                            triggers = new ArrayList<>();
                        }
                        Log.i("LOGS", "FOUND LOG HERE" + triggers.get(0));

                        // Add to list
                        reportLogs.add(new ReportGenerationActivity.DailyLog(date, triggers, ""));
                    }
                    // Mark Task 3 as done
                    tasksCompleted.incrementAndGet();
                    checkIfDataReady(tasksCompleted, reportLogs, reportAdherence, childNameRef, zoneRef, 7);
                })
                .addOnFailureListener(e -> {
                    Log.e("REPORT", "Error fetching logs", e);
                    tasksCompleted.incrementAndGet(); // Proceed even if logs fail
                    checkIfDataReady(tasksCompleted, reportLogs, reportAdherence, childNameRef, zoneRef, 7);
                });

    }

    private void generateComprehensiveReport(int currentLookBack) {
        Toast.makeText(this, "Gathering data...", Toast.LENGTH_SHORT).show();

        // We need to fetch 3 things asynchronously. We'll use a Counter to know when done.
        AtomicInteger tasksCompleted = new AtomicInteger(0);
        AtomicReference<Integer> reportAdherence = new AtomicReference<>(0);
        List<ReportGenerationActivity.DailyLog> reportLogs = Collections.synchronizedList(new ArrayList<>());

        // TASK 1: Get User Details (Name & Schedule for calculator)
        db.collection("Users").document(childId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String fetchedChildName = doc.getString("displayName"); // Assuming 'name' field exists
                if (fetchedChildName == null) fetchedChildName = "Child";

                childNameRef.set(doc.getString("displayName"));

//                // Fetch Zone (Assuming 'currentZone' is stored in Users, otherwise default)
//                String fetchedCurrentZone = doc.getString("currentZone");
//                if (fetchedCurrentZone == null) fetchedCurrentZone = "Pending";

                zoneRef.set(childZone);

                String fetchedCurrentZone = childZone;

                String scheduleType = doc.getString("schedule");
                if (scheduleType == null) scheduleType = "Daily";



                // TASK 2: Get Adherence Score (Nested because we need scheduleType)
                AdherenceCalculator.calculate(childId, scheduleType, currentLookBack, (score, compliant) -> {
                    reportAdherence.set(score);
                    checkIfDataReady(tasksCompleted, reportLogs, reportAdherence, childNameRef, zoneRef, 7);
                });
                searchForCheckIns(childNameRef.get(), reportLogs, tasksCompleted, reportAdherence, childNameRef, zoneRef);            }

        });



        // Mark Task 1 (User fetch) as technically initiated, but the logic inside handles the completion flow
        tasksCompleted.incrementAndGet();
    }

    private void checkIfDataReady(AtomicInteger tasks, List<ReportGenerationActivity.DailyLog> logs, AtomicReference<Integer> scoreRef,
                                  AtomicReference<String> childNameRef, AtomicReference<String> zoneRef, long lookBackDays) {
        // We expect 2 main async branches to finish (User+Adherence branch, and Logs branch)
        if (tasks.get() >= 2) {
            // All data is here!
            String name = childNameRef.get();
            String zone = zoneRef.get();
            int score = scoreRef.get();

            ReportGenerationActivity.AsthmaReportData data = new ReportGenerationActivity.AsthmaReportData(
                    name,
                    zone,
                    score,
                    "Last " + "30" + " Days",
                    logs
            );

            // Generate and Share
            File pdf = ReportGenerationActivity.generatePdfFromData(this, data);
            ReportGenerationActivity.sharePdfFile(this, pdf);
        }
    }


    @SuppressLint("SetTextI18n")
    protected void getUserChildren(ArrayAdapter<String> adapter, List<String> childList, List<String> childIdList){
        Log.i("DEBUG", "Get Children");

        Log.i("DEBUG", "Get Children started");

        // Clear all except the default prompt
        if (childList.size() > 1) childList.subList(1, childList.size()).clear();
        if (childIdList.size() > 1) childIdList.subList(1, childIdList.size()).clear();

        db.collection("Users").document(CurrentUser.getInstance().getUid())
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
                    Log.e("ERROR", "CANNOT GET PARENT DOCUMENT: " + e.getMessage(), e);
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
                        childZone = info.getZone();
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

    private void checkRescuAlert(String childId){
        long currentTime = System.currentTimeMillis();

        db.collection("inhalerLogs")
                .document(childId)
                .collection("rescueEntries")
                .orderBy("timestamp")   // newest last; we’ll break early if needed
                .get()
                .addOnSuccessListener(querySnap -> {
                    int count = 0;

                    for (DocumentSnapshot doc : querySnap.getDocuments()) {
                        Timestamp ts = doc.getTimestamp("timestamp");

                        int diffMs = Math.toIntExact(currentTime - ts.toDate().getTime());

                        // within last 3 hours?
                        if (diffMs <= (3 * 60 * 60 * 1000)) {
                            count++;
                        }
                    }

                    if (count >= 3) {
                        Toast.makeText(ParentDashboardActivity.this, "Child Tried to resuce " + count + " in last 3 hours.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("RESCUE_CHECK", "Error checking rescue attempts", e);
                });
    }


}