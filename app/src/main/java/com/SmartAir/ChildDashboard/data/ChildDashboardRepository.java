package com.SmartAir.ChildDashboard.data;

import com.SmartAir.onboarding.model.CurrentUser;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
                    String name = doc.getString("name");
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
