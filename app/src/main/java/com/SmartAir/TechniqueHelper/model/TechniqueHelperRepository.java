package com.SmartAir.TechniqueHelper.model;

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

public class TechniqueHelperRepository {

    private final FirebaseFirestore db;
    private final CurrentUser user;

    public TechniqueHelperRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.user = CurrentUser.getInstance();
    }

    public Task<Void> updateTechniqueStreak() {
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

                    data.put("techniqueStreak", 1);
                    data.put("techniqueStreakLastUpdated", currentTime);

                    return db.collection("streaks")
                        .document(childId)
                        .set(data, SetOptions.merge());
                }

                Timestamp lastUpdated = doc.getTimestamp("techniqueStreakLastUpdated");

                if (lastUpdated == null) {
                    return db.collection("streaks")
                        .document(childId)
                        .update(
                            "techniqueStreak", 1,
                            "techniqueStreakLastUpdated", currentTime
                        );
                }

                long daysSinceLastUpdate = (currentTime.toDate().getTime() - lastUpdated.toDate().getTime()) / (1000L * 60L * 60L * 24L);

                if (daysSinceLastUpdate < 1) {
                    return Tasks.forResult(null);
                }

                return db.collection("streaks")
                    .document(childId)
                    .update(
                        "techniqueStreak", FieldValue.increment(1),
                        "techniqueStreakLastUpdated", currentTime);
            });
    }

    public Task<Void> updatePerfectTechniqueSessions() {
        String childId = user.getUid();

        return db.collection("badges")
            .document(childId)
            .get()
            .continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (!doc.exists()) {
                    Map<String, Object> data = new HashMap<>();

                    data.put("perfectTechniqueSessions", 1);

                    return db.collection("badges")
                        .document(childId)
                        .set(data, SetOptions.merge());
                }

                return db.collection("badges")
                    .document(childId)
                    .update("perfectTechniqueSessions", FieldValue.increment(1));
            });
    }
}
