package com.SmartAir.ParentDashboard.view;

import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdherenceCalculator {

    public interface AdherenceCallback {
        void onResult(int adherencePercentage, boolean isCurrentlyCompliant);
    }

    public static void calculate(String childId, String scheduleType, AdherenceCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1. Define the "Lookback Period" (e.g., analyze the last 30 days)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -30);
        Date thirtyDaysAgo = cal.getTime();

        // 2. Fetch ALL logs for this user from the last 30 days
        db.collection("controllerLogs")
                .whereEqualTo("childId", childId)
                .whereGreaterThan("timestamp", new Timestamp(thirtyDaysAgo))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Date> logDates = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Timestamp ts = doc.getTimestamp("timestamp");
                        if (ts != null) {
                            logDates.add(ts.toDate());
                            Log.i("CALCULATOR", "Adherance info " + ts.toDate());
                        }
                    }
                    Log.i("CALCULATOR", "did query");

                    // 3. Process the math locally
                    processAdherenceMath(scheduleType, logDates, callback);
                });
    }

    private static void processAdherenceMath(String scheduleType, List<Date> logs, AdherenceCallback callback) {
        // Definitions of intervals in Days
        int intervalDays = 1; // Default Daily
        int bucketsToCheck = 30; // Check last 30 days

        switch (scheduleType) {
            case "Weekly":
                intervalDays = 7;
                bucketsToCheck = 4; // Check last 4 weeks (28 days)
                break;
            case "Biweekly":
                intervalDays = 14;
                bucketsToCheck = 2; // Check last 2 fortnights (28 days)
                break;
            case "Monthly":
                intervalDays = 30;
                bucketsToCheck = 1; // Check last month
                break;
        }

        int successfulBuckets = 0;
        boolean isCurrentBucketSuccess = false;

        Calendar cursor = Calendar.getInstance(); // Start at "Now"

        // 4. Iterate backward through the buckets
        for (int i = 0; i < bucketsToCheck; i++) {

            // Define the specific window for this bucket
            Date windowEnd = cursor.getTime();
            cursor.add(Calendar.DAY_OF_YEAR, -intervalDays); // Move back 1 interval
            Date windowStart = cursor.getTime();

            // Check if ANY log falls in this window
            boolean hit = false;
            for (Date logDate : logs) {
                if (logDate.after(windowStart) && logDate.before(windowEnd)) {
                    hit = true;
                    break;
                }
            }

            if (hit) {
                successfulBuckets++;
                if (i == 0) isCurrentBucketSuccess = true; // The most recent bucket (Now)
            }
        }

        // 5. Final Calculation
        int percentage = (int) (((float) successfulBuckets / bucketsToCheck) * 100);

        // Return results to the Activity
        callback.onResult(percentage, isCurrentBucketSuccess);
    }
}