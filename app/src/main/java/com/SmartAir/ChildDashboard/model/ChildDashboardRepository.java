package com.SmartAir.ChildDashboard.model;

import com.SmartAir.onboarding.model.CurrentUser;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChildDashboardRepository {

    private final FirebaseFirestore db;

    private final CurrentUser user;

    public ChildDashboardRepository() {
        this.db = FirebaseFirestore.getInstance();
        this.user = CurrentUser.getInstance();
    }

    public boolean isChild() {
        String role = user.getRole();

        return Objects.equals(role, "child");
    }

    public Task<String> getChildName() {
        String childId = user.getUid();

        return db.collection("Users")
            .document(childId)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    String name = doc.getString("displayName");
                    if (name == null || name.trim().isEmpty()) {
                        throw new Exception("Child name missing.");
                    } else {
                        return name;
                    }
                } else {
                    throw new Exception("Child not found.");
                }
            });
    }

    public Task<Void> updateControllerStreak() {
        String childId = user.getUid();

        return db.collection("streaks")
                .document(childId)
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    DocumentSnapshot doc = task.getResult();

                    // If doc doesn't exist, create it once with streak = 0.
                    if (!doc.exists()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("controllerStreak", 0);
                        // DO NOT set controllerStreakLastUpdated here; that should only be
                        // set by the actual controller log code.
                        return db.collection("streaks")
                                .document(childId)
                                .set(data, SetOptions.merge());
                    }

                    Long controllerStreak = doc.getLong("controllerStreak");
                    if (controllerStreak == null) {
                        controllerStreak = 0L;
                    }

                    Timestamp lastUpdated = doc.getTimestamp("controllerStreakLastUpdated");

                    // If we've never logged a controller inhaler yet, nothing to reset.
                    if (lastUpdated == null) {
                        return Tasks.forResult(null);
                    }

                    Timestamp currentTime = Timestamp.now();
                    long timeSinceLastUpdateMs =
                            currentTime.toDate().getTime() - lastUpdated.toDate().getTime();
                    long twentyFourHoursMs = 24L * 60 * 60 * 1000;

                    if (timeSinceLastUpdateMs > twentyFourHoursMs && controllerStreak > 0) {
                        // More than 24h since last real controller log → reset streak to 0.
                        // IMPORTANT: do NOT change controllerStreakLastUpdated here.
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("controllerStreak", 0);
                        return db.collection("streaks")
                                .document(childId)
                                .update(updates);
                    }

                    // No reset needed.
                    return Tasks.forResult(null);
                });
    }

    public Task<Void> updateTechniqueStreak() {
        String childId = user.getUid();

        return db.collection("streaks")
                .document(childId)
                .get()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    DocumentSnapshot doc = task.getResult();

                    // Initialize doc once if missing.
                    if (!doc.exists()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("techniqueStreak", 0);
                        // Do NOT set techniqueStreakLastUpdated here.
                        return db.collection("streaks")
                                .document(childId)
                                .set(data, SetOptions.merge());
                    }

                    Long techniqueStreak = doc.getLong("techniqueStreak");
                    if (techniqueStreak == null) {
                        techniqueStreak = 0L;
                    }

                    Timestamp lastUpdated = doc.getTimestamp("techniqueStreakLastUpdated");

                    if (lastUpdated == null) {
                        // No technique session has ever been logged → nothing to reset.
                        return Tasks.forResult(null);
                    }

                    Timestamp currentTime = Timestamp.now();
                    long timeSinceLastUpdateMs =
                            currentTime.toDate().getTime() - lastUpdated.toDate().getTime();
                    long twentyFourHoursMs = 24L * 60 * 60 * 1000;

                    if (timeSinceLastUpdateMs > twentyFourHoursMs && techniqueStreak > 0) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("techniqueStreak", 0);
                        // Do NOT change techniqueStreakLastUpdated.
                        return db.collection("streaks")
                                .document(childId)
                                .update(updates);
                    }

                    return Tasks.forResult(null);
                });
    }

    public Task<Integer> getControllerStreak() {
        String childId = user.getUid();

        return db.collection("streaks")
            .document(childId)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    Long controllerStreak = doc.getLong("controllerStreak");
                    if (controllerStreak == null) {
                        throw new Exception("Controller streak not found.");
                    }

                    return controllerStreak.intValue();
                } else {
                    throw new Exception("Streaks not found.");
                }
            });
    }

    public Task<Integer> getTechniqueStreak() {
        String childId = user.getUid();

        return db.collection("streaks")
            .document(childId)
            .get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }

                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    Long techniqueStreak = doc.getLong("techniqueStreak");
                    if (techniqueStreak == null) {
                        throw new Exception("Technique streak not found.");
                    }

                    return techniqueStreak.intValue();
                } else {
                    throw new Exception("Streaks not found.");
                }
            });
    }
}
