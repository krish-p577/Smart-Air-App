package com.SmartAir.LogController.model;

import com.SmartAir.onboarding.model.CurrentUser;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LogControllerRepository {

    private final FirebaseFirestore db;

    private final CurrentUser user;

    public LogControllerRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.user = CurrentUser.getInstance();
    }

    public Task<Void> logControllerInhalerUse(Map<String, Object> fieldsToLog) {
        String childId = user.getUid();

        return db.collection("inhalerLogs")
            .document(childId)
            .collection("controllerEntries")
            .add(fieldsToLog)
            .continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                return Tasks.forResult(null);
            });
    }

    public Task<Void> updateControllerStreak() {
        String childId = user.getUid();
        Timestamp currentTime = Timestamp.now();

        return db.collection("streaks")
            .document(childId)
            .get()
            .continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (!doc.exists()) {
                    Map<String, Object> data = new HashMap<>();

                    data.put("controllerStreak", 1);
                    data.put("controllerStreakLastUpdated", currentTime);

                    return db.collection("streaks")
                        .document(childId)
                        .set(data, SetOptions.merge());
                }

                Timestamp lastUpdated = doc.getTimestamp("controllerStreakLastUpdated");

                if (lastUpdated == null) {
                    return db.collection("streaks")
                        .document(childId)
                        .update(
                            "controllerStreak", 1,
                            "controllerStreakLastUpdated", currentTime
                        );
                }

                long daysSinceLastUpdate = (currentTime.toDate().getTime() - lastUpdated.toDate().getTime()) / (1000L * 60L * 60L * 24L);

                if (daysSinceLastUpdate < 1) {
                    return Tasks.forResult(null);
                }

                return db.collection("streaks")
                    .document(childId)
                    .update(
                        "controllerStreak", FieldValue.increment(1),
                        "controllerStreakLastUpdated", currentTime
                    );
            });
    }
}
