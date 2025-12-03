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
import com.google.firebase.firestore.ListenerRegistration;

import com.SmartAir.ParentDashboard.model.PefLogsModel;
import com.SmartAir.ParentDashboard.model.RescueLogModel;
import com.SmartAir.R;
import com.SmartAir.dailycheckin.view.DailyCheckInActivity;
import com.SmartAir.history.view.HistoryActivity;
import com.SmartAir.onboarding.model.BaseUser;
import com.SmartAir.onboarding.model.CurrentUser;
import com.SmartAir.onboarding.model.ParentUser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ParentDashboardActivity extends AppCompatActivity {

    private static final String TAG = "ParentDashboardActivity";
    private static final String CHILD_ID_KEY = "childId";
    private static final String USERS_COLLECTION = "Users";
    private static final String PEF_LOGS_COLLECTION = "pefLogs";
    private static final String RESCUE_LOGS_COLLECTION = "rescueLogs";
    private static final String INVENTORY_COLLECTION = "inventory";
    private static final String INHALERS_SUBCOLLECTION = "inhalers";
    private static final String DAILY_CHECK_INS_COLLECTION = "daily_check_ins";

    public static String childId = "";

    private ListenerRegistration pefListener;
    private ListenerRegistration rescueListener;
    private ListenerRegistration inhalerListener;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final BaseUser user = CurrentUser.getInstance().getUserProfile();
    private String childZone = "Pending";
    private final Date appStartTime = new Date();
    private final int months = 3;

    private final AtomicReference<String> childNameRef = new AtomicReference<>("");
    private final AtomicReference<String> zoneRef = new AtomicReference<>("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_dashboard);
        FirebaseApp.initializeApp(this);

        if (user instanceof ParentUser) {
            // No-op
        }

        setupUI();
    }

    private void setupUI() {
        Button schedule_button = findViewById(R.id.radio_buttons);
        schedule_button.setOnClickListener(v -> {
            if (!childId.isEmpty()) {
                Intent intent = new Intent(this, ScheduleActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(ParentDashboardActivity.this, "No child selected", Toast.LENGTH_LONG).show();
            }
        });

        Button pbButton = findViewById(R.id.SetPBButtons);
        pbButton.setOnClickListener(v -> {
            if (!childId.isEmpty()) {
                Intent intent = new Intent(ParentDashboardActivity.this, PbActivity.class);
                intent.putExtra(CHILD_ID_KEY, childId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ParentDashboardActivity.this, "No child selected", Toast.LENGTH_LONG).show();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.parent_dashboard);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView box1 = findViewById(R.id.myText);
        TextView box2 = findViewById(R.id.myText3);
        TextView box3 = findViewById(R.id.myText4);
        Button reportBut = findViewById(R.id.btn_generate_report);

        reportBut.setOnClickListener(v -> generateComprehensiveReport());

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
                childId = childIdList.get(position);
                updateZone(childId, box1, box2, box3);
                startPefListener();
                startRescueListener();
                startInventoryListener();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (pefListener != null) pefListener.remove();
        if (rescueListener != null) rescueListener.remove();
        if (inhalerListener != null) inhalerListener.remove();
    }

    public Task<List<Integer>> getZoneCounts(String childId) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -months);
        Date startDate = calendar.getTime();

        Query query = db.collection(PEF_LOGS_COLLECTION)
                .whereEqualTo(CHILD_ID_KEY, childId)
                .whereGreaterThanOrEqualTo("timestamp", startDate)
                .orderBy("timestamp", Query.Direction.ASCENDING);

        return query.get().continueWith(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            QuerySnapshot snapshot = task.getResult();
            int redCount = 0;
            int yellowCount = 0;
            int greenCount = 0;

            for (DocumentSnapshot document : snapshot.getDocuments()) {
                String zone = document.getString("zone");
                if (zone != null) {
                    switch (zone.toLowerCase()) {
                        case "red":
                            redCount++;
                            break;
                        case "yellow":
                            yellowCount++;
                            break;
                        case "green":
                            greenCount++;
                            break;
                    }
                }
            }

            List<Integer> counts = new ArrayList<>();
            counts.add(redCount);
            counts.add(yellowCount);
            counts.add(greenCount);

            return counts;
        });
    }

    private void startPefListener() {
        pefListener = db.collection(PEF_LOGS_COLLECTION)
                .whereEqualTo(CHILD_ID_KEY, childId)
                .whereEqualTo("zone", "red")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    assert snapshots != null;
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            Date logDate = dc.getDocument().getDate("timestamp");
                            if (logDate != null) {
                                showToast("ALERT: Child entered RED ZONE!");
                            }
                        }
                    }
                });
    }

    private void startRescueListener() {
        rescueListener = db.collection(RESCUE_LOGS_COLLECTION)
                .whereEqualTo(CHILD_ID_KEY, childId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    assert snapshots != null;
                    if (snapshots.size() < 3) return;

                    Date newest = snapshots.getDocuments().get(0).getDate("timestamp");
                    Date oldest = snapshots.getDocuments().get(2).getDate("timestamp");

                    if (newest != null && oldest != null) {
                        long diff = newest.getTime() - oldest.getTime();
                        long threeHours = 3 * 60 * 60 * 1000;
                        if (diff <= threeHours && newest.after(appStartTime)) {
                            showToast("ALERT: 3 Rescue inhalers used in 3 hours!");
                        }
                    }
                });
    }

    private void startInventoryListener() {
        String path = INVENTORY_COLLECTION + "/" + childId + "/" + INHALERS_SUBCOLLECTION;
        inhalerListener = db.collection(path)
                .whereEqualTo("expiredFlag", true)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) return;
                    if (snapshots != null && !snapshots.isEmpty()) {
                        showToast("WARNING: An inhaler in inventory has EXPIRED!");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void generateComprehensiveReport() {
        Toast.makeText(this, "Gathering data...", Toast.LENGTH_SHORT).show();

        AtomicInteger tasksCompleted = new AtomicInteger(0);
        AtomicReference<Integer> reportAdherence = new AtomicReference<>(0);
        List<ReportGenerationActivity.DailyLog> reportLogs = Collections.synchronizedList(new ArrayList<>());

        Runnable checkCompletion = () -> {
            if (tasksCompleted.get() >= 3) {
                String name = childNameRef.get();
                String zone = zoneRef.get();
                int score = reportAdherence.get();

                getZoneCounts(childId).addOnSuccessListener(countsList -> {
                    ReportGenerationActivity.AsthmaReportData data = new ReportGenerationActivity.AsthmaReportData(
                            name, zone, score, "Last 30 Days", reportLogs, countsList
                    );
                    File pdf = ReportGenerationActivity.generatePdfFromData(this, data);
                    ReportGenerationActivity.sharePdfFile(this, pdf);
                });
            }
        };

        fetchUserDetails(tasksCompleted, checkCompletion, reportAdherence, reportLogs);
    }

    private void fetchUserDetails(AtomicInteger tasksCompleted, Runnable onComplete, AtomicReference<Integer> reportAdherence, List<ReportGenerationActivity.DailyLog> reportLogs) {
        db.collection(USERS_COLLECTION).document(childId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                childNameRef.set(doc.getString("displayName"));
                zoneRef.set(childZone);
                String scheduleType = doc.getString("schedule");
                if (scheduleType == null) scheduleType = "Daily";

                tasksCompleted.incrementAndGet();

                calculateAdherence(scheduleType, tasksCompleted, onComplete, reportAdherence);
                searchForCheckIns(childNameRef.get(), reportLogs, tasksCompleted, onComplete);
            }
        });
    }

    private void calculateAdherence(String scheduleType, AtomicInteger tasksCompleted, Runnable onComplete, AtomicReference<Integer> reportAdherence) {
        AdherenceCalculator.calculate(childId, scheduleType, 30, (score, compliant) -> {
            reportAdherence.set(score);
            tasksCompleted.incrementAndGet();
            onComplete.run();
        });
    }

    private void searchForCheckIns(String childName, List<ReportGenerationActivity.DailyLog> reportLogs, AtomicInteger tasksCompleted, Runnable onComplete) {
        db.collection(DAILY_CHECK_INS_COLLECTION)
                .whereEqualTo("Child", childName)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(querySnap -> {
                    for (QueryDocumentSnapshot doc : querySnap) {
                        String date = doc.getString("timestamp");
                        List<String> triggers = doc.get("Triggers", List.class);
                        if (triggers == null) {
                            triggers = new ArrayList<>();
                        }
                        reportLogs.add(new ReportGenerationActivity.DailyLog(date, triggers, ""));
                    }
                    tasksCompleted.incrementAndGet();
                    onComplete.run();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching logs", e);
                    tasksCompleted.incrementAndGet();
                    onComplete.run();
                });
    }

    @SuppressLint("SetTextI18n")
    protected void getUserChildren(ArrayAdapter<String> adapter, List<String> childList, List<String> childIdList) {
        if (childList.size() > 1) childList.subList(1, childList.size()).clear();
        if (childIdList.size() > 1) childIdList.subList(1, childIdList.size()).clear();

        db.collection(USERS_COLLECTION).document(Objects.requireNonNull(CurrentUser.getInstance().getUid()))
                .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> rawIdList = (List<String>) documentSnapshot.get("childrenIds");
                        if (rawIdList == null || rawIdList.isEmpty()) {
                            adapter.notifyDataSetChanged();
                            return;
                        }

                        final int totalChildren = rawIdList.size();
                        final List<String> orderedNames = new ArrayList<>(Collections.nCopies(totalChildren, null));

                        for (int i = 0; i < totalChildren; i++) {
                            final String id = rawIdList.get(i);
                            final int index = i;

                            db.collection(USERS_COLLECTION).document(id).get()
                                    .addOnSuccessListener(documentSnapshot1 -> {
                                        String displayName = documentSnapshot1.getString("displayName");
                                        orderedNames.set(index, displayName != null ? displayName : "[No Name]");

                                        if (orderedNames.stream().allMatch(Objects::nonNull)) {
                                            childList.addAll(orderedNames);
                                            childIdList.addAll(rawIdList);
                                            adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Failed to fetch child details for ID: " + id, e));
                        }
                    }
                }).addOnFailureListener(e -> Log.e(TAG, "CANNOT GET PARENT DOCUMENT: " + e.getMessage(), e));
    }

    @SuppressLint("SetTextI18n")
    protected void updateZone(String childID, TextView box1, TextView box2, TextView box3) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(appStartTime);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startOfDay = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date endOfDay = cal.getTime();

        db.collection(PEF_LOGS_COLLECTION)
                .whereEqualTo(CHILD_ID_KEY, childID)
                .whereGreaterThanOrEqualTo("timestamp", startOfDay)
                .whereLessThanOrEqualTo("timestamp", endOfDay)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        PefLogsModel info = document.toObject(PefLogsModel.class);
                        if (info != null) {
                            box1.setText("Today's Zone:    " + info.getZone());
                            childZone = info.getZone();
                        }
                    } else {
                        box1.setText("Today's Zone not added yet");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error getting logs: " + e.getMessage(), e));

        db.collection(RESCUE_LOGS_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    DocumentSnapshot mostRecentLog = null;
                    Timestamp latestTimestamp = null;
                    int count = 0;

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        if (childID.equals(doc.getString("childid"))) {
                            count++;
                            Timestamp ts = doc.getTimestamp("timestamp");
                            if (ts != null && (latestTimestamp == null || ts.compareTo(latestTimestamp) > 0)) {
                                latestTimestamp = ts;
                                mostRecentLog = doc;
                            }
                        }
                    }
                    box3.setText("Weekly Rescue Count: " + count);

                    if (mostRecentLog != null) {
                        RescueLogModel log = mostRecentLog.toObject(RescueLogModel.class);
                        if (log != null) {
                            Date date = latestTimestamp.toDate();
                            box2.setText("Last Rescue Time: " + date);
                        } else {
                            box2.setText("Last Rescue Time: N/A");
                        }
                    } else {
                        box2.setText("No rescue logs found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching logs", e);
                    box2.setText("Error fetching logs");
                });
    }
}
