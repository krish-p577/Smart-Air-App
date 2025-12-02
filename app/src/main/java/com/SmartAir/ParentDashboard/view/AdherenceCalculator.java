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

    public static void calculate(String childId, String scheduleType, int daysLookback, AdherenceCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1. Calculate the dynamic start date based on the toggle (7 or 30)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -daysLookback);
        Date lookbackDate = cal.getTime();

        db.collection("inhalerLogs").document(childId)
                .collection("controllerEntries")
                .whereGreaterThan("timestamp", new Timestamp(lookbackDate))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Date> logDates = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Timestamp ts = doc.getTimestamp("timestamp");
                        if (ts != null) logDates.add(ts.toDate());
                    }

                    processAdherenceMath(scheduleType, daysLookback, logDates, callback);


                    Log.i("Controller Logs", "Found Controller Logs here: ");

                });
    }

    private static void processAdherenceMath(String scheduleType, int daysLookback, List<Date> logs, AdherenceCallback callback) {
        int intervalDays = 1; // Default Daily

        switch (scheduleType) {
            case "Weekly": intervalDays = 7; break;
            case "Biweekly": intervalDays = 14; break; // Fortnightly
            case "Monthly": intervalDays = 30; break;
        }

        // 2. Determine how many buckets fit in the requested window
        // Example: Daily (1) in 30 days = 30 buckets.
        // Example: Weekly (7) in 30 days = 4 buckets.
        int bucketsToCheck = daysLookback / intervalDays;

        // Safety: If the window is shorter than the interval (e.g., 7 day view for 14 day schedule),
        // check at least 1 bucket so we don't divide by zero.
        if (bucketsToCheck < 1) bucketsToCheck = 1;

        int successfulBuckets = 0;
        boolean isCurrentBucketSuccess = false;
        Calendar cursor = Calendar.getInstance();

        for (int i = 0; i < bucketsToCheck; i++) {
            Date windowEnd = cursor.getTime();
            cursor.add(Calendar.DAY_OF_YEAR, -intervalDays);
            Date windowStart = cursor.getTime();

            boolean hit = false;
            for (Date logDate : logs) {
                if (logDate.after(windowStart) && logDate.before(windowEnd)) {
                    hit = true;
                    break;
                }
            }

            if (hit) {
                successfulBuckets++;
                if (i == 0) isCurrentBucketSuccess = true;
            }
        }

        // Avoid division by zero
        int percentage = (bucketsToCheck == 0) ? 0 : (int) (((float) successfulBuckets / bucketsToCheck) * 100);

        callback.onResult(percentage, isCurrentBucketSuccess);
    }
}